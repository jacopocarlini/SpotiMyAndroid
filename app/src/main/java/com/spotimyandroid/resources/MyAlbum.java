package com.spotimyandroid.resources;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;

/**
 * Created by Jacopo on 13/03/2018.
 */

public class MyAlbum implements Parcelable {

    private String name;


    private String artist;
    private String cover;
    private String id;



    public MyAlbum(AlbumSimple album) {
        name = album.name;
        id = album.id;
        cover = album.images.get(0).url;


    }

    public MyAlbum() {

    }


    public static List<MyAlbum> toArray(List<AlbumSimple> items) {
        int size = items.size();
        ArrayList<MyAlbum> myTracks = new ArrayList<>();
        for(int i=0; i<size;i++){
            MyAlbum myAlbum = new MyAlbum(items.get(i));
            myTracks.add(myAlbum);
        }
        return myTracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getArtist() {
        return artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean hasCover() {
        if (cover.equals("cover")) return false;
        else return true;
    }

    @Override
    public String toString() {
        return  name  +
                ";" + artist +
                ";" + cover +
                ";" + id ;
    }
    public static MyAlbum[] toArray(String s) {
        if(s.equals("")) return new MyAlbum[0];
        ArrayList<MyAlbum> res=new ArrayList<>(5);
        String[] a = s.split(",,,");
        for (int i =0; i<a.length;i++) {
            String[] info = a[i].split(";");
            MyAlbum t = new MyAlbum();
            t.setName(info[0]);
            t.setArtist(info[1]);
            t.setCover(info[2]);
            t.setID(info[3]);
            res.add(t);
        }
        return res.toArray(new MyAlbum[res.size()]);
    }

    private void setID(String id) {
        this.id=id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(cover);
        parcel.writeString(id);
        parcel.writeString(artist);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyAlbum album = (MyAlbum) o;

        return id != null ? id.equals(album.id) : album.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    protected MyAlbum(Parcel in) {
        name = in.readString();
        cover = in.readString();
        id = in.readString();
        artist = in.readString();
    }

    public static final Creator<MyAlbum> CREATOR = new Creator<MyAlbum>() {
        @Override
        public MyAlbum createFromParcel(Parcel in) {
            return new MyAlbum(in);
        }

        @Override
        public MyAlbum[] newArray(int size) {
            return new MyAlbum[size];
        }
    };



}