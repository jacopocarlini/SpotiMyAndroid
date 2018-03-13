package com.spotimyandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.ApplicationSupport;
import com.spotimyandroid.resources.Track;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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
        String value = intent.getStringExtra("song");
        trackInfo = intent.getParcelableExtra("track");
//        System.out.println(value);

        mediaPlayer = ((ApplicationSupport) this.getApplication()).getMP();

        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        server = new Api(this);
        initiview();

        try {
            mediaPlayer.setDataSource(server.getTrackURL(value));
            mediaPlayer.prepare();
            mediaPlayer.start();
            primaryProgressBarUpdater();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                mediaPlayer.seekTo(seekBar.getProgress());

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
        if (trackInfo.hasCover())
            new MainActivity.DownloadImageTask(cover).execute(trackInfo.getCover());


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
        secondaryProgressBarUpdater(i);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
