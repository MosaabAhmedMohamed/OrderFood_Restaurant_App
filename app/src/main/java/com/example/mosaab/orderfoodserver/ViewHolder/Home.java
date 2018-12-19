package com.example.mosaab.orderfoodserver.ViewHolder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.Interfaces.OnItemClickListnrer;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.Banner;
import com.example.mosaab.orderfoodserver.model.Category;
import com.example.mosaab.orderfoodserver.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnItemClickListnrer
{

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =900 ;
    private static final String TAG ="Home" ;
    private TextView TextFillName;
    private EditText editName,banner_newFood_name,banner_newFood_ID;
    private Button btnUpload,btnSelect,update_banner_btn,delete_banner_btn;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ViewPager viewPager;
    private RecyclerView recycler_menu;
    private DotsIndicator dotsIndicator;
    private LinearLayout delete_update_layout;



    private FirebaseDatabase database;
    private DatabaseReference categories_table,Banners_Table;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    private ViewPagerAdapter viewPagerAdapter;

    private ArrayList<Banner> banner_Url_list;
    ArrayList <Banner> banners_ID;
    ArrayList <String> banners_Key;

    private Banner newBanner;
    private Category newCategory;
    private Uri image_file_path;

    private final int Pick_Image_Request =71;
    private String Banners_KEY = null;

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
                ShowDialog_Menu();
            }
        });

        if(Common.isConnectedToInternet(getApplicationContext()))
        {   Setup_Slider();
            LoadMenu();
            Update_Token(FirebaseInstanceId.getInstance().getToken());
        }
        else {
            Toast.makeText(Home.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }



    }

    private void getKeyBanners()
    {
        if (banners_ID.size() != 0 && banners_Key.size() != 0)
        {
            banners_ID.clear();
            banners_Key.clear();
        }
        Banners_Table.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot: snapshot.getChildren()) {

                    banners_ID.add((objSnapshot.getValue(Banner.class)));
                    banners_Key.add((objSnapshot.getKey()));
                    }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("Read failed", firebaseError.getMessage());
            }
        });
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
        categories_table =database.getReference("Category");
        Banners_Table =database.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference("images");


        Paper.init(this);

        //image slider
        viewPager = findViewById(R.id.banner_viewPager);
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
        banners_ID = new ArrayList<>();
        banners_Key = new ArrayList<>();
        update_banner_btn = findViewById(R.id.Update_banner_btn);
        delete_banner_btn = findViewById(R.id.Delete_banner_btn);
        delete_update_layout = findViewById(R.id.update_delte_layout);


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
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));
        Log.d(TAG, "InitUI: "+Common.current_user.getPhone());
    }

    private void ShowDialog_Menu() {
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
                UploadImage_Menu();
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
                    categories_table.push().setValue(newCategory);
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

    private void UploadImage_Menu() {

        if(image_file_path != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();

            final StorageReference imageFolder =storageReference.child("images/"+imageName);

            imageFolder.putFile(image_file_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                         image_file_path = null;
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
            image_file_path =data.getData();
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

    private void Setup_Slider()
    {
        banner_Url_list = new ArrayList<>();

        final DatabaseReference Banners = database.getReference("Banner");

        Banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren())
                {
                    Banner banner = postSnapShot.getValue(Banner.class);

                    banner_Url_list.add(new Banner(banner.getId(),banner.getName(),banner.getImage()));
                    //  imageUrls.put(banner.getName()+"_"+banner.getId(),banner.getImage());
                }

                Init_ViewPager();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Home.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });



    }

    private void Init_ViewPager() {

        viewPagerAdapter= new ViewPagerAdapter(Home.this, banner_Url_list);
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.setOnItemClickListner(Home.this);
        if (banner_Url_list.size() != 0)
        { dotsIndicator.setViewPager(viewPager); }
        getKeyBanners();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                delete_update_layout.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });


    }

    protected void Add_Banner()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this,R.style.MyDialogTheme);

        alertDialog.setTitle("Add new Banner");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_banner,null);

        banner_newFood_name = add_menu_layout.findViewById(R.id.banner_newfood_name);
        banner_newFood_ID = add_menu_layout.findViewById(R.id.banner_newfood_id);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Home.this, "ads", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 23){
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(Home.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(Home.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }else{
                        ActivityCompat.requestPermissions(Home.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }else {

                  ChooseImage();
                }






            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image_file_path != null)
                {
                    UploadImage_Banner();
                }
            }
        });

        alertDialog.setView(add_menu_layout);

        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if ( newBanner != null)
                {
                    banner_Url_list.clear();
                    viewPagerAdapter.notifyDataSetChanged();
                    Banners_Table.push().setValue(newBanner);
                }
            }
        });

        alertDialog.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           dialog.dismiss();
           newBanner = null ;
            }
        });

        alertDialog.show();

    }



    private void UploadImage_Banner() {

        if(image_file_path != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();

            final StorageReference imageFolder =storageReference.child("images/"+imageName);

            imageFolder.putFile(image_file_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(Home.this,"Uploaded !",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newBanner = new Banner();
                            newBanner.setName(banner_newFood_name.getText().toString());
                            newBanner.setId(banner_newFood_ID.getText().toString());
                            newBanner.setImage(uri.toString());
                            Log.d(TAG, "onSuccess: "+uri.toString());

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
                            image_file_path = null;
                        }
                    });
        }


    }

    private void Upadte_Banner(final String key, final Banner item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this,R.style.MyDialogTheme);

        alertDialog.setTitle("Update Banner");
        alertDialog.setMessage("Please fill full information");

        final LayoutInflater inflater =this.getLayoutInflater();
        View edit_banner = inflater.inflate(R.layout.add_new_banner,null);


        banner_newFood_name = edit_banner.findViewById(R.id.banner_newfood_name);
        banner_newFood_ID = edit_banner.findViewById(R.id.banner_newfood_id);

        btnSelect = edit_banner.findViewById(R.id.btnSelect);
        btnUpload = edit_banner.findViewById(R.id.btnUpload);

        banner_newFood_ID.setText(item.getId());
        banner_newFood_name.setText(item.getName());

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();//let user select image to upload it to firesbase
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeImage_Banner(item);
            }
        });

        alertDialog.setView(edit_banner);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Update Information

                item.setName(banner_newFood_name.getText().toString());
                item.setId(banner_newFood_ID.getText().toString());


                Map <String,Object> update = new HashMap<>();
                update.put("name",item.getName());
                update.put("id",item.getId());
                update.put("image",item.getImage());



                Banners_Table.child(key)
                        .updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "Food "+item.getName()+" was edited", Toast.LENGTH_SHORT).show();
                            }
                        });

                if (banner_Url_list.size()!=0)
                {
                    banner_Url_list.clear();
                }
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ChooseImage();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void LoadMenu() {

        FirebaseRecyclerOptions options =new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categories_table,Category.class)
                .build();

        adapter =new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options)
        {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item,viewGroup,false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.get().load(model.getLink()).into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {

                        //Send Category id about specific type of food and start new activity
                        Intent food_list_Intent = new Intent(Home.this,Food_List.class);
                        food_list_Intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(food_list_Intent);

                    }
                });
            }
        };
        adapter.startListening();
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
        else if(id == R.id.nav_add_banner)
        {
            Add_Banner();
        }
        else if(id == R.id.nav_contact_us)
        {
            Contact_Us();
        }
        else if(id == R.id.nav_sign_out)
        {
            Paper.book().destroy();

            Intent SignIn = new Intent(Home.this,SignIn.class);
            SignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(SignIn);
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

        categories_table.child(key).removeValue();

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
                categories_table.child(key).setValue(item);


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

    private void ChangeImage(final Category item ) {

        if(image_file_path != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();

            final StorageReference imageFolder =storageReference.child("images/"+imageName);
            imageFolder.putFile(image_file_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    private void ChangeImage_Banner(final Banner item ) {

        if(image_file_path != null)
        {
            final ProgressDialog mDialog =new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName =UUID.randomUUID().toString();

            final StorageReference imageFolder =storageReference.child("images/"+imageName);
            imageFolder.putFile(image_file_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(Home.this,"Uploaded !",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(View view, final int position)
    {

        Update_Banner(position);


        Delete_Banner(position);

    }

    private void Update_Banner(final int position) {

        delete_update_layout.setVisibility(View.VISIBLE);


        update_banner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_update_layout.setVisibility(View.GONE);

                if (banners_ID.get(position).getId().equals(banner_Url_list.get(position).getId())) {

                    Banners_KEY = banners_Key.get(position);

                    if (!TextUtils.isEmpty(Banners_KEY))
                    {
                        Upadte_Banner(Banners_KEY,banner_Url_list.get(position));
                    }

                }
            }
        });
    }

    private void Delete_Banner(final int position)
    {
        delete_banner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                delete_update_layout.setVisibility(View.GONE);

                if (banners_ID.get(position).getId().equals(banner_Url_list.get(position).getId())) {

                    Banners_KEY = banners_Key.get(position);

                    if (!TextUtils.isEmpty(Banners_KEY))
                    {
                        Log.d(TAG, "onItemClick: " + Banners_KEY);
                        Banners_Table.child(Banners_KEY).removeValue();

                        if(banner_Url_list.size() != 0)
                        {
                            banner_Url_list.clear();
                            viewPagerAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }
        });

    }
}
