package com.sama.samacontactlenses.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sama.samacontactlenses.Activity.HomeActivity;
import com.sama.samacontactlenses.Activity.LoginActivity;
import com.sama.samacontactlenses.Activity.NotificationActivity;
import com.sama.samacontactlenses.Activity.OrderDetailsActivity;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import static com.sama.samacontactlenses.Common.Constant.NOTIFICATION_COUNT;
import static com.sama.samacontactlenses.Common.Constant.SHARED_PREF;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String title = "", description = "", image_url = "",type="",id="",cat_id="";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        storeRegIdInPref(s);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject(remoteMessage.getData());
        try {
            title = jsonObj.getString("title");
            description = jsonObj.getString("body");
            if(jsonObj.has("image")){
                image_url = jsonObj.getString("image");
            }

            type = jsonObj.getString("notification_type");
            if (type.equals("O")){
                id = jsonObj.getString("orderId");
            }else if (type.equals("C")){
                cat_id = jsonObj.getString("category_id");
            }
            else {
                id="";
                cat_id="";
            }
            NOTIFICATION_COUNT = jsonObj.getString("notification_count");
            handleSimpleNoti(title, description, image_url, type, id);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void handleSimpleNoti(String title, String description, String image_url, String type, String id) {
        int icon = R.drawable.logo_2;
        int min = 65;
        int max = 80;
        long[] v = {500,1000,500,1000,500,1000};

        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;
        int requestID = i1;
        String CHANNEL_ID = "1002";// The id of the channel.
        CharSequence name = "Sama Contact Lenses";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Bitmap bitmap = null;
        if (!image_url.equals("") && !image_url.equals("null") && !image_url.equals(null)){
            bitmap = getBitmapFromURL(image_url);
        }



        Intent notificationIntent = null;
        if (type.equals("O")){
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_logged_in", false)) {
                notificationIntent = new Intent(this, OrderDetailsActivity.class);
                notificationIntent.putExtra("order_id", id);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }else {
                notificationIntent = new Intent(this, LoginActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
        }else if (type.equals("C")){
            notificationIntent = new Intent(this, SearchActivity.class);
            notificationIntent.putExtra("category_id",cat_id);
            notificationIntent.putExtra("parent_id","");
            notificationIntent.putExtra("key","");
            notificationIntent.putExtra("sort","");
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }else if (type.equals("H")){
            notificationIntent = new Intent(this, HomeActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }else {
            notificationIntent = new Intent(this, NotificationActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableVibration(true);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle(title);
            builder.setContentText(description);
            builder.setSmallIcon(icon);
            if(bitmap != null){
                builder.setLargeIcon(bitmap);
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
            }else {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(description).setBigContentTitle(title));
            }
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            builder.setVibrate(v);
            builder.setContentIntent(contentIntent);
            builder.setChannelId(CHANNEL_ID);
            builder.setAutoCancel(true);
            final Notification notification = builder.build();
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(requestID, notification);
        } else {
            final Notification notification;
            if(bitmap != null){
                notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(title)
                        .setContentText(description)
                        .setSmallIcon(icon)
                        .setLargeIcon(bitmap)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setVibrate(v)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();
                mNotificationManager.notify(requestID, notification);
            }else {
                notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(title)
                        .setContentText(description)
                        .setSmallIcon(icon)
                        .setStyle(new Notification.BigTextStyle().bigText(description).setBigContentTitle(title))
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setVibrate(v)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();
                mNotificationManager.notify(requestID, notification);
            }

        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
