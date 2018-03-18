package com.spotimyandroid.utils;

import android.app.Application;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.spotimyandroid.PlayerActivity;
import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.spotimyandroid.ManageConection.BROADCAST_FILTER;

/**
 * Created by Jacopo on 13/03/2018.
 */


public class ApplicationSupport extends Application  implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {
    private MediaPlayer mp = new MediaPlayer();
    private ArrayList<Track> queue = new ArrayList<>();
    private int pointer=0;


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
        return queue.size()<=pointer? null : queue.get(pointer);
    }

//    public void addTrackToQueue(Track track) {
//        queue.add(track);
//    }

    public Track getNextTrack(){
        return (++pointer < queue.size()) ? queue.get(pointer) : null;
    }

//    public void resetQueue(){
//        pointer=0;
//        queue=new ArrayList<>();
//    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        System.out.println("COMPLETATO");
        mp.stop();
        mp.reset();
        System.out.println(pointer);
        System.out.println(queue.size());
        if ( (++pointer < queue.size())) {
            Track t = queue.get(pointer);
            String query = t.getName() + " - " + t.getArtist();
            query = query.replace(" ", "%20");
            try {
                mediaPlayer.setDataSource(Api.getTrackURL(getCurrentTrack().getArtist(), getCurrentTrack().getName()));
                mp.prepare();
                mp.start();
                Intent i = new Intent(BROADCAST_FILTER);
                i.putExtra("next_track", true);
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




}