package com.spotimyandroid.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Album;
import com.spotimyandroid.resources.Artist;
import com.spotimyandroid.resources.Track;

import org.json.JSONArray;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;


import static com.spotimyandroid.utils.StringsValues.BROADCAST_NEXT;
import static com.spotimyandroid.utils.StringsValues.BROADCAST_PLAY;

/**
 * Created by Jacopo on 13/03/2018.
 */


public class ApplicationSupport extends Application  implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {
    private MediaPlayer mp = new MediaPlayer();
    private ArrayList<Track> queue = new ArrayList<>();
    private int pointer=0;
    public String state;

    private ArrayList<Track> recentTracks = new ArrayList<>(5);
    private ArrayList<Album> recentAlbums= new ArrayList<>(5);;
    private ArrayList<Artist> recentArtists= new ArrayList<>(5);;


    public void prepare(){
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = null;
            aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mp.setAudioAttributes(aa);
        }
        else   {

            mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        }

        mp.setOnCompletionListener(this);

    }

    public MediaPlayer getMP() {
        return mp;
    }

    public void setMP(MediaPlayer mp) {
        this.mp = mp;
    }



    public int getLenghtQueue(){
        return queue.size();
    }

    public Track getCurrentTrack(){
        return queue.size()<=pointer? new Track() : queue.get(pointer);
    }


    public Track getNextTrack(){
        return (++pointer < queue.size()) ? queue.get(pointer) : null;
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
        System.out.println(pointer);
        System.out.println(queue.size());
        if ( (++pointer < queue.size())) {
            state=StringsValues.DOWNLOADING;
            Track t = queue.get(pointer);
            String query = t.getName() + " - " + t.getArtist();
            query = query.replace(" ", "%20");
            try {
                Intent i = new Intent(BROADCAST_NEXT);
                i.putExtra("next_track", true);
                sendBroadcast(i);
                mediaPlayer.setDataSource(Api.getTrackURL(getCurrentTrack().getArtist(),getCurrentTrack().getAlbum(), getCurrentTrack().getName()));
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

    public void play() {
        addTrack();

        if (mp.isPlaying()) mp.stop();
        mp.reset();
        try {
            state=StringsValues.DOWNLOADING;
            mp.setDataSource(Api.getTrackURL(getCurrentTrack().getArtist(),getCurrentTrack().getAlbum(), getCurrentTrack().getName()));
            mp.prepare();
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
            play();
        }

    }

    public void previousTrack() {
        if(state==StringsValues.PLAY) {
            if (pointer - 1 < 0) return;
            pointer--;
            play();
        }
    }

    public int getPosition() {
        return pointer;
    }

    public void addTrack(){
        if(recentTracks.size()==5){
            recentTracks.set(0,recentTracks.get(1));
            recentTracks.set(1,recentTracks.get(2));
            recentTracks.set(2,recentTracks.get(3));
            recentTracks.set(3,recentTracks.get(4));
            recentTracks.add(getCurrentTrack());
        }
        else recentTracks.add(getCurrentTrack());
        String str="";
        for(int i= recentTracks.size()-1; i>=0 ; i--) {
            Track t=recentTracks.get(i);
            if(i==recentTracks.size()-1) str= str.concat(t.toString());
            else str=str.concat(",,,"+t.toString());
        }
        SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
        prefEditor.putString("tracks", str);
        prefEditor.commit();
        System.out.println("PLAY "+str);


    }

    public void addAlbum(Album info) {
        if(recentAlbums.size()==5){
            recentAlbums.set(0,recentAlbums.get(1));
            recentAlbums.set(1,recentAlbums.get(2));
            recentAlbums.set(2,recentAlbums.get(3));
            recentAlbums.set(3,recentAlbums.get(4));
            recentAlbums.add(info);
        }
        else recentAlbums.add(info);
        String str="";
        for(int i= recentAlbums.size()-1; i>=0 ; i--) {
            Album t=recentAlbums.get(i);
            if(i==recentAlbums.size()-1) str= str.concat(t.toString());
            else str=str.concat(",,,"+t.toString());
        }
        SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
        prefEditor.putString("albums", str);
        prefEditor.commit();
        System.out.println("ALBUM "+str);
    }

    public void addArtist(Artist info) {
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
        System.out.println("ARTIST "+str);
    }
}