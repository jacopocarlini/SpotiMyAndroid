package com.spotimyandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SearchView;
import android.widget.TextView;

import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Artist;
import com.spotimyandroid.resources.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private SearchView searchView;

    private Api server;
    private ScrollView scrollView;
    private LinearLayout tracksView;
    private LinearLayout albumsView;
    private LinearLayout artistsView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        server = new Api(this);
        initview();


    }

    private void initview() {
        this.scrollView=(ScrollView) findViewById(R.id.results);
        this.searchView=(SearchView) findViewById(R.id.search);
        this.tracksView = (LinearLayout) findViewById(R.id.tracksView);
        this.artistsView = (LinearLayout) findViewById(R.id.artistsView);
        this.albumsView = (LinearLayout) findViewById(R.id.albumsView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                scrollView.setVisibility(View.INVISIBLE);
                doMySearch(s);
                return false;
            }
        });

    }


    private void doMySearch(final String query) {
        System.out.println("do search");
        server.findTrack(query.replace(" ","%20"), new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray array = result.getJSONObject("tracks").getJSONArray("items");

                    addElemToTracksView(Track.toArray(array));

                    server.findArtist(query, new Api.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                JSONArray array = result.getJSONObject("artists").getJSONArray("items");
                                addElemToArtistsView(Artist.toArray(array));

                                server.findAlbum(query, new Api.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        try {
                                            JSONArray array = result.getJSONObject("albums").getJSONArray("items");
                                            addElemToAlbumsView(Artist.toArray(array));
                                            scrollView.setVisibility(View.VISIBLE);
                                        } catch (JSONException e) {
                                            System.out.println("errore");
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            } catch (JSONException e) {
                                System.out.println("errore");
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (JSONException e) {
                    System.out.println("errore");
                    e.printStackTrace();
                }

            }
        });





    }

    private void addElemToAlbumsView(Artist[] artists) {
        albumsView.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i =0 ;i<artists.length;i++){
            View elem = inflater.inflate(R.layout.item_album, null);
            TextView name = (TextView) elem.findViewById(R.id.name);
            name.setText(artists[i].getName());
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    String message = name.getText().toString();
                    intent.putExtra("album", message);
                    startActivity(intent);
                }
            });
            albumsView.addView(elem);
        }
    }

    private void addElemToArtistsView(Artist[] artists) {
        artistsView.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i =0 ;i<artists.length;i++){
            View elem = inflater.inflate(R.layout.item_artist, null);
            TextView name = (TextView) elem.findViewById(R.id.artist);
            name.setText(artists[i].getName());
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                    TextView name = (TextView) view.findViewById(R.id.artist);
                    String message = name.getText().toString();
                    intent.putExtra("artist", message);
                    startActivity(intent);
                }
            });
            artistsView.addView(elem);
        }
    }

    public void addElemToTracksView(Track[] tracks){
        tracksView.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i =0 ;i<tracks.length;i++){
            View elem = inflater.inflate(R.layout.item_track, null);
            TextView name = (TextView) elem.findViewById(R.id.name);
            name.setText(tracks[i].getName());
            TextView artist = (TextView) elem.findViewById(R.id.artist);
            artist.setText(tracks[i].getArtist());
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView artist = (TextView) view.findViewById(R.id.artist);
                    String message = name.getText()+" - "+artist.getText();
                    intent.putExtra("song", message);
                    startActivity(intent);
                }
            });
            tracksView.addView(elem);
        }

    }


//    private class MyClickListener implements AdapterView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
//            TextView name = (TextView) view.findViewById(R.id.name);
//            TextView artist = (TextView) view.findViewById(R.id.artist);
//            String message = name.getText()+" - "+artist.getText();
//            intent.putExtra("song", message);
//            startActivity(intent);
//        }
//    }
}
