package com.example.medicinereminderqrcode;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

public class MyAlarmBroadCast extends BroadcastReceiver {

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        createNotificationChannel(context);
        notificationManager.notify(1,createNotification(context,bundle.getString("title"),bundle.getString("text")));
        Intent intentBack = new Intent(context, MainActivity.class);
        intentBack.putExtra("username",bundle.getString("username"));
        intentBack.putExtra("status","notify");
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intentBack);
    }


    private Notification createNotification(Context context,String title,String text){
        NotificationCompat.Builder Builder = new NotificationCompat.Builder(context, "ic")
                .setSmallIcon(R.drawable.ic_medicine)
                .setContentTitle(title.toUpperCase())
                .setContentText(text)
                .setTicker(text)
                .setAutoCancel(true)
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setChannelId("ic");
        //Log.i("xxx", "createNotification: "+title);
        return Builder.build();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "important channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ic", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
