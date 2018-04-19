package com.spotimyandroid.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;

import com.google.gson.Gson;
import com.spotimyandroid.http.ApiHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.spotimyandroid.utils.StringsValues.BROADCAST_NEXT;
import static com.spotimyandroid.utils.StringsValues.BROADCAST_PLAY;

/**
 * Created by Jacopo on 13/03/2018.
 */


public class ApplicationSupport extends Application  implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener{

    private MediaPlayer mp;
    private int pointer=0;
    public String state;
    private ArrayList<String> pathfiles;
    private ArrayList<Track> queue = new ArrayList<>();
    private ArrayList<Track> recentTracks = new ArrayList<>(5);
    private ArrayList<Album> recentAlbums= new ArrayList<>(5);;
    private ArrayList<Artist> recentArtists= new ArrayList<>(5);;
    private String token;
    private ApiHelper apiHelper;
    private SpotifyService spotify;


    public void prepare(){
        apiHelper =new ApiHelper(getApplicationContext());
        mp = new MediaPlayer();
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                System.out.println(i);
                System.out.println(i1);
                System.out.println("errore del media player");
                return false;
            }
        });

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnCompletionListener(this);
        mp.reset();

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        state=StringsValues.FINISH;
        System.out.println("COMPLETATO");
        mp.stop();
        mp.reset();
        if ( (++pointer < queue.size())) {
            state=StringsValues.DOWNLOADING;
            Track track = queue.get(pointer);
            try {
                Intent i = new Intent(BROADCAST_NEXT);
                i.putExtra("next_track", true);
                sendBroadcast(i);
                mediaPlayer.setDataSource("");
                mp.prepare();
                mp.start();
                state=StringsValues.PLAY;
                i = new Intent(BROADCAST_PLAY);
                i.putExtra("play", true);
                sendBroadcast(i);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MediaPlayer getMP() {
        return mp;
    }


    public int getLenghtQueue(){
        return queue.size();
    }

    public Track getCurrentTrack(){
        if(pointer<0 || pointer>=queue.size()) return null;
        return  queue.get(pointer);
    }

    public void newQueue(Track[] tracks) {
        queue = new ArrayList<>(Arrays.asList(tracks));
        pointer=0;
    }

    public ArrayList<Track> getQueue() {
        return queue;
    }


    public void setPosition(int position) {
        this.pointer = position;
    }

    public void play(ArrayList<String> pathfiles, int pos) {
        System.out.println("play");
        this.pathfiles=pathfiles;
        String s = pathfiles.get(pos);
        System.out.println(s);
        addTrack();


        if (mp.isPlaying()) mp.stop();
        mp.reset();

        try {
            System.out.println(s);
            mp.setDataSource(s);
            mp.prepare();
            System.out.println("setDatasource");
            //mp3 will be started after completion of preparing...

            System.out.println("preparate");
            mp.start();
            state=StringsValues.PLAY;
            Intent i = new Intent(BROADCAST_PLAY);
            i.putExtra("next_track", true);
            sendBroadcast(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextTrack() {
        if (state == StringsValues.PLAY) {
            if (pointer + 1 > getLenghtQueue()) return;
            pointer++;
//            play();
        }

    }


    public void previousTrack() {
//        if(state==StringsValues.PLAY) {
//            if (pointer - 1 < 0) return;
//            pointer--;
//            play(torrent.getFileNames()[trackInfo.track_number]);
//        }
    }

    public int getPosition() {
        return pointer;
    }

    public void addTrack(){
        if(recentTracks.contains(getCurrentTrack())){
          return;
        }
        if(recentTracks.size()==5){
            recentTracks.remove(0);
            recentTracks.add(getCurrentTrack());
        }
        else recentTracks.add(getCurrentTrack());
        SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(recentTracks);
        prefEditor.putString("tracks", json);
        prefEditor.commit();




    }

    public void addAlbum(final Album info) {
        if(recentAlbums.contains(info)) return;

//        server = new Api(getApplicationContext());
//        server.findArtist(info.getArtist(), new Api.VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//                Artist artist = Artist.toArray(result)[0];
//                System.out.println(artist);
//                if (recentAlbums.size() == 5) {
//                    recentAlbums.set(0, recentAlbums.get(1));
//                    recentAlbums.set(1, recentAlbums.get(2));
//                    recentAlbums.set(2, recentAlbums.get(3));
//                    recentAlbums.set(3, recentAlbums.get(4));
//                    recentAlbums.add(info);
//                } else recentAlbums.add(info);
//                String str = "";
//                for (int i = recentAlbums.size() - 1; i >= 0; i--) {
//                    Album t = recentAlbums.get(i);
//                    if (i == recentAlbums.size() - 1) str = str.concat(t.toString());
//                    else str = str.concat(",,," + t.toString());
//                }
//                SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
//                prefEditor.putString("albums", str);
//                prefEditor.commit();
//                addArtist(artist);
//            }
//        });
    }

    public void addArtist(Artist info) {
        if(recentArtists.contains(info)) {
            System.out.println("artista già presente nei recenti");
            return;
        }
        if(recentArtists.size()==5){
            recentArtists.set(0,recentArtists.get(1));
            recentArtists.set(1,recentArtists.get(2));
            recentArtists.set(2,recentArtists.get(3));
            recentArtists.set(3,recentArtists.get(4));
            recentArtists.add(info);
        }
        else recentArtists.add(info);
        String str="";
        for(int i= recentArtists.size()-1; i>=0 ; i--) {
            Artist t=recentArtists.get(i);
            if(i==recentArtists.size()-1) str= str.concat(t.toString());
            else str=str.concat(",,,"+t.toString());
        }
        SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
        prefEditor.putString("artists", str);
        prefEditor.commit();
    }

    public void addRecentTracks(List<Track> tracks) {
        recentTracks.addAll(tracks);
    }

    public void addRecentAlbums(Album[] albums) {
        for (Album album: albums) {
            if(!recentAlbums.contains(album)){
                recentAlbums.add(album);
            }
        }
    }

    public void addRecentArtists(Artist[] artists) {
        for (Artist artist: artists) {
            if(!recentArtists.contains(artist)){
                recentArtists.add(artist);
            }
        }
    }


    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


    public void setSpotify(SpotifyService spotify) {
        this.spotify = spotify;
    }
}