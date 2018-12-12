package com.example.mosaab.orderfoodserver.ViewHolder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.Remote.API_Service;
import com.example.mosaab.orderfoodserver.model.Notification_;
import com.example.mosaab.orderfoodserver.model.Request;
import com.example.mosaab.orderfoodserver.model.Response;
import com.example.mosaab.orderfoodserver.model.Sender;
import com.example.mosaab.orderfoodserver.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;

public class Order_Status extends AppCompatActivity {

    public static final String TAG ="Order_Status_Activity";
    public static final int Error_Dialog_request =9001;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialSpinner spinner;

    private FirebaseRecyclerAdapter<Request,Order_Status_ViewHolder> adpater;
    private FirebaseDatabase db;
    private DatabaseReference requests;

    private API_Service api_service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__status);

        db = FirebaseDatabase.getInstance();
        requests =db.getReference("Requests");

        api_service =Common.Get_FCMClint();

        recyclerView = findViewById(R.id.ListOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadOrders();
    }

    private void loadOrders() {

        adpater = new FirebaseRecyclerAdapter<Request,Order_Status_ViewHolder>(
                Request.class,
                R.layout.order_layout,
                Order_Status_ViewHolder.class,
                requests)
        {


            @Override
            protected void populateViewHolder(Order_Status_ViewHolder viewHolder, final Request model, final int position) {
                  viewHolder.txtOrderId.setText(adpater.getRef(position).getKey());
                  viewHolder.txtOrderStauts.setText(Common.convertCodeToString(model.getStatus()));
                  viewHolder.txtOrderAddress.setText(model.getAdress());
                  viewHolder.txtOrderPhone.setText(model.getPhone());

                  viewHolder.Detail_BU.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Intent order_detail_intent = new Intent(Order_Status.this,Order_Detial.class);
                          Common.currentRequest = model;
                          order_detail_intent.putExtra("OrderId",adpater.getRef(position).getKey());
                          startActivity(order_detail_intent);
                      }
                  });

                  viewHolder.Remove_BU.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          deleteOrder(adpater.getRef(position).getKey());
                      }
                  });

                  viewHolder.Edit_Bu.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ShowUpdateDialog(adpater.getRef(position).getKey(), adpater.getItem(position));

                      }
                  });

                  viewHolder.Derication_BU.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          if (isServiceSDk())
                          {
                              Intent Tracking_order_intent = new Intent(Order_Status.this, TrackingOrder.class);
                              Common.currentRequest = model;
                              startActivity(Tracking_order_intent);
                          }
                      }
                  });

    }
        };

        adpater.notifyDataSetChanged();
        recyclerView.setAdapter(adpater);
    }


    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adpater.notifyDataSetChanged();
    }

    private void ShowUpdateDialog(String key, final Request item) {

        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Order_Status.this,R.style.MyDialogTheme);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose status");

        final LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);

        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my way","Shipped");

        alertDialog.setView(view);

        final String localKey = key;

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);

                adpater.notifyDataSetChanged();
                Send_Order_Status_ToUSer(localKey,item);
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

    private void Send_Order_Status_ToUSer(final String localKey, Request item) {

        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot postSnapshot :dataSnapshot.getChildren())
                        {
                            Token token =postSnapshot.getValue(Token.class);

                            //make raw payload
                            Notification_ notification = new Notification_("Order Food","Your Order"+localKey+"was updated");
                            Sender content = new Sender(token.getToken(),notification);

                            api_service.sendNotification(content).enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                                    if(response.body().success == 1)
                                    {
                                        Toast.makeText(Order_Status.this, "order was updated !", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(Order_Status.this, "order was updated but failed to send notification !", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                    Log.e("ERROR", "onFailure: "+t.getMessage() );
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    //to know if this device is allowed to access map or not
    public boolean isServiceSDk()
    {
        Log.d(TAG, "isServiceOk:checking google services version ");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Order_Status.this);

        if (available == ConnectionResult.SUCCESS)
        {
            //everything is fine and the user can make map requests

            Log.d(TAG, "isServiceOk: Google play serviecs is working ");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //an error occured but we can fix it

            Log.d(TAG, "isServiceOk: an error occured but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Order_Status.this,available,Error_Dialog_request);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "you cant map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
