package com.sahil.mymusicplayer.Model;

public class SongFiles {
    private String title;
    private String path;
    private String duration;
    private String artist;
    private String album;

    public SongFiles(String title, String path, String duration, String artist, String album) {
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
    }

    public SongFiles() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
