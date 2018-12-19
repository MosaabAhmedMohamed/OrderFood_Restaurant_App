package com.example.mosaab.orderfoodserver.ViewHolder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.Foods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Food_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    private ConstraintLayout rootLayout;

    //firebase
    private FirebaseDatabase database;
    private DatabaseReference foodList_table;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private String CategoryId ="";
    private FirebaseRecyclerAdapter <Foods,Food_List_ViewHolder> adapter;

    private EditText edt_name,edt_Description,edt_price,edt_Discount;
    private Button btnSelect,btnUpload;

    private final int Pick_Image_Request =71;
    private Uri saveImageUri;

    private Foods newFoods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__list);


        InitUI();
        check_internet_connection();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAddFoodDialog();
            }
        });

    }

    private void InitUI()
    {
        rootLayout = findViewById(R.id.root_layout);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList_table =database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init recycler
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab = findViewById(R.id.fab);
    }

    private void check_internet_connection()
    {
        if(Common.isConnectedToInternet(getApplicationContext()))
        {
            if(getIntent()!=null)
            {
                CategoryId =getIntent().getStringExtra("CategoryId");
                if(!CategoryId.isEmpty()) {
                    loadListFood(CategoryId);
                }
            }
        }
        else
        {
            Toast.makeText(Food_List.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void ShowAddFoodDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Food_List.this,R.style.MyDialogTheme);

        alertDialog.setTitle("Add new Foods");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food,null);

        edt_name = add_menu_layout.findViewById(R.id.edtName);
        edt_Description = add_menu_layout.findViewById(R.id.edtDescription);
        edt_price = add_menu_layout.findViewById(R.id.edtPrice);
        edt_Discount = add_menu_layout.findViewById(R.id.edtDiscount);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();//let user select image to upload it to firesbase
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if(newFoods !=null)
                {
                    foodList_table.push().setValue(newFoods);
                    Snackbar.make(rootLayout,"New Foods "+ newFoods.getName()+"was addes",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void loadListFood(String categoryId) {

        //to filter search
        Query Category = foodList_table.orderByChild("MenuId").equalTo(categoryId.toString());

        FirebaseRecyclerOptions options =new FirebaseRecyclerOptions.Builder<Foods>()
                .setQuery(Category,Foods.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Foods, Food_List_ViewHolder>(options)
        {
            @NonNull
            @Override
            public Food_List_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View item_view  = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.food_item,viewGroup,false);

                return new Food_List_ViewHolder(item_view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Food_List_ViewHolder viewHolder, int position, @NonNull Foods model) {
                viewHolder.food_Name.setText(model.getName());

                Picasso.get().load(model.getImage())
                        .into(viewHolder.food_image);

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {


                    }
                });
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    private void UploadImage() {

        if(saveImageUri != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();
            final StorageReference imageFolder =storageReference.child("iamges/"+imageName);
            imageFolder.putFile(saveImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(Food_List.this,"Uploaded !",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newFoods = new Foods();
                            newFoods.setName(edt_name.getText().toString());
                            newFoods.setDescription(edt_Description.getText().toString());
                            newFoods.setPrice(edt_price.getText().toString());
                            newFoods.setDiscount(edt_Discount.getText().toString());
                            newFoods.setMenuId(CategoryId);
                            newFoods.setImage(uri.toString());


                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Food_List.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");
                        }
                    });
        }


    }

    private void ChooseImage() {

        Intent pick_iamge_intent =new Intent();
        pick_iamge_intent.setType("image/*");
        pick_iamge_intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(pick_iamge_intent,"Select Picture"),Pick_Image_Request);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Pick_Image_Request && resultCode==RESULT_OK
                && data!=null &&data.getData()!=null)
        {
            saveImageUri =data.getData();
            btnSelect.setText("Image Selected !");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE))
        {
           ShowUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            DeleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void DeleteFood(String key) {
    foodList_table.child(key).removeValue();
    }

    private void ShowUpdateFoodDialog(final String key, final Foods item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Food_List.this);

        alertDialog.setTitle("Edit Foods");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food,null);

        edt_name = add_menu_layout.findViewById(R.id.edtName);
        edt_Description = add_menu_layout.findViewById(R.id.edtDescription);
        edt_price = add_menu_layout.findViewById(R.id.edtPrice);
        edt_Discount = add_menu_layout.findViewById(R.id.edtDiscount);

        edt_name.setText(item.getName());
        edt_Discount.setText(item.getDiscount());
        edt_price.setText(item.getPrice());
        edt_Description.setText(item.getDescription());


        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();//let user select image to upload it to firesbase
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


                    item.setImage(edt_name.getText().toString());
                    item.setPrice(edt_price.getText().toString());
                    item.setDiscount(edt_Discount.getText().toString());
                    item.setDescription(edt_Description.getText().toString());

                    foodList_table.child(key).setValue(item);

                    Snackbar.make(rootLayout,"category "+item.getName()+"was edited",Snackbar.LENGTH_SHORT).show();


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void ChangeImage(final Foods item) {

        if(saveImageUri != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();
            final StorageReference imageFolder =storageReference.child("iamges/"+imageName);
            imageFolder.putFile(saveImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(Food_List.this,"Uploaded !",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Food_List.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");
                        }
                    });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
        {
            adapter.startListening();
        }
    }
}
