package com.example.mosaab.orderfoodserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;

public class Shipper_Adapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView shipper_name,shipper_phone;
    public Button shipper_remove_btn,shipper_edit_btn;
    private ItemClickListner itemClickListner;

    public Shipper_Adapter(@NonNull View itemView) {
        super(itemView);

        shipper_name = itemView.findViewById(R.id.shipper_name);
        shipper_phone = itemView.findViewById(R.id.shipper_phone);
        shipper_edit_btn = itemView.findViewById(R.id.edit_Bu);
        shipper_remove_btn = itemView.findViewById(R.id.remove_Bu);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);
    }
}
