package com.spotimyandroid.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;


import com.spotimyandroid.http.CallHelper;
import com.spotimyandroid.resources.MyAlbum;
import com.spotimyandroid.resources.MyArtist;
import com.spotimyandroid.resources.MyTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.TrackSimple;
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

    private ArrayList<MyTrack> queue = new ArrayList<>();
    private ArrayList<MyTrack> recentTracks = new ArrayList<>(5);
    private ArrayList<MyAlbum> recentAlbums= new ArrayList<>(5);;
    private ArrayList<MyArtist> recentArtists= new ArrayList<>(5);;
    private String token;
    private AlbumSimple album;
    public SpotifyService spotify;
    public ArrayList<MyTrack> oldqueue;


    public void prepare(){
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
            MyTrack track = queue.get(pointer);
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

    public MyTrack getCurrentTrack(){
        if(pointer<0 || pointer>=queue.size()) return null;
        return  queue.get(pointer);
    }

    public void newQueue(List<MyTrack> tracks) {
        queue = new ArrayList<>();
        queue.addAll(tracks);
        pointer=0;
    }

    public ArrayList<MyTrack> getQueue() {
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
        state=StringsValues.DOWNLOADING;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "audio/mp3"); // change content type if necessary
        headers.put("Accept-Ranges", "bytes");
        headers.put("Status", "206");
        headers.put("Cache-control", "no-cache");
        CallHelper apiHelper = new CallHelper(getApplicationContext());
        apiHelper.findTracks(getCurrentTrack().getArtist() + " " + getCurrentTrack().getName(), new CallHelper.onMusicCallback() {
            @Override
            public void onSuccess(String url) {
                try {
                    System.out.println(url);
                    mp.setDataSource(url);
                    System.out.println("setDatasource");
                    mp.prepare();
                    //mp3 will be started after completion of preparing...
//                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                        @Override
//                        public void onPrepared(MediaPlayer player) {
//                            System.out.println("preparate");
//                            player.start();
//                            state=StringsValues.PLAY;
//                            Intent i = new Intent(BROADCAST_PLAY);
//                            i.putExtra("next_track", true);
//                            sendBroadcast(i);
//                        }
//
//                    });
                    System.out.println("preparate");
                    mp.start();
                    state=StringsValues.PLAY;
                    Intent i = new Intent(BROADCAST_PLAY);
                    i.putExtra("next_track", true);
                    sendBroadcast(i);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

            }
        });
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
        spotify.getAlbum(getCurrentTrack().getAlbumid(), new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                System.out.println("successo");
                if(recentTracks.contains(getCurrentTrack())){
                    return;
                }

                if(recentTracks.size()==5){
                    recentTracks.remove(0);
                    recentTracks.add(getCurrentTrack());
                }
                else recentTracks.add(getCurrentTrack());
                String str="";
                for(int i= recentTracks.size()-1; i>=0 ; i--) {
                    MyTrack t=recentTracks.get(i);
                    if(i==recentTracks.size()-1) str= str.concat(t.toString());
                    else str=str.concat(",,,"+t.toString());
                }
                SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
                prefEditor.putString("tracks", str);
                prefEditor.commit();
                System.out.println(str);
                addAlbum(new MyAlbum(album));
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });



    }

    public void addAlbum(final MyAlbum info) {
        spotify.getArtist(getCurrentTrack().getArtistid(), new Callback<Artist>() {
            @Override
            public void success(Artist artist, Response response) {
                if(recentAlbums.contains(info)) return;
                if (recentAlbums.size() == 5) {
                    recentAlbums.set(0, recentAlbums.get(1));
                    recentAlbums.set(1, recentAlbums.get(2));
                    recentAlbums.set(2, recentAlbums.get(3));
                    recentAlbums.set(3, recentAlbums.get(4));
                    recentAlbums.add(info);
                } else recentAlbums.add(info);
                String str = "";
                for (int i = recentAlbums.size() - 1; i >= 0; i--) {
                    MyAlbum t = recentAlbums.get(i);
                    if (i == recentAlbums.size() - 1) str = str.concat(t.toString());
                    else str = str.concat(",,," + t.toString());
                }
                SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
                prefEditor.putString("albums", str);
                prefEditor.commit();
                System.out.println(str);
                addArtist(new MyArtist(artist));
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });


    }

    public void addArtist(MyArtist info) {
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
            MyArtist t=recentArtists.get(i);
            if(i==recentArtists.size()-1) str= str.concat(t.toString());
            else str=str.concat(",,,"+t.toString());
        }
        SharedPreferences.Editor prefEditor = getSharedPreferences("recent", Context.MODE_PRIVATE).edit();
        prefEditor.putString("artists", str);
        prefEditor.commit();
    }

    public void addRecentTracks(List<MyTrack> tracks) {
        for (MyTrack track: tracks) {
            if(!recentTracks.contains(track)){
                recentTracks.add(track);
            }
        }
    }

    public void addRecentAlbums(List<MyAlbum> albums) {
        for (MyAlbum album: albums) {
            if(!recentAlbums.contains(album)){
                recentAlbums.add(album);
            }
        }
    }

    public void addRecentArtists(List<MyArtist> artists) {
        for (MyArtist artist: artists) {
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


    public void setAlbum(AlbumSimple album) {
        this.album = album;
    }

    public AlbumSimple getAlbum() {
        return album;
    }

    public void addQueue(MyTrack myTrack) {
        queue.add(myTrack);
    }

    public void removeQueue(MyTrack myTrack) {
        queue.remove(myTrack);
    }

}