package com.spotimyandroid.resources;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class Artist implements Parcelable {

    private String name;
    private String image;
    private String id;

    public Artist(JSONObject o) {
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
    public static Artist[] toArray(JSONArray array) {
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
