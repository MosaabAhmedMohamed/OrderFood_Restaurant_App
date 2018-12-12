package com.example.mosaab.orderfoodserver.ViewHolder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.R;

public class Order_Detial extends AppCompatActivity {

    private TextView order_id,order_phone,order_address,order_total,order_comment;
    private String order_id_value = "";
    private RecyclerView listFoods;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__detial);

        Init_UI();
        if(getIntent() != null)
        {
            order_id_value = getIntent().getStringExtra("OrderId");

            setting_UI();
        }
    }


    private void Init_UI() {
        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.Order_Phone);
        order_address = findViewById(R.id.Order_address);
        order_total = findViewById(R.id.Order_total);
        order_comment = findViewById(R.id.Order_Comment);

        listFoods = findViewById(R.id.listFoods);
        listFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listFoods.setLayoutManager(layoutManager);


    }

    private void setting_UI() {

        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getToatl());
        order_address.setText(Common.currentRequest.getAdress());
        order_comment.setText(Common.currentRequest.getComment());


        order_detail_adapter adapter = new order_detail_adapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        listFoods.setAdapter(adapter);
    }

}
