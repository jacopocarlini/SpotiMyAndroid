package com.spotimyandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Album;
import com.spotimyandroid.resources.Artist;
import com.spotimyandroid.resources.Track;
import com.spotimyandroid.utils.ApplicationSupport;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jacopo on 12/03/2018.
 */

public class ArtistActivity extends AppCompatActivity {

    private Artist artistInfo;
    private Api server;
    private TextView name;
    private ImageView image;
    private LinearLayout popular;
    private GridLayout albums;
    private Album[] albumsInfo;
    private Track[] tracksInfo;
    private ApplicationSupport as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        as = (ApplicationSupport) this.getApplication();

        Intent intent = getIntent();
        artistInfo = intent.getParcelableExtra("artist");
        server = new Api(this);

        initview();
    }

    private void initview() {
        this.name = (TextView) findViewById(R.id.name);
        this.image =(ImageView) findViewById(R.id.image);
        this.popular = (LinearLayout) findViewById(R.id.popular);
        this.albums = (GridLayout) findViewById(R.id.albums);

        name.setText(artistInfo.getName());

        Glide.with(this).load(artistInfo.getImage()).fitCenter().into(image);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                findAlbums();
                findTracks();
                return null;
            }
        };
        task.execute();
    }

    private void findTracks() {
        server.findPopularOfArtist(artistInfo.getId(), new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    tracksInfo= Track.toArray(result.getJSONArray("tracks"));
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    as.resetQueue();
                    for (int i=0; i<5;i++){
                        tracksInfo[i].setAlbum(tracksInfo[i].getCover());
                        View elem = inflater.inflate(R.layout.item_album_track, null);
                        final TextView track = (TextView) elem.findViewById(R.id.track);
                        track.setText(tracksInfo[i].getName());
                        TextView position = (TextView) elem.findViewById(R.id.position);
                        position.setText(Integer.toString(i+1));
//                        as.addTrackToQueue(tracksInfo[i]);
                        final int finalI = i;
                        elem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
//                                intent.putExtra("track", tracksInfo[finalI]);
                                as.newQueue(tracksInfo);
                                startActivity(intent);
                            }
                        });
                        popular.addView(elem);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findAlbums() {
        server.findAlbumsOfArtist(artistInfo.getId(), new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    albumsInfo = Album.toArray(result.getJSONArray("items"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (int i=0; i<albumsInfo.length;i++){
                    View elem = inflater.inflate(R.layout.item_album, null);
                    final TextView name = (TextView) elem.findViewById(R.id.name);
                    name.setText(albumsInfo[i].getName());
                    ImageView cover = (ImageView) elem.findViewById(R.id.cover);
                    Glide.with(ArtistActivity.this).load(albumsInfo[i].getCover()).into(cover);
                    final int finalI = i;
                    elem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                            intent.putExtra("album", albumsInfo[finalI]);
//                            System.out.println(albumsInfo[finalI]);
                            startActivity(intent);
                        }
                    });
                    albums.addView(elem);

                }



            }
        });
    }

}
