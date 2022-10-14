package com.sahil.mymusicplayer.Adapters;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sahil.mymusicplayer.Activities.Album_details;
import com.sahil.mymusicplayer.Activities.PlayerActivity;
import com.sahil.mymusicplayer.Model.SongFiles;
import com.sahil.mymusicplayer.R;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.ViewHolder> {

  public static    ArrayList<SongFiles> albumFiles;
    private Context context;

    public AlbumDetailsAdapter(ArrayList<SongFiles> albumFiles, Context context) {
        this.albumFiles = albumFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.Album_name.setText(albumFiles.get(position).getTitle());

        byte[] image = getalbumArt(albumFiles.get(position).getPath());
        if(image!=null){
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .into(holder.Album_image);
        } else{
            Glide.with(context).load(R.mipmap.ic_launcher_round)
                    .into(holder.Album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, PlayerActivity.class);
                intent.putExtra("sender", "albumDetails");
                intent.putExtra("position", position);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView Album_image;
        public TextView Album_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Album_image = itemView.findViewById(R.id.song_img);
            Album_name = itemView.findViewById(R.id.song_name);

        }
    }
    private byte[] getalbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}
