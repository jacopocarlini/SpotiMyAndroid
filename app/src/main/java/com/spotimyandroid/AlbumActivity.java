package com.spotimyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spotimyandroid.resources.MyAlbum;
import com.spotimyandroid.resources.MyTrack;
import com.spotimyandroid.utils.ApplicationSupport;
import com.spotimyandroid.utils.BottomNavigationViewHelper;
import com.spotimyandroid.utils.StringsValues;

import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jacopo on 13/03/2018.
 */

public class AlbumActivity extends AppCompatActivity{
    private TextView albumView;
    private TextView artistView;
    private LinearLayout tracksView;
    private List<TrackSimple> tracks;
    private MyAlbum albumInfo;
    private ImageView cover;
    private ApplicationSupport app;
    private MediaPlayer mediaPlayer;
    private BroadcastReceiver mReceiverPlay;
    private ImageView pause;
    private ImageView player;
    public Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        app = (ApplicationSupport) this.getApplication();
        Intent intent = getIntent();
        albumInfo = intent.getParcelableExtra("album");
        mediaPlayer = app.getMP();
        app.spotify.getAlbum(albumInfo.getId(), new Callback<Album>() {
            @Override
            public void success(Album album1, Response response) {
                album = album1;
                tracks = album.tracks.items;
                albumInfo.setArtist(album1.artists.get(0).name);

                initview();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        mReceiverPlay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Do what you need in here
                LinearLayout playerBar = (LinearLayout) findViewById(R.id.playerBar);
                playerBar.setVisibility(View.VISIBLE);
                if(mediaPlayer.isPlaying()) {
                    pause.setImageResource(R.drawable.ic_pause_black_24dp);
                }
                else{
                    pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        player();
        setIcons();
        registerReceiver(mReceiverPlay, new IntentFilter(StringsValues.BROADCAST_PLAY));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiverPlay);
        super.onDestroy();
    }


    private void initview() {
        albumView = (TextView) findViewById(R.id.album);
        albumView.setText(albumInfo.getName());
        artistView = (TextView) findViewById(R.id.artist);
        artistView.setText(albumInfo.getArtist());
        tracksView = (LinearLayout) findViewById(R.id.tracks);
        tracksView.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i=0; i<tracks.size();i++){
            View elem = inflater.inflate(R.layout.item_album_track, null);
            final TextView track = (TextView) elem.findViewById(R.id.track);
            track.setText(tracks.get(i).name);
            TextView position = (TextView) elem.findViewById(R.id.position);
            position.setText(Integer.toString(i+1));
            final int finalI = i;
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    app.newQueue(MyTrack.toArraySimple(tracks, album));
                    app.setPosition(finalI);

                    intent.putExtra("action","play");
                    startActivity(intent);
                }
            });
            final ImageView queue = elem.findViewById(R.id.queue);

            tracksView.addView(elem);

        }

        cover = (ImageView) findViewById(R.id.cover);
        if (albumInfo.hasCover())
            Glide.with(this).load(albumInfo.getCover()).into(cover);


        setBottomBar();
        player();

    }

    private void setIcons(){
        if(tracks==null || album==null) return;
        final List<MyTrack> tracks1= MyTrack.toArraySimple(tracks, album);
        int count = tracksView.getChildCount();
        View elem = null;
        for(int i=0; i<count; i++) {
            elem = tracksView.getChildAt(i);
            final ImageView queue = elem.findViewById(R.id.queue);
            if (app.getQueue().contains(tracks.get(i))) {
                queue.setImageResource(R.drawable.ic_library_books_white_24dp);
                queue.setColorFilter(getResources().getColor(R.color.blue));
            } else {
                queue.setImageResource(R.drawable.ic_queue_white_24dp);
            }
            final int finalI = i;
            queue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!app.getQueue().contains(tracks.get(finalI))) {
                        app.addQueue(tracks1.get(finalI));
                        queue.setImageResource(R.drawable.ic_library_books_white_24dp);
                        queue.setColorFilter(getResources().getColor(R.color.blue));
                    } else {
                        app.removeQueue(tracks1.get(finalI));
                        queue.setImageResource(R.drawable.ic_queue_white_24dp);
                        queue.setColorFilter(getResources().getColor(R.color.white));
                    }
                }
            });
        }

    }



    public void player(){
        LinearLayout playerBar = (LinearLayout) findViewById(R.id.playerBar);
        if(app.state== StringsValues.PLAY) playerBar.setVisibility(View.VISIBLE);
        else playerBar.setVisibility(View.INVISIBLE);
        pause=(ImageButton) findViewById(R.id.pause);
        if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
            pause.setImageResource(R.drawable.ic_pause_black_24dp);
        }
        else{
            pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    mediaPlayer.pause();
                }
                else if(app.getLenghtQueue()>0){
                    pause.setImageResource(R.drawable.ic_pause_black_24dp);
                    mediaPlayer.start();
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

        this.player = (ImageView) findViewById(R.id.player);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("action","openonly");
                startActivity(intent);
            }
        });
    }

    private void setBottomBar(){
//        LinearLayout home = (LinearLayout) findViewById(R.id.home);
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });
        LinearLayout discover = (LinearLayout) findViewById(R.id.discover);
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout profile = (LinearLayout) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout settings = (LinearLayout) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

    }


}
