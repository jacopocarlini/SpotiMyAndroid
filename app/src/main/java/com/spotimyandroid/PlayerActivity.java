package com.spotimyandroid;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.spotimyandroid.http.Api;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener {

    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        String value = intent.getStringExtra("song");
        System.out.println(value);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        initiview();
        Api server = new Api(this);
        try {
            mediaPlayer.setDataSource(server.getTrackURL(value));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        server.play(value, new Api.VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//                mediaPlayer.setDataSource(result);
//            }
//        });

    }

    private void initiview() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(99); // It means 100% .0-99
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
