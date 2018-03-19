package com.spotimyandroid.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spotimyandroid.http.RequestQueue_Singeton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Jacopo on 11/03/2018.
 */


public class Api {
//    private static String SERVER_IP = "10.0.2.2"; //localhost
//    private static String SERVER_IP = "192.168.1.15"; //pc
    private static String SERVER_IP = "104.40.208.29"; //azure
//    private static String SERVER_IP = "casacarlini.homepc.it"; //raspberyy
    private static String SERVER_PORT = "3000";
//    private static String SERVER_PORT = "3001";

    public static final String TAG = "API";
    private static Context contextS;
    private Context context;
    private RequestQueue queue;
    private int offset;


    public Api(Context context) {
        this.context = context;
        contextS=context;
        // Get a RequestQueue
        queue = RequestQueue_Singeton.getInstance(context).getRequestQueue();
    }

    private void call(String url, final VolleyCallback callback) {
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
//                        Log.d("response", response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                try {
                    callback.onSuccess(new JSONObject("{\"status\":\"error\"}"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // Set the tag on the request.
        jsonRequest.setTag(TAG);
        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestQueue_Singeton.getInstance(context).addToRequestQueue(jsonRequest);
    }



    public void findTrack(String query, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/track/" + sostituisci(query);
        call(url, callback);
    }

    public void findArtist(String query, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/artist/" + sostituisci(query);
        call(url, callback);
    }

    public void findAlbum(String query, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/album/" + sostituisci(query);
        call(url, callback);
    }

    public void lyric(String artist, String track, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/lyric/" + sostituisci(artist)+"/"+sostituisci(track);
        call(url, callback);
    }

    public void findTracksOfAlbum(String id, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/tracks_of_album/" + sostituisci(id);
        call(url, callback);
    }

    public void findAlbumsOfArtist(String id, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/albums_of_artist/" + sostituisci(id);
        call(url, callback);
    }

    public void findPopularOfArtist(String id, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/popular_of_artist/" + sostituisci(id);
        call(url, callback);
    }

    public static String getTrackURL(String artist, String album, String track) {
        track = track.split("-")[0];
        FileInputStream in = null;
        try {
            in = contextS.getApplicationContext().openFileInput("settings.txt");

        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            if(sb.toString().equals("fast")) return  "http://" + SERVER_IP + ":"+SERVER_PORT+"/playfast/"+  sostituisci(artist)+"/"+sostituisci(track);
            else return  "http://" + SERVER_IP + ":"+SERVER_PORT+"/play_and_save/" + sostituisci(artist)+"/" + sostituisci(album)+"/"+ sostituisci(track);
        } catch (IOException e) {
            e.printStackTrace();
            return  "http://" + SERVER_IP + ":"+SERVER_PORT+"/play_and_save/" + sostituisci(artist)+"/"+ sostituisci(album)+"/"+ sostituisci(track);
        }

    }



    public void play(String artist, String track, final VolleyCallback callback) {
        String url = "http://" + SERVER_IP + ":"+SERVER_PORT+"/play/" + sostituisci(artist)+"/"+sostituisci(track);
        call(url, callback);
    }


    public static String sostituisci(String s){
        s=s.replaceAll(" ", "%20");
        s=s.replaceAll("/","%2F");
        return s;
    }

    public void cancel() {
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public void setOffset(int i) {
        this.offset=i;
    }


    public interface VolleyCallback {
        void onSuccess(JSONObject result);
    }

}