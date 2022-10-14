package com.sahil.mymusicplayer.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahil.mymusicplayer.Adapters.AlbumAdapter;
import com.sahil.mymusicplayer.Adapters.SongFileAdapter;
import com.sahil.mymusicplayer.R;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static com.sahil.mymusicplayer.Activities.MainActivity.albumList;
import static com.sahil.mymusicplayer.Activities.MainActivity.songFiles;

public class AlbumFragment extends Fragment {
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.album_recyclerView);
        recyclerView.setHasFixedSize(true);
        if (!(albumList.size() < 1)) {
            albumAdapter = new AlbumAdapter( albumList,getContext());
            recyclerView.setAdapter(albumAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
//                    false));
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, VERTICAL,false));
        }
        return view;
    }
}