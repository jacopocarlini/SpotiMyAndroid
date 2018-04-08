package com.spotimyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.frostwire.jlibtorrent.FileStorage;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.spotimyandroid.http.Api;
import com.spotimyandroid.http.ApiHelper;
import com.spotimyandroid.resources.MyTorrent;
import com.spotimyandroid.utils.ApplicationSupport;
import com.spotimyandroid.utils.StringsValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class PlayerActivity extends AppCompatActivity implements TorrentListener {

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
    private ApplicationSupport app;
    private AsyncTask downloadSong;
    private BroadcastReceiver mReceiverPlay;
    private BroadcastReceiver mReceiverNext;
    private ApiHelper apiHelper;
    private TorrentStream torrentStream;
    private List<MyTorrent> myTorrents;
    private int index;
    private ArrayList<File> torrentFiles;
    private int fileSelected;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        apiHelper = new ApiHelper(getApplicationContext());
        app = ((ApplicationSupport) this.getApplication());
        mediaPlayer = app.getMP();
        trackInfo = app.getCurrentTrack();

        initiview();

        Intent i = getIntent();
        String info = i.getStringExtra("info");
        if(!info.equals("openonly")) {  //if info is equals to play then play song
            apiHelper.scraper(app.getCurrentTrack().artists.get(0).name, app.getCurrentTrack().album.name,
                    new ApiHelper.onTorrentCallback() {

                        @Override
                        public void onSuccess(List<MyTorrent> myTorrentsFound) {
                            myTorrents=myTorrentsFound;
//                            search(myTorrents, index=0);
                            TorrentOptions torrentOptions = new TorrentOptions.Builder()
                                    .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                                    .removeFilesAfterStop(true)
                                    .build();

                            torrentStream = TorrentStream.init(torrentOptions);
                            torrentStream.addListener(PlayerActivity.this);
                            torrentStream.startStream(myTorrents.get(0).getMagnet());
                        }

                        @Override
                        public void onError(String err) {

                        }
                    });

        }


        mReceiverPlay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Do what you need in here
                trackInfo = app.getCurrentTrack();
                initiview();
                primaryProgressBarUpdater();
                enableSeek();
            }
        };

        mReceiverNext = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Do what you need in here
                trackInfo = app.getCurrentTrack();
                initiview();
                primaryProgressBarUpdater();
                enableSeek();

            }
        };


    }

    private void search(List<MyTorrent> myTorrents, int i){
        System.out.println("search");
        System.out.println(myTorrents);
        System.out.println(i);
        if(i>myTorrents.size()) return;
        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        torrentStream = TorrentStream.init(torrentOptions);
        torrentStream.addListener(PlayerActivity.this);
        torrentStream.startStream(myTorrents.get(i).getMagnet());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiverPlay, new IntentFilter(StringsValues.BROADCAST_PLAY));
        registerReceiver(mReceiverNext, new IntentFilter(StringsValues.BROADCAST_NEXT));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiverPlay);
        unregisterReceiver(mReceiverNext);
        super.onDestroy();
    }



    private void initiview() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(99); // It means 100% .0-99
        seekBar.setProgress(0);


        pause=(ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(app.state==StringsValues.PLAY){
                    if (mediaPlayer.isPlaying()) {
                        pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        mediaPlayer.pause();
                    } else {
                        pause.setImageResource(R.drawable.ic_pause_black_24dp);
                        mediaPlayer.start();
                        primaryProgressBarUpdater();
                    }
                }
            }
        });

        ImageView next = (ImageView) findViewById(R.id.next);
        ImageView previous = (ImageView) findViewById(R.id.previous);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.nextTrack();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.previousTrack();
            }
        });

        track = (TextView) findViewById(R.id.track);
        album = (TextView) findViewById(R.id.album);
        artist = (TextView) findViewById(R.id.artist);
        lyric = (TextView) findViewById(R.id.lyric);
        lyric.setMovementMethod(new ScrollingMovementMethod());

        track.setText(trackInfo.name);
        album.setText(trackInfo.album.name);
        artist.setText(trackInfo.artists.get(0).name);
        apiHelper.getLyric(trackInfo.artists.get(0).name, trackInfo.name, new ApiHelper.onLyricCallback() {
            @Override
            public void onSuccess(String lyricText) {
                lyric.setText(lyricText);
            }

            @Override
            public void onError(String err) {
                System.out.println(err);
            }
        });

        cover=(ImageView) findViewById(R.id.cover);
        if (!trackInfo.album.images.isEmpty()){
            Glide.with(this).load(trackInfo.album.images.get(0).url).into(cover);
        }

    }


    private void primaryProgressBarUpdater() {
        if (mediaPlayer==null) return;
        if(app.state!=StringsValues.PLAY) return;
        int x=(int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);


        seekBar.setProgress(x); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primaryProgressBarUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    private void enableSeek(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int duration = mediaPlayer.getDuration();
                mediaPlayer.seekTo(duration * seekBar.getProgress() / 100);



            }
        });


    }


    @Override
    public void onStreamPrepared(Torrent torrent) {
//        System.out.println("stream prepared");
////        ArrayList<File> f = new ArrayList<>();
////        listf(torrent.getSaveLocation().getAbsolutePath(), f);
////        System.out.println("filelist "+ f);
//        System.out.println(torrent.getTorrentHandle().getTorrentInfo().collections());
//        fileSelected=isPresent(torrent.getTorrentHandle().getTorrentInfo().origFiles(), trackInfo);
//        if (fileSelected!=-1) {
//            System.out.println("file trovato, indice" + fileSelected);
//            System.out.println("torrent trovato, indice" + index);
//            torrent.setSelectedFileIndex(fileSelected);
//            torrent.startDownload();
//        }
//        else{
//            System.out.println("non è il torrent che cerco");
//            torrentStream.stopStream();
//            search(myTorrents , index+1);
//        }

        torrent.startDownload();

    }

    @Override
    public void onStreamStarted(Torrent torrent) {


    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {

    }

    @Override
    public void onStreamReady(Torrent torrent) {
        fileSelected = isPresent(torrent, trackInfo);
//        app.play(torrentFiles.get(fileSelected).getAbsolutePath());
    }

    private int isPresent(Torrent torrent, Track track) {
        System.out.println("ispresent");
        ArrayList<File> files = new ArrayList<>();
        listf(torrent.getSaveLocation().getAbsolutePath(), files);
        torrentFiles=files;
        for(int i=0; i<torrentFiles.size(); i++){
            String filename = "";
            System.out.println(filename);
            String trackname = track.name.toLowerCase();
            String[] parts = trackname.split(" ");
            if (!(filename.indexOf(".mp3") > -1  || filename.indexOf(".wav") > -1 || filename.indexOf(".flac") > -1)) {
                return -1;
            }

            if (filename.indexOf(trackname) > -1) {
                // console.log("ha il nome che cerco")
                return i;
            }
            int total = parts.length;
            int count=0;
            for(int j=0; j<parts.length; j++){
                if (filename.indexOf(parts[j]) > -1){
                    count=count+1;
                }
            }
            // console.log("total="+total/2+" count="+count);
            if(count>=total/2){
                return i;
            }
            else  return -1;
            }

        return -1;
    }

    public void listf(String directoryName, ArrayList<File> files) {
        System.out.println("listf");
        System.out.println(directoryName);
        File directory = new File(directoryName);
        if(!directory.exists()) directory.mkdir();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }


    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {

    }

    @Override
    public void onStreamStopped() {

    }
}
