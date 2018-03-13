package com.spotimyandroid.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class Artist {

    private String name;

    public Artist(JSONObject o) {
        try {
            this.name = o.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
            this.name = "name";
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
}
