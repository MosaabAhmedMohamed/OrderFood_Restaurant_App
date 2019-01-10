package com.example.mosaab.orderfoodserver.Common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.mosaab.orderfoodserver.Remote.API_Service;
import com.example.mosaab.orderfoodserver.Remote.FCMRetrofitClient;
import com.example.mosaab.orderfoodserver.ViewHolder.Home;
import com.example.mosaab.orderfoodserver.model.Request;
import com.example.mosaab.orderfoodserver.model.User;

import java.util.Locale;

public class Common {

    public static User current_user;
    public static Request currentRequest;
    public static String PHONE_TEXT ="userPhone";
    public static final String UPDATE ="Update";
    public static final String DELETE ="Delete";

    public static final String USER_KEY ="User";
    public static final String PWD_KEY ="Password";
    public static final String SHIPPERS_TABLE = "shippers";

    public static final String BASE_URL = "https://fcm.googleapis.com/";


    public static boolean  isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for (int i= 0;i<info.length;i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }

            }
        }
        return false;
    }

    public static String convertCodeToString(String code)
    {
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static API_Service Get_FCMClint()
    {
        return FCMRetrofitClient.getClient(BASE_URL).create(API_Service.class);
    }

    public static String getDate(Long time)
    {
        java.util.Calendar calendar = java.util.Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yy HH:mm",calendar).toString());
        return date.toString();
    }


    public static boolean READ_EXTRNAL_STORAGE (Activity context,int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
    {

        if (Build.VERSION.SDK_INT >= 23){
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale( context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                 return false;
                }
                else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions( context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                }
            }

        }
        else
        {
            return true;
        }
        return false;
    }




}
