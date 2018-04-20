package com.spotimyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spotimyandroid.resources.MyAlbum;
import com.spotimyandroid.resources.MyArtist;
import com.spotimyandroid.resources.MyTrack;
import com.spotimyandroid.utils.ApplicationSupport;
import com.spotimyandroid.utils.BottomNavigationViewHelper;
import com.spotimyandroid.utils.StringsValues;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class ArtistActivity extends AppCompatActivity {

    private MyArtist artistInfo;
    private TextView name;
    private ImageView image;
    private LinearLayout popular;
    private GridLayout albums;
    private ApplicationSupport app;
    private ImageButton pause;
    private MediaPlayer mediaPlayer;
    private BroadcastReceiver mReceiverPlay;
    private ImageView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        app = (ApplicationSupport) this.getApplication();

        Intent intent = getIntent();
        artistInfo = intent.getParcelableExtra("artist");
        mediaPlayer = app.getMP();

        initview();

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
        registerReceiver(mReceiverPlay, new IntentFilter(StringsValues.BROADCAST_PLAY));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiverPlay);
        super.onDestroy();
    }

    private void initview() {
        this.name = (TextView) findViewById(R.id.name);
        this.image =(ImageView) findViewById(R.id.image);
        this.popular = (LinearLayout) findViewById(R.id.popular);
        this.albums = (GridLayout) findViewById(R.id.albums);

        name.setText(artistInfo.getName());
        Glide.with(this).load(artistInfo.getImage()).fitCenter().into(image);
        findAlbums();
        findTracks();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    return true;
                }
                if (item.getItemId() == R.id.settings) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.profile) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        player();
    }

    private void findTracks() {
        app.spotify.getArtistTopTrack(artistInfo.getId(), "IT", new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                final List<MyTrack> tracksInfo = MyTrack.toArray(tracks.tracks);
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (int i=0; i<5;i++){
                    View elem = inflater.inflate(R.layout.item_album_track, null);
                    final TextView track = (TextView) elem.findViewById(R.id.track);
                    track.setText(tracksInfo.get(i).getName());
                    TextView position = (TextView) elem.findViewById(R.id.position);
                    position.setText(Integer.toString(i+1));
                    final int finalI = i;
                    elem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
//                                intent.putExtra("track", tracksInfo[finalI]);
                            app.newQueue(tracksInfo);
                            app.setPosition(finalI);
                            intent.putExtra("action","play");
                            startActivity(intent);
                        }
                    });
                    popular.addView(elem);

                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void findAlbums() {
        app.spotify.getArtistAlbums(artistInfo.getId(), new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                final List<MyAlbum> albumsInfo = MyAlbum.toArray(albumPager.items);
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (int i=0; i<albumsInfo.size();i++){
                    View elem = inflater.inflate(R.layout.item_album, null);
                    final TextView name = (TextView) elem.findViewById(R.id.name);
                    name.setText(albumsInfo.get(i).getName());
                    ImageView cover = (ImageView) elem.findViewById(R.id.cover);
                    Glide.with(ArtistActivity.this).load(albumsInfo.get(i).getCover()).into(cover);
                    final int finalI = i;
                    elem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                            intent.putExtra("album", albumsInfo.get(finalI));
                            startActivity(intent);
                        }
                    });
                    albums.addView(elem);

                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    public void player(){
        LinearLayout playerBar = (LinearLayout) findViewById(R.id.playerBar);
        if(app.state== StringsValues.PLAY) playerBar.setVisibility(View.VISIBLE);
        else playerBar.setVisibility(View.INVISIBLE);
        pause=(ImageButton) findViewById(R.id.pause);
        if(mediaPlayer.isPlaying()) {
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



}
