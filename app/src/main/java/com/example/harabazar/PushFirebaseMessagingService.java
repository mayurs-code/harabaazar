/*
package com.example.harabazar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.fcm.MessagingService;
import com.ruleitgroup.scanstore.R;
import com.ruleitgroup.scanstore.activities.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class PushFirebaseMessagingService extends FirebaseMessagingService {
String type,title;
    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        AppLogger.i("NotificationsService", "Got a remote message 🎉");
        AppLogger.i("NotificationsService" + " ", " onMessageReceived data =" + message.getData().toString());
        AppLogger.i("NotificationsService" + " ", " onMessageReceived data..." + message.getData().get("body"));
       // AppLogger.i("NotificationsService" + " ", " onMessageReceived getNotification =" + message.getNotification().getBody());


        if (message.getNotification() == null)
            return;
        */
/*Your Makhana product quntity is low.*//*

*/
/*{"id":242,"name":"Makhana","qty":2,"store_name":"Unicorn Grocery Shop","shop_id":5,"user_id":5,"email":"ruleitgroup@gmail.com","c_name":"Vishal Paliwal"}*//*

      */
/*  if(Utils.isBlankOrNull(message.getNotification().getBody())){

        }*//*

      if(!Utils.isBlankOrNull(message.getData().get("body"))){
          try {
              JSONObject jsonObject = new JSONObject(message.getData().get("body"));
              title = message.getNotification().getTitle();
            */
/*  type = jsonObject.getString("type");
              AppLogger.i(Utils.getTag(),"title..."+title);
              AppLogger.i(Utils.getTag(),"type..."+type);*//*



          } catch (JSONException e) {
              e.printStackTrace();
          }

      }

        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
*/
/*
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                //.setTicker("Hearty365")
                .setContentTitle("test")
                .setContentText("Lorum ipsum")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(1, b.build());*//*

      */
/*  NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Storely")
                .setContentText(message.getNotification().getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null)
            notificationManager.notify(0, notificationBuilder.build());*//*

    }
}
*/
