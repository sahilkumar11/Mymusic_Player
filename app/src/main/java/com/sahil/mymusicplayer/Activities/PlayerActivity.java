package com.sahil.mymusicplayer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahil.mymusicplayer.Model.SongFiles;
import com.sahil.mymusicplayer.R;

import java.util.ArrayList;
import java.util.Random;

import static com.sahil.mymusicplayer.Activities.ApplicationClass.ACTION_NEXT;
import static com.sahil.mymusicplayer.Activities.ApplicationClass.ACTION_PLAY;
import static com.sahil.mymusicplayer.Activities.ApplicationClass.ACTION_PREV;
import static com.sahil.mymusicplayer.Activities.ApplicationClass.CHANNEL_ID_2;
import static com.sahil.mymusicplayer.Activities.MainActivity.repeatbool;
import static com.sahil.mymusicplayer.Activities.MainActivity.shuffleBool;
import static com.sahil.mymusicplayer.Activities.MainActivity.songFiles;
import static com.sahil.mymusicplayer.Adapters.AlbumDetailsAdapter.albumFiles;
import static com.sahil.mymusicplayer.Adapters.SongFileAdapter.mFiles;
import static com.sahil.mymusicplayer.R.mipmap.ic_launcher_round;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying , ServiceConnection {

    private TextView song_name,Artist_name,duration_played,duration_total;
    private ImageView coverArt ,nextBtn,prevBtn,shuffleBtn,repeatBtn;
    private SeekBar seekBar;
    private FloatingActionButton playPauseBtn;

    int  position ;
    static ArrayList<SongFiles> listsongs = new ArrayList<>();
    static Uri uri;
//    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread,prevThread,nextThread;
//
    MusicService musicService; // creating instance of MusicService class;

    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();

        song_name = findViewById(R.id.song_name_play);
        Artist_name = findViewById(R.id.artist_name);
        duration_played =findViewById(R.id.playing_duration);
        duration_total = findViewById(R.id.total_duration);
        coverArt = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.next_btn);
        prevBtn = findViewById(R.id.prev_btn);
        shuffleBtn = findViewById(R.id.shuffle);
        repeatBtn = findViewById(R.id.repeat_btn);
        seekBar = findViewById(R.id.seekBar);
        playPauseBtn = findViewById(R.id.play_pause);

        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "my audio");

      playsong();  // getting position of song by intent through Song fragment


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if(musicService!=null && fromUser){
                  musicService.seekTO(progress*1000);
              }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {

          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {

          }
      });

      PlayerActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
              if(musicService!=null){
                  int mCurrentPosition = musicService.getCurrentPostition()/1000;
                  seekBar.setProgress(mCurrentPosition);
                  duration_played.setText(formattedTime(mCurrentPosition));
              }
              handler.postDelayed(this, 1000);
          }
      });

      shuffleBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(shuffleBool){
                  shuffleBool = false;
                  shuffleBtn.setImageResource(R.drawable.ic_shuffle_of);
              } else {
                  shuffleBool =true;
                  shuffleBtn.setImageResource(R.drawable.ic_shuffle);
              }
          }
      });
      repeatBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(repeatbool){
                  repeatbool = false;
                  repeatBtn.setImageResource(R.drawable.ic_repeat);
              } else {
                  repeatbool = true;
                  repeatBtn.setImageResource(R.drawable.ic_repeat_on);
              }
          }
      });

    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    String sender;
    private void playsong() {
        position = getIntent().getIntExtra("position", -1);

         sender = getIntent().getStringExtra("sender");
        if(sender!= null && sender.equals("albumDetails")){
            listsongs = albumFiles;
        } else{
            listsongs =mFiles;
        }
        if(listsongs!=null){
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listsongs.get(position).getPath());
        }
        showNotification(R.drawable.ic_pause);
         Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);


    }

     void MetaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(String.valueOf(uri));
        int totalDuration = Integer.parseInt(listsongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(totalDuration));
        byte[] art = retriever.getEmbeddedPicture();

         Bitmap bitmap;
        if(art!=null){
//            Glide.with(this).asBitmap().load(art).into(coverArt);

            bitmap  = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(getApplicationContext(),coverArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch= palette.getDominantSwatch();
                    if(swatch!=null){
                  ImageView gradient = findViewById(R.id.imagegradient_bg);
                        ConstraintLayout mContainer = findViewById(R.id.mContainer);
//                        gradient.setBackgroundResource(R.drawable.background_bg);
//                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP
                        ,new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawablebg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawablebg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        Artist_name.setTextColor(swatch.getBodyTextColor());

                    } else{
                        ImageView gradient = findViewById(R.id.imagegradient_bg);
                        ConstraintLayout mContainer = findViewById(R.id.mContainer);
//                        gradient.setBackgroundResource(R.drawable.background_bg);
//                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP
                                ,new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawablebg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        mContainer.setBackground(gradientDrawablebg);
                        song_name.setTextColor(Color.BLUE);
                        Artist_name.setTextColor(Color.DKGRAY);


                    }
                }
            });
        } else{
            Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(coverArt);

                ImageView gradient = findViewById(R.id.imagegradient_bg);
                ConstraintLayout mContainer = findViewById(R.id.mContainer);
//                        gradient.setBackgroundResource(R.drawable.background_bg);
//                        mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.DKGRAY);
            Artist_name.setTextColor(Color.DKGRAY);
        }

    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String second = String.valueOf(mCurrentPosition%60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalOut = minutes+ ":" + second;
        totalNew = minutes + ":" + "0" + second;

        if (second.length()==1){
            return totalNew;
        } else{
            return totalOut;
        }
    }


    @Override
    protected void onPostResume() {
        //TO Connect the Service
        Intent intent  = new Intent(this,MusicService.class);
        bindService(intent,this, BIND_AUTO_CREATE );  // Binding the Service
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        unbindService(this);  // UnBind the Service
        super.onPause();
    }

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
      if(musicService.isPlaying()){
          showNotification(R.drawable.ic_play_arrow);
          playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
          musicService.pause();
          seekBar.setMax(musicService.getDuration()/1000);

          PlayerActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  if(musicService!=null){
                      int mCurrentPosition = musicService.getCurrentPostition()/1000;
                      seekBar.setProgress(mCurrentPosition);

                  }
                  handler.postDelayed(this, 1000);
              }
          });

      } else{
          showNotification(R.drawable.ic_pause);
          playPauseBtn.setImageResource(R.drawable.ic_pause);
          musicService.start();
          PlayerActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  if(musicService!=null){
                      int mCurrentPosition = musicService.getCurrentPostition()/1000;
                      seekBar.setProgress(mCurrentPosition);

                  }
                  handler.postDelayed(this, 1000);
              }
          });

      }
      }


    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        if(musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            //if shuffle is on
            if (shuffleBool && !repeatbool) {
                Random random = new Random();
                position = random.nextInt(listsongs.size());
            }  //if repeat is on
            else if(!shuffleBool && !repeatbool){
                position = ((position -1)<0)?listsongs.size()-1: position-1;            }
            //if shuffle and repeat is of then postion will not change by +1

            uri = Uri.parse(listsongs.get(position).getPath());
//            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri );
            musicService.createMediaPlayer(position);
            MetaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            Artist_name.setText(listsongs.get(position).getArtist());
            ///////////////////////
            seekBar.setMax(musicService.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition = musicService.getCurrentPostition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        } else{
            musicService.stop();
            musicService.release();
             //if shuffle is on
            if (shuffleBool && !repeatbool) {
                Random random = new Random();
                position = random.nextInt(listsongs.size());
            }  //if repeat is on
            else if(!shuffleBool && !repeatbool){
                position = ((position -1)<0)?listsongs.size()-1: position-1;            }
            //if shuffle and repeat is of then postion will not change by +1
            uri = Uri.parse(listsongs.get(position).getPath());
//            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri );
            musicService.createMediaPlayer(position);
            MetaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            Artist_name.setText(listsongs.get(position).getArtist());
            ///////////////////////
            seekBar.setMax(musicService.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int mCurrentPosition = musicService.getCurrentPostition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_play_arrow);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play_arrow);
        }
    }


    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
       if(musicService.isPlaying()){
           musicService.stop();
           musicService.release();
           //if shuffle is on
           if (shuffleBool && !repeatbool) {
               Random random = new Random();
               position = random.nextInt(listsongs.size());
           }  //if repeat is on
           else if(!shuffleBool && !repeatbool){
               position = (position + 1) % listsongs.size();
           }
           //if shuffle and repeat is of then postion will not change by +1
           uri = Uri.parse(listsongs.get(position).getPath());
//           mediaPlayer = MediaPlayer.create(getApplicationContext(),uri );
           musicService.createMediaPlayer(position);
           MetaData(uri);
           song_name.setText(listsongs.get(position).getTitle());
           Artist_name.setText(listsongs.get(position).getArtist());
           ///////////////////////
           seekBar.setMax(musicService.getDuration()/1000);

           PlayerActivity.this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if(musicService!=null){
                       int mCurrentPosition = musicService.getCurrentPostition()/1000;
                       seekBar.setProgress(mCurrentPosition);

                   }
                   handler.postDelayed(this, 1000);
               }
           });
           musicService.OnCompleted();;
           showNotification(R.drawable.ic_pause);
           playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
           musicService.start();
       } else{
           musicService.stop();
           musicService.release();
           //if shuffle is on
           if (shuffleBool && !repeatbool) {
               Random random = new Random();
               position = random.nextInt(listsongs.size());
           }  //if repeat is on
           else if(!shuffleBool && !repeatbool){
               position = (position + 1) % listsongs.size();
           }
           //if shuffle and repeat is of then postion will not change by +1

           uri = Uri.parse(listsongs.get(position).getPath());
//           mediaPlayer = MediaPlayer.create(getApplicationContext(),uri );
           musicService.createMediaPlayer(position);
           MetaData(uri);
           song_name.setText(listsongs.get(position).getTitle());
           Artist_name.setText(listsongs.get(position).getArtist());
           ///////////////////////
           seekBar.setMax(musicService.getDuration()/1000);

           PlayerActivity.this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if(musicService!=null){
                       int mCurrentPosition = musicService.getCurrentPostition()/1000;
                       seekBar.setProgress(mCurrentPosition);

                   }
                   handler.postDelayed(this, 1000);
               }
           });
           musicService.OnCompleted();
           showNotification(R.drawable.ic_play_arrow);
           playPauseBtn.setBackgroundResource(R.drawable.ic_play_arrow);
       }
    }


    public void ImageAnimation(final Context context , final ImageView imageView, final Bitmap bitmap){
        Animation animation_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation animation_in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animation_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            Glide.with(context).load(bitmap).into(imageView);
            animation_in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
           imageView.startAnimation(animation_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation_out);

    }


    // methods of service connections
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service; //initialising the binder class
        musicService = myBinder.getService(); // initialising music Service using myBinder.getService() method
       musicService.setCallback(this);
        Toast.makeText(this, "Connected "+ musicService, Toast.LENGTH_SHORT).show();

        seekBar.setMax(musicService.getDuration()/1000);
        MetaData(uri);
        song_name.setText(listsongs.get(position).getTitle());
        Artist_name.setText(listsongs.get(position).getArtist());
        musicService.OnCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
           musicService = null;
    }

    void showNotification(int playPauseBtn){
        Intent intent = new Intent(this,PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent prevIntent = new Intent(this,NotificationReciever.class)
                .setAction(ACTION_PREV);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this,NotificationReciever.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this,NotificationReciever.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        byte[] picture = null;
        picture = getalbumArt(listsongs.get(position).getPath());
        Bitmap thumb =null;

        if(picture != null){
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), ic_launcher_round);
        }

        Notification notification =  new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(listsongs.get(position).getTitle())
                .setContentText(listsongs.get(position).getArtist())
                .addAction(R.drawable.ic_previous_btn, "previous", prevPending)
                .addAction(playPauseBtn,"pause",pausePending)
                .addAction(R.drawable.ic_next_btn, "next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);



    }
    private byte[] getalbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();   // this class provides a unified interface for retrieving frame and meta data from an input media file.
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }

}

