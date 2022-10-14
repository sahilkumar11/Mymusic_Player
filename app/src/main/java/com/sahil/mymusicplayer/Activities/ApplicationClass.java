package com.sahil.mymusicplayer.Activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.sahil.mymusicplayer.BuildConfig;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_NEXT = "actionNext";
    public static final String ACTION_PREV = "actionPrev";
    public static final String ACTION_PLAY = "actionPlay";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_1,
                    "channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("channel desc..");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,
                    "channel(2)",NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("channel2 desc..!!");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
