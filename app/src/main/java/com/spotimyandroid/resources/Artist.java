package com.spotimyandroid.resources;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class Artist implements Parcelable {

    private String name;
    private String image;
    private String id;

    private Artist(JSONObject o) {
        try {
            this.name = o.getString("name");
            this.id = o.getString("id");

        } catch (JSONException e) {
//            e.printStackTrace();
            this.name = "name";
            this.id = "id";

        }

        try {
            JSONArray a = o.getJSONArray("images");
            this.image=a.getJSONObject(1).getString("url");
        } catch (JSONException e) {
//            e.printStackTrace();
            this.image="image";

        }


    }

    public Artist() {

    }

    public static Artist[] toArray(JSONObject result) {
        JSONArray array = new JSONArray();
        try {
            array = result.getJSONObject("artists").getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Artist[] a = new Artist[array.length()];
        for (int i =0 ; i< array.length();i++){
            try {
                Artist elem= new Artist(array.getJSONObject(i));
                a[i]=elem;
            } catch (JSONException e) {
                e.printStackTrace();
                return new Artist[0];
            }
        }
        return a;
    }

    @Override
    public String toString() {
        return  name  +
                ";" + image +
                ";" + id ;
    }
    public static Artist[] toArray(String s) {
        if(s.equals("")) return new Artist[0];
        ArrayList<Artist> res=new ArrayList<>(5);
        String[] a = s.split(",,,");
        for (int i =0; i<a.length;i++) {
            String[] info = a[i].split(";");
            Artist t = new Artist();
            t.setName(info[0]);
            t.setImage(info[1]);
            t.setId(info[2]);
            res.add(t);
        }
        return res.toArray(new Artist[res.size()]);
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
        if (image.equals("image")) return false;
        else return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return id != null ? id.equals(artist.id) : artist.id == null;
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


    protected Artist(Parcel in) {
        name = in.readString();
        image = in.readString();
        id = in.readString();

    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

}
