package com.sahil.mymusicplayer.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sahil.mymusicplayer.Fragments.AlbumFragment;
import com.sahil.mymusicplayer.Fragments.SongsFragment;
import com.sahil.mymusicplayer.Model.SongFiles;
import com.sahil.mymusicplayer.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int REQUEST_CODE = 1;
    public static ArrayList<SongFiles> songFiles; // list of songs
    public static boolean shuffleBool = false , repeatbool=false;

    public static  ArrayList<SongFiles>albumList = new ArrayList<>(); // list of albums
    private final String MY_SORT_PREF = "sort_prefs"; // for keeping the sorting order after activity is killed

     @RequiresApi(api = Build.VERSION_CODES.Q)
     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.View_pager);
        TabLayout  tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new SongsFragment(), "Songs");
        viewPagerAdapter.AddFragment(new AlbumFragment(), "Albums");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        permission();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void permission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
        !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else{
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            songFiles  =getAllSongs(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /////
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                songFiles = getAllSongs(this);

            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public  ArrayList<SongFiles> getAllSongs(Context context){
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");

        ArrayList<SongFiles> list = new ArrayList<>();
        ArrayList<String> duplicate = new ArrayList<>();
        albumList.clear();

        String order = null;

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder){
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;

            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;
        }

        String [] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA    // for Path
        };

        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null,order);

        if(cursor!=null){
            while(cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String duration = cursor.getString(3);
                String path = cursor .getString(4);

                SongFiles songFiles = new SongFiles(title, path, duration, artist, album);
                list.add(songFiles);

//                Log.d("SONGSSS", " title " + title + " , Arist " + artist );
                if(!duplicate.contains(album)){
                    albumList.add(songFiles);
                    duplicate.add(album);
                }

            }
            cursor.close();
        }

        return list;
    }

    ///////// View Pager adapter class ////////
    public static class  ViewPagerAdapter extends FragmentPagerAdapter{
       private ArrayList<Fragment> fragments;
         private ArrayList<String> titles;

       public ViewPagerAdapter(@NonNull FragmentManager fm) {
           super(fm);
           this.fragments = new ArrayList<>();
           this.titles = new ArrayList<>();
       }


      public void AddFragment(Fragment fragment ,String title){
           fragments.add(fragment);
           titles.add(title);
       }


       @NonNull
       @Override
       public Fragment getItem(int position) {
           return fragments.get(position);
       }

       @Override
       public int getCount() {
           return fragments.size();
       }

       @Nullable
       @Override
       public CharSequence getPageTitle(int position) {
           return titles.get(position);
       }
   }


   ///////// to add search functionality  ///////
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
//        Log.d("SOONGGG", "ss " + input + "    " + newText);
        ArrayList<SongFiles> myFiles = new ArrayList<>();
        for(SongFiles song : songFiles ){
            String toAdded  =  song.getTitle().toLowerCase();
            if(toAdded.contains(input)){
                myFiles.add(song);
            }
        }
            SongsFragment.songFileAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // using shared prefs for checking which of the item is selected
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();

        switch (item.getItemId()){
            case R.id.sortby_name:
                editor.putString("sorting", "sortByName");
                editor.apply();  // applying editor
                this.recreate(); // recreating the activity
                break;
            case R.id.sortby_date:
                editor.putString("sorting", "sortByDate");
                editor.apply();  // applying editor
                this.recreate(); // recreating the activity
                break;
            case R.id.sortby_size:
                editor.putString("sorting", "sortBySize");
                editor.apply();  // applying editor
                this.recreate(); // recreating the activity
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}