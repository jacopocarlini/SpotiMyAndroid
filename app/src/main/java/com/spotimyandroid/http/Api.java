package com.spotimyandroid.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 11/03/2018.
 */


public class Api {
    public static final String TAG = "API";
    private RequestQueue queue;
    private int offset;
    private Context context;


    public Api(Context context) {
        // Get a RequestQueue
        this.context=context;
        queue = RequestQueue_Singeton.getInstance(context).getRequestQueue();
    }

    private void callHTML(String url, final HTMLCallback callback) {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void call(String url, final VolleyCallback callback) {
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
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


    public void lyric(String artist, String track, final VolleyCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://lyric-api.herokuapp.com/api/find/" + sostituisci(artist)+"/"+sostituisci(track);
        call(url, callback);
    }

    public void torrentX1337(String query, final HTMLCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://1337x.to/search/"+ sostituisci(query) +"/1/";
        callHTML(url, callback);
    }
    public void magnetX1337(String query, final HTMLCallback callback) {
        // Instantiate the RequestQueue.

        String url = "http://1337x.to/"+ sostituisci(query);
        callHTML(url, callback);
    }

    public String torrentX1337URL(String artist, String album) {
        String url = "http://1337x.to/search/"+ sostituisci(artist)+"%20"+sostituisci(album) +"/1/";
        return url;
    }

    public String magnetX1337URL(String link) {
        String url = "http://1337x.to"+ link;
        return url;
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

    public interface HTMLCallback {
        void onSuccess(String result);
        void onError(String error);
    }

}