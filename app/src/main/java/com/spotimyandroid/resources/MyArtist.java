package com.spotimyandroid.resources;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class MyArtist implements Parcelable {

    private String name;
    private String image;
    private String id;

    private MyArtist(Artist artist) {
        name = artist.name;
        id = artist.id;
        if(artist.images.size()>0)
            image = artist.images.get(0).url;
        else image = "null";
    }

    public MyArtist() {

    }

    public static List<MyArtist> toArray(List<Artist> items) {
        int size = items.size();
        ArrayList<MyArtist> myArtists = new ArrayList<>();
        for(int i=0; i<size;i++){
            MyArtist myArtist = new MyArtist(items.get(i));
            myArtists.add(myArtist);
        }
        return myArtists;
    }


    @Override
    public String toString() {
        return  name  +
                ";" + image +
                ";" + id ;
    }
    public static MyArtist[] toArray(String s) {
        if(s.equals("")) return new MyArtist[0];
        ArrayList<MyArtist> res=new ArrayList<>(5);
        String[] a = s.split(",,,");
        for (int i =0; i<a.length;i++) {
            String[] info = a[i].split(";");
            MyArtist t = new MyArtist();
            t.setName(info[0]);
            t.setImage(info[1]);
            t.setId(info[2]);
            res.add(t);
        }
        return res.toArray(new MyArtist[res.size()]);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasImage() {
        if (image.equals("null")) return false;
        else return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyArtist MyArtist = (MyArtist) o;

        return id != null ? id.equals(MyArtist.id) : MyArtist.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(id);


    }


    protected MyArtist(Parcel in) {
        name = in.readString();
        image = in.readString();
        id = in.readString();

    }

    public static final Creator<MyArtist> CREATOR = new Creator<MyArtist>() {
        @Override
        public MyArtist createFromParcel(Parcel in) {
            return new MyArtist(in);
        }

        @Override
        public MyArtist[] newArray(int size) {
            return new MyArtist[size];
        }
    };


}
