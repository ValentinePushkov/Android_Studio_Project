package com.example.courseproject_new.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.courseproject_new.R;

public class MyReceiver {

        public void onReceive(Context context, Intent intent, String title,String description ) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "valson")
                    .setSmallIcon(R.drawable.ic_launcher_background).setContentTitle(title)
                    .setContentText(description).setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
        }


}
