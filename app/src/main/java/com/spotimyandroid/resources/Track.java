package com.spotimyandroid.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class Track {
    private String name;
    private String artist;
    private String album;
    private String date;
    private int duration;

    public Track(String name, String artists, String album, String date, int duration) {
        this.name = name;
        this.artist = artists;
        this.album = album;
        this.date = date;
        this.duration = duration;
    }

    public Track(JSONObject o) {
        try {
            this.name = o.getString("name");
            this.artist =  o.getJSONArray("artists").getJSONObject(0).getString("name");
//            this.album =  o.getString("album");
//            this.date =  o.getString("date");
//            this.duration =  o.getInt("duration");
        } catch (JSONException e) {
            e.printStackTrace();
            this.name = "name";
            this.artist = "artists";
            this.album = "album";
            this.date = "date";
            this.duration = 0;
        }

    }

    public static Track[] toArray(JSONArray array){
        Track[] r = new Track[array.length()];
        for (int i =0 ; i< array.length();i++){
            try {
                Track elem= new Track(array.getJSONObject(i));
                r[i]=elem;
            } catch (JSONException e) {
                e.printStackTrace();
                return new Track[0];
            }
        }
        return r;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artists) {
        this.artist = artists;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
