package com.spotimyandroid.resources;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class MyTrack implements Parcelable {
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


    public MyTrack(Track track) {
        name = track.name;
        album = track.album.name;
        artist = track.artists.get(0).name;
        cover = track.album.images.get(0).url;
        id = track.id;
    }
    public MyTrack(TrackSimple track) {
        name = track.name;
        artist = track.artists.get(0).name;
        id = track.id;
    }

    public MyTrack() {
        this.name = "name";
        this.artist = "artists";
        this.album = "album";
        this.cover="cover";
        this.date = "date";
        this.lyric = "lyric";
        this.duration = 0;
        this.id = "id";
    }



    public static List<MyTrack> toArray(List<Track> tracks) {
        int size = tracks.size();
        ArrayList<MyTrack> myTracks = new ArrayList<>();
        for(int i=0; i<size;i++){
            MyTrack myTrack = new MyTrack(tracks.get(i));
            myTracks.add(myTrack);
        }
        return myTracks;
    }

    public static List<MyTrack> toArraySimple(List<TrackSimple> tracks, Album album) {
        int size = tracks.size();
        ArrayList<MyTrack> myTracks = new ArrayList<>();
        for(int i=0; i<size;i++){
            MyTrack myTrack = new MyTrack(tracks.get(i));
            myTrack.setAlbumid(album.id);
            myTrack.setCover(album.images.get(0).url);
            myTracks.add(myTrack);
        }
        return myTracks;
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

    public static MyTrack[] toArray(String s) {
        if(s.equals("")) return new MyTrack[0];
        ArrayList<MyTrack>res=new ArrayList<>(5);
        String[] a = s.split(",,,");
        for (int i =0; i<a.length;i++) {
            String[] info = a[i].split(";");
            MyTrack t = new MyTrack();
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
        return res.toArray(new MyTrack[res.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyTrack track = (MyTrack) o;

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


    protected MyTrack(Parcel in) {
        name = in.readString();
        artist = in.readString();
        album = in.readString();
        cover = in.readString();
        id = in.readString();
        albumid = in.readString();
        artistid = in.readString();
        artistImage = in.readString();

    }

    public static final Creator<MyTrack> CREATOR = new Creator<MyTrack>() {
        @Override
        public MyTrack createFromParcel(Parcel in) {
            return new MyTrack(in);
        }

        @Override
        public MyTrack[] newArray(int size) {
            return new MyTrack[size];
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