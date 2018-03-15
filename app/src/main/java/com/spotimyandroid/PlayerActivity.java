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

import static android.media.AudioAttributes.CONTENT_TYPE_MUSIC;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {

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
        trackInfo = intent.getParcelableExtra("track");
//        System.out.println(value);

        mediaPlayer = ((ApplicationSupport) this.getApplication()).getMP();
        System.out.println("1");
        mediaPlayer.reset();

        System.out.println("2");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = null;
            aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(aa);
        }
        else   {

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        mediaPlayer.setOnCompletionListener(this);
        System.out.println("3");

        server = new Api(this);
        initiview();

//        String query = trackInfo.getName() +" - "+trackInfo.getArtist();
//        query=query.replace(" ","%20");
//        try {
//            mediaPlayer.setDataSource(server.getTrackURL(query));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();
//        primaryProgressBarUpdater();

        AsyncTask downloadSong = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    mediaPlayer.reset();
                    String query = trackInfo.getName() +" - "+trackInfo.getArtist();
                    query=query.replace(" ","%20");
                    mediaPlayer.setDataSource(server.getTrackURL(query));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
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
                System.out.println(duration *seekBar.getProgress()/100);

                mediaPlayer.seekTo(duration *seekBar.getProgress()/100);

            }
        });

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
                    lyric.setText(result.getString("lyric"));
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
//        System.out.println(mediaPlayer.getCurrentPosition());
//        System.out.println(mediaPlayer.getDuration());
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

    private void secondaryProgressBarUpdater(int i) {
        final int j = i;
        seekBar.setSecondaryProgress(i);
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                   secondaryProgressBarUpdater(j);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//        System.out.println("buff");
//        secondaryProgressBarUpdater(i);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }



}
