package com.spotimyandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spotimyandroid.resources.MyTrack;
import com.spotimyandroid.utils.ApplicationSupport;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jacopo on 30/03/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    private ApplicationSupport app;
    private LinearLayout queue;
    private LinearLayout playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        app = (ApplicationSupport) this.getApplication();

        initview();
    }

    private void initview() {
        this.queue= (LinearLayout) findViewById(R.id.queue);
        this.playlists= (LinearLayout) findViewById(R.id.playlists);

        addElemToTracksView(app.getQueue());
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
