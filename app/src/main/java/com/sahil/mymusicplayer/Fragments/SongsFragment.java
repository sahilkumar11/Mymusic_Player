package com.sahil.mymusicplayer.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahil.mymusicplayer.Adapters.SongFileAdapter;
import com.sahil.mymusicplayer.R;

import static com.sahil.mymusicplayer.Activities.MainActivity.songFiles;


public class SongsFragment extends Fragment {
    private RecyclerView recyclerView;
    public static SongFileAdapter songFileAdapter;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.song_recyclerView);
        recyclerView.setHasFixedSize(true);
        if(!(songFiles.size()<1)){
            songFileAdapter = new SongFileAdapter(getContext(), songFiles);
            recyclerView.setAdapter(songFileAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,
                    false));
        }
        return view;
    }
}