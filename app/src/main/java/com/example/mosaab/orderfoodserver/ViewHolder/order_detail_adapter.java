package com.example.mosaab.orderfoodserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.order;

import java.util.List;



public class order_detail_adapter extends RecyclerView.Adapter<order_detail_adapter.Order_Detail_ViewHolder> {

    List<order> myOrders;

    public order_detail_adapter(List<order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public Order_Detail_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item_view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_detail_layout,viewGroup,false);

        return new Order_Detail_ViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_Detail_ViewHolder order_detail_viewHolder, int i) {
       order order =myOrders.get(i);

        order_detail_viewHolder.name.setText(String.format("Name : %s",order.getProductName()));
        order_detail_viewHolder.quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        order_detail_viewHolder.price.setText(String.format("Name : %s",order.getPrice()));
        order_detail_viewHolder.discount.setText(String.format("Discount : %s",order.getDiscount()));


    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }

    class Order_Detail_ViewHolder extends RecyclerView.ViewHolder{

        public TextView name,quantity,price,discount;

        public Order_Detail_ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.product_quantity);
            price = itemView.findViewById(R.id.product_price);
            discount = itemView.findViewById(R.id.product_discount);
        }
    }
}
