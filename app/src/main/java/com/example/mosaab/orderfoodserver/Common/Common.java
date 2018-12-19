package com.example.mosaab.orderfoodserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.mosaab.orderfoodserver.Remote.API_Service;
import com.example.mosaab.orderfoodserver.Remote.FCMRetrofitClient;
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


}
