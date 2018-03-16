package com.spotimyandroid;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Track;
import com.spotimyandroid.utils.ApplicationSupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.media.AudioAttributes.CONTENT_TYPE_MUSIC;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class PlayerActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private ImageButton previous;
    private ImageButton pause;
    private ImageButton next;
    private TextView track;
    private TextView album;
    private TextView artist;
    private TextView lyric;
    private ImageView cover;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Track trackInfo;
    private Api server;
    private ApplicationSupport as;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
//        trackInfo = intent.getParcelableExtra("track");


        as = ((ApplicationSupport) this.getApplication());
        mediaPlayer = as.getMP();
        server = new Api(this);
        trackInfo = as.getCurrentTrack();
        Log.d("PlayerActivity", trackInfo.toString());
        System.out.println("QUI: "+trackInfo.toString());

        initiview();


        AsyncTask downloadSong = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                try {

                        as.resetQueue();
                        as.addTrackToQueue(trackInfo);
                        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                        mediaPlayer.reset();
                        String query = trackInfo.getName() + " - " + trackInfo.getArtist();
                        query = query.replace(" ", "%20");
                        mediaPlayer.setDataSource(server.getTrackURL(query));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                System.out.println("seekto"+ seekBar.getProgress());

                                int duration = mediaPlayer.getDuration();
                                System.out.println(duration * seekBar.getProgress() / 100);
                                mediaPlayer.seekTo(duration * seekBar.getProgress() / 100);



                            }
                        });

                    primaryProgressBarUpdater();




                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        downloadSong.execute();



    }

    private void initiview() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(99); // It means 100% .0-99

        pause=(ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    pause.setImageResource(android.R.drawable.ic_media_play);
                    mediaPlayer.pause();
                }
                else{
                    pause.setImageResource(android.R.drawable.ic_media_pause);
                    mediaPlayer.start();
                }
            }
        });

        track = (TextView) findViewById(R.id.track);
        album = (TextView) findViewById(R.id.album);
        artist = (TextView) findViewById(R.id.artist);
        lyric = (TextView) findViewById(R.id.lyric);
        lyric.setMovementMethod(new ScrollingMovementMethod());

        track.setText(trackInfo.getName());
        album.setText(trackInfo.getAlbum());
        artist.setText(trackInfo.getArtist());
        server.lyric(trackInfo.getArtist(), trackInfo.getName(), new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    lyric.setText(parseLyric(result.getString("lyric")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cover=(ImageView) findViewById(R.id.cover);
        System.out.println(trackInfo);
        if (trackInfo.hasCover()){
            Glide.with(this).load(trackInfo.getCover()).into(cover);

        }




    }


    private void primaryProgressBarUpdater() {
        if (mediaPlayer==null) return;
        seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primaryProgressBarUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }


    private String parseLyric(String s){
//        System.out.println(s);
       s=s.replaceAll("&#xE8;","è");
       s=s.replaceAll("&#xE9;","é");
       s=s.replaceAll("&#xF2;","ò");
       s=s.replaceAll("&#xE0;","à");
       s=s.replaceAll("&#xEC;","ì");
       s=s.replaceAll("&#xF9;","ù");
       return s;
    }



}
