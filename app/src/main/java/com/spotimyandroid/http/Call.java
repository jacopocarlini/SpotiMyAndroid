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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacopo on 11/03/2018.
 */


public class Call {
    public static final String TAG = "API";
    private Context context;
    private RequestQueue queue;
    private int offset;


    public Call(Context context) {
        this.context = context;
        // Get a RequestQueue
        queue = RequestQueue_Singeton.getInstance(context).getRequestQueue();
    }


    private void callHTML(String url, final String query, final HTMLCallback callback) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                             callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onError(error.getMessage());
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("q", query);
                    params.put("sort", "2");
                    params.put("count", "300");
                    params.put("performer_only", "0");
                    return params;
                }
            };

            RequestQueue_Singeton.getInstance(context).addToRequestQueue(stringRequest);
        }
        catch (Exception e){

        }
    }

    public void findTrack(String query, final HTMLCallback callback) {
        String url = "https://my-free-mp3.net/api/search.php?";
        callHTML(url, query,callback);
    }


    private void callJSON(String url, final JSONCallback callback) {
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


    public void lyric(String artist, String track, final JSONCallback callback) {
        // Instantiate the RequestQueue.
        String url = "http://lyric-api.herokuapp.com/api/find/" + sostituisci(artist)+"/"+sostituisci(track);
        callJSON(url, callback);
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


    public interface JSONCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }

    public interface HTMLCallback {
            void onSuccess(String result);
            void onError(String error);
    }
}