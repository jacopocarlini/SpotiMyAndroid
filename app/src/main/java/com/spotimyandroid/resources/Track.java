package com.spotimyandroid.resources;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class Track implements Parcelable {
    private String name;
    private String artist;
    private String album;
    private String date;
    private String cover;
    private String id;
    private String albumid;
    private String artistid;
    private String artistImage;



    private String lyric;
    private int duration;
    private JSONObject json;


    public Track(JSONObject o) {
        json=o;
        try {
            this.name = o.getString("name");
            this.artist =  o.getJSONArray("artists").getJSONObject(0).getString("name");
            this.id =  o.getString("id");
            this.artistid = o.getJSONArray("artists").getJSONObject(0).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            this.name = "name";
            this.artist = "artists";
            this.album = "album";
            this.date = "date";
            this.duration = 0;
            this.id = "";
        }

        try {
            if(o.has("album")) {
                this.cover = o.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
            }
        } catch (JSONException e) {
            this.cover="cover";
        }
        try {
            if(o.has("album")) {
                this.album = o.getJSONObject("album").getString("name");
                this.albumid = o.getJSONObject("album").getString("id");
                album=album.split(Pattern.quote("("))[0];
                album=album.split("-")[0];
            }
        } catch (JSONException e) {
            this.album="album";
        }
        this.artistImage="";
        name=name.split(Pattern.quote("("))[0];
        name=name.split("-")[0];

    }

    public Track() {
        this.name = "name";
        this.artist = "artists";
        this.album = "album";
        this.cover="cover";
        this.date = "date";
        this.lyric = "lyric";
        this.duration = 0;
        this.id = "id";
    }



    public static Track[] toArray(JSONObject result){
        JSONArray array = new JSONArray();
        try {
            if (!result.has("tracks"))
                array = result.getJSONArray("items");
            else{
               try {
                   System.out.println("json");
                   result = result.getJSONObject("tracks");
                   array = result.getJSONArray("items");
               }
               catch (Exception e){
                   array =  result.getJSONArray("tracks");
               }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getArtistid() {
        return artistid;
    }

    public void setArtistid(String artistid) {
        this.artistid = artistid;
    }

    public String getArtistImage() {
        return artistImage;
    }

    public void setArtistImage(String artistImage) {
        this.artistImage = artistImage;
    }

    public boolean hasCover(){
        if (cover.equals("cover")) return false;
        else return true;
    }

    @Override
    public String toString() {
        return  name  +
                ";" + artist +
                ";" + album +
                ";" + date +
                ";" + cover +
                ";" + lyric +
                ";" + duration +
                ";" + id +
                ";" + albumid +
                ";" + artistid +
                ";" + artistImage ;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static Track[] toArray(String s) {
        if(s.equals("")) return new Track[0];
        ArrayList<Track>res=new ArrayList<>(5);
        String[] a = s.split(",,,");
        for (int i =0; i<a.length;i++) {
            String[] info = a[i].split(";");
            Track t = new Track();
            t.setName(info[0]);
            t.setArtist(info[1]);
            t.setAlbum(info[2]);
            t.setDate(info[3]);
            t.setCover(info[4]);
            t.setLyric(info[5]);
            //            t.setDuration(Integer.parseInt(info[6]));
            t.setID(info[7]);
            t.setAlbumid(info[8]);
            t.setArtistid(info[9]);
            t.setArtistImage(info[10]);


            res.add(t);
        }
        return res.toArray(new Track[res.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        return id != null ? id.equals(track.id) : track.id == null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(cover);
        parcel.writeString(id);
        parcel.writeString(albumid);
        parcel.writeString(artistid);
        parcel.writeString(artistImage);

    }


    protected Track(Parcel in) {
        name = in.readString();
        artist = in.readString();
        album = in.readString();
        cover = in.readString();
        id = in.readString();
        albumid = in.readString();
        artistid = in.readString();
        artistImage = in.readString();

    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };


    public JSONObject getJSON() {
        return json;
    }


    public void setID(String ID) {
        this.id = ID;
    }

    public void addArtistImage(JSONObject result) {
        try {
            artistImage = result.getJSONObject("artists").getJSONArray("items").getJSONObject(0)
                    .getJSONArray("images").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
