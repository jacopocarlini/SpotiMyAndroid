package com.spotimyandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.spotimyandroid.utils.ApplicationSupport;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jacopo on 30/03/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    private ApplicationSupport as;
    private LinearLayout queue;
    private LinearLayout playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        as = (ApplicationSupport) this.getApplication();

        initview();
    }

    private void initview() {
        this.queue= (LinearLayout) findViewById(R.id.queue);
        this.playlists= (LinearLayout) findViewById(R.id.playlists);

        addElemToTracksView(as.getQueue().toArray(new Track[as.getLenghtQueue()]));

    }


    public void addElemToTracksView(final Track[] tracks){
//        scrollView.setVisibility(View.VISIBLE);
        queue.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        for (int i =0 ;i<tracks.length;i++){
//            View elem = inflater.inflate(R.layout.item_track, null);
//            TextView name = (TextView) elem.findViewById(R.id.name);
//            name.setText(tracks[i].getName());
//            TextView artist = (TextView) elem.findViewById(R.id.artist);
//            artist.setText(tracks[i].getArtist());
//            TextView album = (TextView) elem.findViewById(R.id.album);
//            album.setText(tracks[i].getAlbum());
//            if(as.getCurrentTrack().equals(tracks[i])){
//                LinearLayout background = (LinearLayout) elem.findViewById(R.id.background);
//                background.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark));
//            }
//            final int finalI = i;
//            elem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
//                    if(!as.getQueue().toArray().equals(tracks)) {
//                        as.newQueue(tracks);
//                    }
//                    as.setPosition(finalI);
//                    intent.putExtra("info","play");
//                    startActivity(intent);
//                }
//            });
//            queue.addView(elem);
//        }

    }

}
