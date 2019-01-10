package com.example.mosaab.orderfoodserver.ViewHolder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.Foods;
import com.example.mosaab.orderfoodserver.model.Shipper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagment extends AppCompatActivity {

    private FloatingActionButton fab ;


    private FirebaseDatabase db ;
    private DatabaseReference shippers;

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Shipper,Shipper_Adapter> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_managment);

        InitUi();
    }

    private void InitUi() {

        fab = findViewById(R.id.fab_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Show_create_shipper();
            }
        });
        
        recyclerView = findViewById(R.id.recycler_shipper);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        db = FirebaseDatabase.getInstance();
        shippers = db.getReference(Common.SHIPPERS_TABLE);
        
        Load_All_Shippers();


    }

    private void Load_All_Shippers() {



            FirebaseRecyclerOptions options =new FirebaseRecyclerOptions.Builder<Shipper>()
                    .setQuery(shippers,Shipper.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<Shipper,Shipper_Adapter>(options)
            {

                @NonNull
                @Override
                public Shipper_Adapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View item_view  = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.single_shipper_mangment,viewGroup,false);

                    return new Shipper_Adapter(item_view);
                }

                @Override
                protected void onBindViewHolder(@NonNull Shipper_Adapter  viewHolder, final int position, @NonNull final Shipper model)
                {
                    viewHolder.shipper_name.setText(model.getName());
                    viewHolder.shipper_phone.setText(model.getPhone());

                    viewHolder.shipper_edit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Show_Edit_Shipper_Dialog(adapter.getRef(position).getKey(),model);
                        }
                    });

                    viewHolder.shipper_remove_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Remove_Shipper(adapter.getRef(position).getKey());
                        }
                    });
                }



            };
            adapter.startListening();
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }

    private void Show_Edit_Shipper_Dialog(String key,Shipper model) {

        AlertDialog.Builder create_shipper_dialog = new AlertDialog.Builder(ShipperManagment.this,R.style.MyDialogTheme);

        create_shipper_dialog.setTitle("Edit Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout,null);

        final EditText edit_name,edit_phone,edit_password;

        edit_name = view.findViewById(R.id.edtName_shipper);
        edit_phone = view.findViewById(R.id.edtPhone_shipper);
        edit_password = view.findViewById(R.id.edtPassword_shipper);

        edit_name.setText(model.getName());
        edit_phone.setText(model.getPhone());
        edit_password.setText(model.getPassword());

        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        create_shipper_dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Map <String,Object> update = new HashMap<>();

                update.put("name",edit_name.getText().toString());
                update.put("phone",edit_phone.getText().toString());
                update.put("password",edit_password.getText().toString());



                shippers.child(edit_phone.getText().toString())
                        .setValue(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagment.this, "Shipper updated" , Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ShipperManagment.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        create_shipper_dialog.show();
    }

    private void Remove_Shipper(String key) {

        shippers.child(key).
                removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagment.this, "Shipper Deleted ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ShipperManagment.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void Show_create_shipper() {

        AlertDialog.Builder create_shipper_dialog = new AlertDialog.Builder(ShipperManagment.this,R.style.MyDialogTheme);

        create_shipper_dialog.setTitle("Create Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout,null);

        final EditText edit_name,edit_phone,edit_password;

        edit_name = view.findViewById(R.id.edtName_shipper);
        edit_phone = view.findViewById(R.id.edtPhone_shipper);
        edit_password = view.findViewById(R.id.edtPassword_shipper);

        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        create_shipper_dialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Shipper shipper =new Shipper();

                shipper.setName(edit_name.getText().toString());
                shipper.setPassword(edit_password.getText().toString());
                shipper.setPhone(edit_phone.getText().toString());

                shippers.child(edit_phone.getText().toString())
                        .setValue(shipper)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagment.this, "Shipper Created" , Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ShipperManagment.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        create_shipper_dialog.show();
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
