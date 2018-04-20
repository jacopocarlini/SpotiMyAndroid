package com.spotimyandroid;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.spotimyandroid.resources.MyTrack;
import com.spotimyandroid.utils.ApplicationSupport;
import com.spotimyandroid.utils.StringsValues;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jacopo on 30/03/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    private ApplicationSupport app;
    private LinearLayout queue;
    private LinearLayout playlists;
    private MediaPlayer mediaPlayer;
    private ArrayList<MyTrack> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        System.out.println("oncreate");
        app = (ApplicationSupport) this.getApplication();
        mediaPlayer=app.getMP();
        if(app.oldqueue==null) tracks = app.getQueue();
        else tracks = app.oldqueue;
        initview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setIcons();
        player();
    }

    private void initview() {
        this.queue= (LinearLayout) findViewById(R.id.queue);
        this.playlists= (LinearLayout) findViewById(R.id.playlists);

        addElemToTracksView(tracks);
        setBottomBar();

    }


    private void addElemToTracksView(final List<MyTrack> tracks) {
        queue.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i =0 ;i<tracks.size();i++){
            View elem = inflater.inflate(R.layout.item_track, null);
            TextView name = (TextView) elem.findViewById(R.id.name);
            name.setText(tracks.get(i).getName());
            TextView artist = (TextView) elem.findViewById(R.id.artist);
            artist.setText(tracks.get(i).getArtist());
            TextView album = (TextView) elem.findViewById(R.id.album);
            album.setText(tracks.get(i).getAlbum());
            final int finalI = i;
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    if(!tracks.get(finalI).equals(app.getCurrentTrack())) {
                        app.newQueue(tracks);
                    }
                    app.setPosition(finalI);
                    intent.putExtra("action","play");
                    startActivity(intent);
                }
            });
            queue.addView(elem);
        }
    }

    private void setIcons(){
        int count = queue.getChildCount();
        View elem = null;
        for(int i=0; i<count; i++) {
            elem = queue.getChildAt(i);
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
                    System.out.println(tracks.size());
                    if (!app.getQueue().contains(tracks.get(finalI))) {
                        app.addQueue(tracks.get(finalI));
                        queue.setImageResource(R.drawable.ic_library_books_white_24dp);
                        queue.setColorFilter(getResources().getColor(R.color.blue));
                    } else {
                        app.oldqueue= (ArrayList<MyTrack>) tracks.clone();
                        app.removeQueue(tracks.get(finalI));
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
        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
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
        ImageView player = (ImageView) findViewById(R.id.player);
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
        LinearLayout home = (LinearLayout) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout discover = (LinearLayout) findViewById(R.id.discover);
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });
//        LinearLayout profile = (LinearLayout) findViewById(R.id.profile);
//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
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
