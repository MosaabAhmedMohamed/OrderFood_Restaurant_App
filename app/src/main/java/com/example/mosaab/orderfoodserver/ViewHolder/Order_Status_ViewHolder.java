package com.example.mosaab.orderfoodserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;

public class Order_Status_ViewHolder extends RecyclerView.ViewHolder  {

    public TextView txtOrderId, txtOrderStauts, txtOrderPhone, txtOrderAddress;
    public Button Edit_Bu,Remove_BU,Detail_BU,Derication_BU;



    public Order_Status_ViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = itemView.findViewById(R.id.Order_address);
        txtOrderId = itemView.findViewById(R.id.order_name);
        txtOrderStauts = itemView.findViewById(R.id.Order_status);
        txtOrderPhone = itemView.findViewById(R.id.Order_Phone);

        Edit_Bu = itemView.findViewById(R.id.edit_Bu);
        Detail_BU = itemView.findViewById(R.id.detail_BU);
        Remove_BU = itemView.findViewById(R.id.remove_Bu);
        Derication_BU = itemView.findViewById(R.id.direction_BU);

    }




}

