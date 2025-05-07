package com.aki.claimreporting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    public static String pushtitle;
    public static String pushbody;
    public DatabaseHelper mydb;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().isEmpty()) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else {
            showNotification(remoteMessage.getData());
        }

    }


    private void showNotification(Map<String, String> data) {
        String title = data.get("title").toString();
        String body = data.get("body").toString();
        // Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);
        // Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/raw/notification");
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String NOTIFICATION_CHANNEL_ID = "cra.bimayangu.services.test";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("code sphere");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            notificationChannel.setSound(soundUri, audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Intent resultIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(MainActivity.pushdescritpion));
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        // PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.bimayangicon)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification))
                .setContentIntent(resultPendingIntent)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    private void showNotification(String title, String body) {
        // pushtitle = title;
        // pushbody = body;
        // Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);
        //   Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/raw/notification");
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "cra.bimayangu.services.test";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("code sphere");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            notificationChannel.setSound(soundUri, audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        // Intent resultIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(body));
// Create the TaskStackBuilder and add the intent, which inflates the back stack
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.bimayangicon)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification))
                .setContentIntent(resultPendingIntent)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("Firebasetoken ----->", s);
        mydb = new DatabaseHelper(MyFirebaseInstanceService.this);
        if (mydb.getfirebaseTokendetails().getCount() != 0) {
            mydb.deletefirebasetokendata();
        }
        boolean Isfbinserted = mydb.insertfirebasetoken(s);
        if (Isfbinserted == true) {
            boolean test = Isfbinserted;
            Log.i(null, "Insertion Done");
        } else {
            boolean test = Isfbinserted;
            Log.i(null, "Not Insertion Done");
        }
    }

}