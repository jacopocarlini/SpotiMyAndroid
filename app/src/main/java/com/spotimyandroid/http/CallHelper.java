package com.spotimyandroid.http;

import android.content.Context;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CallHelper {
    private Context context;
    private RequestQueue queue;
    private Call mApi;


    public CallHelper(Context context) {
        this.context = context;
        mApi = new Call(context);
        // Get a RequestQueue
        queue = RequestQueue_Singeton.getInstance(context).getRequestQueue();
    }

    public void findTracks(String artist, String track, final onMusicCallback callback){
        artist = artist.replaceAll("\\s+$", "");
        track = track.split("-")[0];
        track = track.split("\\(")[0];
        track = track.split("\\[")[0];
        String query = artist+" "+track;
        System.out.println(query);
        final String finalArtist = artist;
        mApi.findTrack(query, new Call.HTMLCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    System.out.println(result);
                    result = result.substring(2,result.length()-2);
                    JSONObject response = new JSONObject(result+"\"");
                    if (response.getJSONArray("response").length()==0) callback.onSuccess("null");
//                    System.out.println(response.getJSONArray("response").getString(0));
                    for(int i=1; i< response.getJSONArray("response").length();i++){
                        JSONObject song = response.getJSONArray("response").getJSONObject(i);
                        System.out.println(song.getString("artist"));
                        if(song.getString("artist").toLowerCase().contains(finalArtist.toLowerCase())){
                            System.out.println("trovato artista");
                            int ownerId = song.getInt("owner_id");
                            int aid = song.getInt("id");
                            String prettyId = encode(ownerId) + ":" + encode(aid);
                            String url = "https://newtabs.stream/stream/"+prettyId;
                            if (exists(url)) {
                                callback.onSuccess(url);
                                return;
                            }
                            else{
                                continue;
                            }
                        }
                    }
                    callback.onSuccess("null");

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onSuccess("null");
                }
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });
    }
    public boolean exists(String file_url){
        URL url = null;
        try {
            url = new URL(file_url);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD");
            huc.connect () ;
            int code = huc.getResponseCode() ;
            System.out.println(code);

            if(code==200)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
    String[] map = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "x",
            "y", "z", "1", "2", "3"};

    public String encode(int input) {
        int length = map.length;
        String encoded = "";
        if (input == 0)
            return map[0];
        if (input < 0) {
            input *= -1;
            encoded += "-";
        };
        while (input > 0) {
            int val = (int)(input % length);
            input = (int)(input / length);
            encoded += map[val];
        }
        return encoded;
    }

    public void getLyric(String artist, String track, final onLyricCallback callback) {
        mApi.lyric(artist, track, new Call.JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    callback.onSuccess(parseLyric(result.getString("lyric")));
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public interface onLyricCallback{
        void onSuccess(String lyric);
        void onError(String err);
    }

    public interface onMusicCallback{
        void onSuccess(String url);
        void onError(String error);
    }

    private String parseLyric(String s){
        s=s.replaceAll("&#xE8;","è");
        s=s.replaceAll("&#xE9;","é");
        s=s.replaceAll("&#xF2;","ò");
        s=s.replaceAll("&#xF3;","ó");
        s=s.replaceAll("&#xE0;","à");
        s=s.replaceAll("&#xEC;","ì");
        s=s.replaceAll("&#xF9;","ù");
        s=s.replaceAll("&#x2019;","'");
        return s;
    }
}
