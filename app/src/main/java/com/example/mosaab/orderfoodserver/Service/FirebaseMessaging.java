package com.example.mosaab.orderfoodserver.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.Helper.Notification_Helper;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.ViewHolder.MainActivity;
import com.example.mosaab.orderfoodserver.ViewHolder.Order_Detial;
import com.example.mosaab.orderfoodserver.model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            send_Notification_API26(remoteMessage);
        }
        else
        {
            sendNotification(remoteMessage);
        }
    }


    @Override
    public void onNewToken(String token_refreshed) {
        super.onNewToken(token_refreshed);
        if(Common.current_user != null)
        {
            Update_token_ToFirebase(token_refreshed);
        }

    }

    private void Update_token_ToFirebase(String token_refreshed) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token =new Token(token_refreshed,false);//false becuse this token send from client app
        tokens.child(Common.current_user.getPhone()).setValue(token);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void send_Notification_API26(RemoteMessage remoteMessage)
    {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String content = notification.getBody();

        Intent intent = new Intent(this,Order_Detial.class);
        //intent.putExtra(Common.PHONE_TEXT,Common.currntUser.getPhone());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification_Helper notification_helper = new Notification_Helper(this);
        Notification.Builder builder = notification_helper.getChannelNotification(title,content,pendingIntent,defaultSoundUri);

        //get random id for notification to show all notification
        notification_helper.getManager().notify(new Random().nextInt(),builder.build());
    }



    private void sendNotification(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification =remoteMessage.getNotification();

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setContentIntent(pendingIntent);
        NotificationManager notifiy = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifiy.notify(0,builder.build());
    }
}
