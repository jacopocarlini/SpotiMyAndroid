package com.spotimyandroid.resources;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class Artist {

    private String name;
    private String image;

    public Artist(JSONObject o) {
        try {
            this.name = o.getString("name");

        } catch (JSONException e) {
//            e.printStackTrace();
            this.name = "name";

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

    public boolean hasImage() {
        if (image.equals("image")) return false;
        else return true;
    }
}
