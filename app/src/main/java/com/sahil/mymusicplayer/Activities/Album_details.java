package com.sahil.mymusicplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sahil.mymusicplayer.Adapters.AlbumDetailsAdapter;
import com.sahil.mymusicplayer.Model.SongFiles;
import com.sahil.mymusicplayer.R;

import java.util.ArrayList;

import static com.sahil.mymusicplayer.Activities.MainActivity.songFiles;

public class Album_details extends AppCompatActivity {
    RecyclerView recyclerView; //creating instances of recyclerView
    ImageView imageView;
    String albumName;
    ArrayList<SongFiles> albumSings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        recyclerView  = findViewById(R.id.recyclerview_album2);
        imageView = findViewById(R.id.album_cover_photo);

        albumName = getIntent().getStringExtra("Albumname");
        int count =0;

        for(int i=0;i<songFiles.size();i++){
            if(albumName.equals(songFiles.get(i).getAlbum())){
                 albumSings.add(count, songFiles.get(i));
                 count++;
                byte [] image = getalbumArt(songFiles.get(i).getPath());
                if(image!=null){
                    Glide.with(this)
                            .load(image)
                            .into(imageView);
                } else{
                    Glide.with(this)
                            .load(R.mipmap.ic_launcher_round)
                            .into(imageView);
                }
            }
        }

    }
    private byte[] getalbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }

    @Override
    protected void onResume() {
        super.onResume();
        {
            if(!(albumSings.size() <1)){
                AlbumDetailsAdapter adapter = new AlbumDetailsAdapter(albumSings, this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
            }
        }
    }
}
/*

 */