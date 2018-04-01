package com.spotimyandroid.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Album;
import com.spotimyandroid.resources.Artist;
import com.spotimyandroid.resources.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import static com.spotimyandroid.utils.StringsValues.BROADCAST_NEXT;
import static com.spotimyandroid.utils.StringsValues.BROADCAST_PLAY;

/**
 * Created by Jacopo on 13/03/2018.
 */


public class ApplicationSupport extends Application  implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener{
    private MediaPlayer mp;
    private ArrayList<Track> queue = new ArrayList<>();
    private int pointer=0;
    public String state;

    private ArrayList<Track> recentTracks = new ArrayList<>(5);
    private ArrayList<Album> recentAlbums= new ArrayList<>(5);;
    private ArrayList<Artist> recentArtists= new ArrayList<>(5);;
    private Api server;


    public void prepare(){
        server=new Api(getApplicationContext());
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
        mp.reset();

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
        if(pointer<0 || pointer>=queue.size()) return null;
        return  queue.get(pointer);
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
        System.out.println("play");
        addTrack();

        if (mp.isPlaying()) mp.stop();
        mp.reset();
//        try {
            state=StringsValues.DOWNLOADING;
            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "audio/mp3"); // change content type if necessary
            headers.put("Accept-Ranges", "bytes");
            headers.put("Status", "206");
            headers.put("Cache-control", "no-cache");
//            server.getTrackURL2(getCurrentTrack().getArtist(), getCurrentTrack().getAlbum(), getCurrentTrack().getName(), new Api.VolleyCallback() {
//                @Override
//                public void onSuccess(JSONObject result) {
//                    String filepath=result.toString();
//                    System.out.println("file path"+filepath);
//                    server.torrent(filepath, new Api.VolleyCallback(){
//
//
//                        @Override
//                        public void onSuccess(JSONObject result) {
//                            try {
//                                System.out.println(result.toString());
//                                mp.setDataSource(result.toString());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            System.out.println("setDatasource");
//                            mp.prepareAsync();
//                            //mp3 will be started after completion of preparing...
//                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                                @Override
//                                public void onPrepared(MediaPlayer player) {
//                                    System.out.println("preparate");
//                                    player.start();
//                                    state=StringsValues.PLAY;
//                                    Intent i = new Intent(BROADCAST_PLAY);
//                                    i.putExtra("next_track", true);
//                                    sendBroadcast(i);
//                                }
//
//                            });
//                        }
//                    });
//                }
//            });
            String url = Api.getTrackURL(getCurrentTrack().getArtist(),getCurrentTrack().getAlbum(), getCurrentTrack().getName());
            Uri uri = Uri.parse(url);
//            mp.setDataSource(getApplicationContext() , uri, headers);
        try {
            mp.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("setDatasource");
            mp.prepareAsync();
            //mp3 will be started after completion of preparing...
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer player) {
                    System.out.println("preparate");
                    player.start();
                    state=StringsValues.PLAY;
                    Intent i = new Intent(BROADCAST_PLAY);
                    i.putExtra("next_track", true);
                    sendBroadcast(i);
                }

            });
//            System.out.println("preparate");
//            mp.start();
//            state=StringsValues.PLAY;
//            Intent i = new Intent(BROADCAST_PLAY);
//            i.putExtra("next_track", true);
//            sendBroadcast(i);

//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



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
        if(recentTracks.contains(getCurrentTrack())){
          return;
        }
        server = new Api(getApplicationContext());
        server.findArtist(getCurrentTrack().getArtist(), new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                getCurrentTrack().addArtistImage(result);
                System.out.println("attualmente ci sono "+recentTracks.size()+ " tracce recenti");
                if(recentTracks.size()==5){
                    recentTracks.remove(0);
                    recentTracks.add(getCurrentTrack());
                }
                else recentTracks.add(getCurrentTrack());
                String str="";
                for(int i= recentTracks.size()-1; i>=0 ; i--) {
                    Track t=recentTracks.get(i);
                    if(i==recentTracks.size()-1) str= str.concat(t.toString());
                    else str=str.concat(",,,"+t.toString());
                }
                System.out.println("salvo la stringa "+str);
                SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
                prefEditor.putString("tracks", str);
                prefEditor.commit();
                addAlbum(new Album(getCurrentTrack().getAlbumid(), getCurrentTrack().getAlbum(),
                            getCurrentTrack().getArtist(), getCurrentTrack().getCover()));
            }
        });


    }

    public void addAlbum(final Album info) {
        if(recentAlbums.contains(info)) return;
        server = new Api(getApplicationContext());
        server.findArtist(info.getArtist(), new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Artist artist = Artist.toArray(result)[0];
                System.out.println(artist);
                if (recentAlbums.size() == 5) {
                    recentAlbums.set(0, recentAlbums.get(1));
                    recentAlbums.set(1, recentAlbums.get(2));
                    recentAlbums.set(2, recentAlbums.get(3));
                    recentAlbums.set(3, recentAlbums.get(4));
                    recentAlbums.add(info);
                } else recentAlbums.add(info);
                String str = "";
                for (int i = recentAlbums.size() - 1; i >= 0; i--) {
                    Album t = recentAlbums.get(i);
                    if (i == recentAlbums.size() - 1) str = str.concat(t.toString());
                    else str = str.concat(",,," + t.toString());
                }
                SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
                prefEditor.putString("albums", str);
                prefEditor.commit();
                addArtist(artist);
            }
        });
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

    public void addRecentTracks(Track[] tracks) {
        for (Track track: tracks) {
            if(!recentTracks.contains(track)){
                recentTracks.add(track);
            }
        }
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


}