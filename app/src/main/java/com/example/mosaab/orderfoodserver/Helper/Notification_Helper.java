package com.example.mosaab.orderfoodserver.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.mosaab.orderfoodserver.R;

public class Notification_Helper extends ContextWrapper
{
    private static final String NotificationChannel_ID = "com.example.mosaab.orderfoods";
    private static final String Notification_Channel_Name = "Order Food";

    private NotificationManager manager ;

    public Notification_Helper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Create_Channel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void Create_Channel() {
        NotificationChannel notificationChannel = new NotificationChannel(NotificationChannel_ID,Notification_Channel_Name,NotificationManager.IMPORTANCE_DEFAULT);

        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager()
    {
        if(manager == null)
        {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),NotificationChannel_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.logo)
                .setSound(soundUri)
                .setContentInfo("Info")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false);

    }
}
