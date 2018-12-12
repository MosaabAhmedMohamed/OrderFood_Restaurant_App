package com.example.mosaab.orderfoodserver.ViewHolder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.Category;
import com.example.mosaab.orderfoodserver.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView TextFillName;
    private MaterialEditText editName;
    private Button btnUpload,btnSelect;
    private  Toolbar toolbar;
    private FloatingActionButton fab;


    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference categories;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;


    private Category newCategory;
    private Uri saveImageUri;
    private final int Pick_Image_Request =71;

    private  DrawerLayout drawer;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        InitUI();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ShowDialog();
            }
        });

        if(Common.isConnectedToInternet(getApplicationContext()))
        {
            LoadMenu();
            Update_Token(FirebaseInstanceId.getInstance().getToken());
        }
        else {
            Toast.makeText(Home.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }



    }

    private void Update_Token(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens  =db.getReference("Tokens");
        Token data = new Token(token,true);
        tokens.child(Common.current_user.getPhone()).setValue(data);
    }

    private void InitUI()
    {
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        //Init firebase
        database = FirebaseDatabase.getInstance();
        categories=database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference("images");


        fab = (FloatingActionButton) findViewById(R.id.fab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set name for the user
        View headerView =navigationView.getHeaderView(0);
        TextFillName = headerView.findViewById(R.id.txtFullName);
        TextFillName.setText(Common.current_user.getName());

        //init recycler
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        Log.d("Inittt", "InitUI: "+Common.current_user.getPhone());
    }

    private void ShowDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this,R.style.MyDialogTheme);

        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        editName = add_menu_layout.findViewById(R.id.edtName);
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

                if(newCategory!=null)
                {
                    categories.push().setValue(newCategory);
                    Snackbar.make(drawer,"New category"+newCategory.getName()+"was added",Snackbar.LENGTH_SHORT).show();
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

    private void UploadImage() {

        if(saveImageUri != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();

            final StorageReference imageFolder =storageReference.child("images/"+imageName);

            imageFolder.putFile(saveImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                 mDialog.dismiss();
                    Toast.makeText(Home.this,"Uploaded !",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCategory  = new Category(editName.getText().toString(),uri.toString());

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Pick_Image_Request && resultCode==RESULT_OK
                && data!=null &&data.getData()!=null)
        {
            saveImageUri =data.getData();
            btnSelect.setText("Image Selected !");
        }

    }

    private void ChooseImage()
    {
        Intent pick_iamge_intent =new Intent();
        pick_iamge_intent.setType("image/*");
        pick_iamge_intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(pick_iamge_intent,"Select Picture"),Pick_Image_Request);
        }
    }

    private void LoadMenu() {
        adapter =new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                categories)
        {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, final int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(Home.this).load(model.getLink()).into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int postion, boolean isLingClick) {

                        //Send Category id about specific type of food and start new activity
                        Intent food_list_Intent = new Intent(Home.this,Food_List.class);
                        food_list_Intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(food_list_Intent);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders)
        {
            Intent Orders_intent = new Intent(Home.this, Order_Status.class);
            startActivity(Orders_intent);
        }
        else if(id == R.id.nav_contact_us)
        {
            Contact_Us();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Contact_Us() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"mosabahmeddev@gmail.com"});
        try {
            startActivity(Intent.createChooser(intent, "E-mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            //do something else
        }
    }

    //Update & delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpadteDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {

        DatabaseReference  foods=database.getReference("Foods");
        Query foodInCategory = foods.orderByChild("MenuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot :dataSnapshot.getChildren())
                {
                    postSnapShot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        categories.child(key).removeValue();

        Toast.makeText(this, "Item Deleted !!", Toast.LENGTH_SHORT).show();
    }

    private void showUpadteDialog(final String key, final Category item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this,R.style.MyDialogTheme);

        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        final LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        editName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        editName.setText(item.getName());

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

                //Update Information

                item.setName(editName.getText().toString());
                categories.child(key).setValue(item);


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

    private void ChangeImage(final Category item) {

        if(saveImageUri != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();

            final StorageReference imageFolder =storageReference.child("images/"+imageName);
            imageFolder.putFile(saveImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(Home.this,"Uploaded !",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setLink(uri.toString());

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
