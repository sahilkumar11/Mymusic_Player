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
import com.sahil.mymusicplayer.Model.SongFiles;
import com.sahil.mymusicplayer.Activities.PlayerActivity;
import com.sahil.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

 public class SongFileAdapter extends RecyclerView.Adapter<SongFileAdapter.ViewHolder> {

    private Context context;
    public static ArrayList<SongFiles> mFiles;

    public SongFileAdapter(Context context, ArrayList<SongFiles> mFiles) {
        this.context = context;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.song_name.setText(mFiles.get(position).getTitle()); // setting the title of the song

        byte[] image = getalbumArt(mFiles.get(position).getPath());
        if(image!=null){
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .into(holder.song_image);
        } else{
            Glide.with(context).load(R.mipmap.ic_launcher_round)
                    .into(holder.song_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {      // on click property of item(one song or one element)
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent); // starting the activity
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }



    // view holder class for recycler View of setting view
    public class ViewHolder  extends  RecyclerView.ViewHolder{
      TextView song_name;
      ImageView song_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            song_name = itemView.findViewById(R.id.song_name);
            song_image = itemView.findViewById(R.id.song_img);
        }
    }

    private byte[] getalbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();   // this class provides a unified interface for retrieving frame and meta data from an input media file.
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }

    public void updateList(ArrayList<SongFiles> updatedList){
        mFiles = new ArrayList<>();
        mFiles.addAll(updatedList);
        notifyDataSetChanged();   // displaying the updated list

    }

}
