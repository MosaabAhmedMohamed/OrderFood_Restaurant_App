package com.example.mosaab.orderfoodserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Interfaces.ItemClickListner;
import com.example.mosaab.orderfoodserver.R;


public class Food_List_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener  {


    public TextView  food_Name;
    public ImageView food_image;

    private ItemClickListner itemClickListner;

    public Food_List_ViewHolder(@NonNull View itemView)
    {
        super(itemView);

        food_Name = itemView.findViewById(R.id.food_name);
        food_image =itemView.findViewById(R.id.food_iamge);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner)
    {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the Action");


        menu.add(0,0,getAdapterPosition(),Common.UPDATE);
        menu.add(0,1,getAdapterPosition(),Common.DELETE);


    }
}
