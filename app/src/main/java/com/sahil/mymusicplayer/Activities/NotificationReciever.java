package com.sahil.mymusicplayer.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.sahil.mymusicplayer.Activities.ApplicationClass.ACTION_NEXT;
import static com.sahil.mymusicplayer.Activities.ApplicationClass.ACTION_PLAY;
import static com.sahil.mymusicplayer.Activities.ApplicationClass.ACTION_PREV;

public class NotificationReciever  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context,MusicService.class);

        switch (actionName){
            case ACTION_PLAY :
                Log.d("NNNN", "reciever");
                serviceIntent.putExtra("ACtionName", "playPause");
                context.startService(serviceIntent);
                break;
            case ACTION_NEXT :
                serviceIntent.putExtra("ACtionName", "next");
                context.startService(serviceIntent);
                break;
            case ACTION_PREV :
                serviceIntent.putExtra("ACtionName", "previous");
                context.startService(serviceIntent);
                break;
        }
    }
}
