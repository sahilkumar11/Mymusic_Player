package com.sahil.mymusicplayer.Activities;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sahil.mymusicplayer.Model.SongFiles;

import java.util.ArrayList;
import java.util.List;

import static com.sahil.mymusicplayer.Activities.PlayerActivity.listsongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<SongFiles>  musicFiles = new ArrayList<>();
    private Uri uri;
    int postion =-1;

    ActionPlaying actionPlaying;
    @Override
    public void onCreate() {
        super.onCreate();

    }

    //When the service will Bind this method will called
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("BIND" , "METHOD");   
        return mBinder;
    }



    public class MyBinder extends Binder {
        MusicService getService(){   // this method returns the instance of this class
            return  MusicService.this;
        }
    }
    //when the service is started this method will be called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myIntent = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ACtionName");
        if(myIntent != -1){
            playMedia(myIntent);
        }

        if(actionName != null){
            switch (actionName){
                case "playPause":
                    Log.d("NNNN","PLAY");
                    Toast.makeText(this, "playPause", Toast.LENGTH_SHORT).show();
                    if(actionPlaying != null){
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "next":
                    Log.d("NNNN","next");
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    if(actionPlaying != null){
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
                    Log.d("NNNN","prev");
                    Toast.makeText(this, "prev", Toast.LENGTH_SHORT).show();
                    if(actionPlaying != null){
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int Spostion) {
        musicFiles = listsongs;
        postion = Spostion;
        if(mediaPlayer !=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles !=null){
                createMediaPlayer(postion);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(postion);
            mediaPlayer.start();
        }
    }

    void start(){
         mediaPlayer.start();
    }
    boolean isPlaying(){
      return   mediaPlayer.isPlaying();
    }
    void stop(){
         mediaPlayer.stop();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration(){
        return mediaPlayer.getDuration();
    }

    void seekTO(int pos){
        mediaPlayer.seekTo(pos);
    }

    int getCurrentPostition(){
        return mediaPlayer.getCurrentPosition();
    }
    void createMediaPlayer(int pos){
        postion = pos;
        uri = Uri.parse(musicFiles.get(pos).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    void pause(){
        mediaPlayer.pause();
    }

    void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(actionPlaying !=null){
            actionPlaying.nextBtnClicked();
            if(mediaPlayer!=null){
                createMediaPlayer(postion);
                start();
                OnCompleted();
            }
        }


    }

    void setCallback(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }


}
