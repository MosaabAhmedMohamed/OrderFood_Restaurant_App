package com.example.mosaab.orderfoodserver.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.Interfaces.OnItemClickListnrer;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.Banner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter
{
    private static final String TAG = "ViewPagerAdapter";
    private Context context;
    private ArrayList<Banner> banner_Url_list;
    private ImageView Slide_image;
    private Button edit_banner;
    private TextView food_name_tv;
    private OnItemClickListnrer onItemClickListnrer;

    ViewPagerAdapter(Context context, ArrayList<Banner> banner_Url_list) {
        this.context = context;
        this.banner_Url_list = banner_Url_list;
    }

    @Override
    public int getCount() {
        return banner_Url_list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater=(LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.banner,container,false);

        edit_banner =itemView.findViewById(R.id.banner_edit_btn);
        edit_banner.setVisibility(View.VISIBLE);
        Slide_image = itemView.findViewById(R.id.slide_iamge);
        food_name_tv = itemView.findViewById(R.id.banner_foodName);
        food_name_tv.setText(banner_Url_list.get(position).getName());

        Log.d(TAG, "instantiateItem: "+ banner_Url_list.size());
        Picasso.get()
                .load(banner_Url_list.get(position).getImage())
                .fit()
                .centerCrop()
                .into(Slide_image);


        edit_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListnrer != null) {
                   onItemClickListnrer.onItemClick(view, position);
                }
            }
        });

        container.addView(itemView);
        return itemView;

    }

    public void setOnItemClickListner(OnItemClickListnrer onitem_clicklistner)
    {
             onItemClickListnrer = onitem_clicklistner;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }



}
