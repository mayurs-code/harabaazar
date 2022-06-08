package com.example.harabazar.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.harabazar.Activity.RateActivity;
import com.example.harabazar.R;
import com.example.harabazar.Utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("MESSAGE_RECIEVED "+remoteMessage.getNotification());
        System.out.println("MESSAGE_RECIEVED "+remoteMessage.getNotification().getTitle());
        System.out.println("MESSAGE_RECIEVED "+remoteMessage.getNotification().getBody());
        String notification=remoteMessage.getNotification().getBody();
        Intent intentBroadcast = new Intent(Constants.BROAD_REFRESH_NOTIFICATION_COUNT);
        intentBroadcast.putExtra(Constants.NOTIFICATION_DATA ,notification );
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.ring);
        mp.start();
        if(notification.contains("Completed")&&notification.contains("Handpick")){
            Intent notificationIntent = new Intent(getApplicationContext() , RateActivity. class ) ;
            notificationIntent.putExtra( "NotificationMessage" , notification ) ;
            notificationIntent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
            notificationIntent.setAction(Intent. ACTION_MAIN ) ;
            notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
            notificationIntent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK ) ;
            startActivity(notificationIntent);
        }

    }
    
}
