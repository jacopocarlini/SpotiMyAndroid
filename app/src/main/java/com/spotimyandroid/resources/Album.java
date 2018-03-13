package com.spotimyandroid.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 13/03/2018.
 */

public class Album {

    private String name;
    private String cover;

    public Album(JSONObject o) {
        try {
            this.name = o.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
            this.name = "name";

        }

        try {
            this.cover=o.getJSONArray("images").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
            this.cover="cover";

        }


    }
    public static Album[] toArray(JSONArray array) {
        Album[] a = new Album[array.length()];
        for (int i =0 ; i< array.length();i++){
            try {
                Album elem= new Album(array.getJSONObject(i));
                a[i]=elem;
            } catch (JSONException e) {
                e.printStackTrace();
                return new Album[0];
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

    public String getCover() {
        return cover;
    }

    public boolean hasCover() {
        if (cover.equals("cover")) return false;
        else return true;
    }
}
