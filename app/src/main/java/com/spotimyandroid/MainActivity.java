package com.spotimyandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.spotimyandroid.http.Api;
import com.spotimyandroid.resources.Album;
import com.spotimyandroid.resources.ApplicationSupport;
import com.spotimyandroid.resources.Artist;
import com.spotimyandroid.resources.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;


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

        ((ApplicationSupport) this.getApplication()).setMP(new MediaPlayer());



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
                scrollView.setVisibility(View.INVISIBLE);
                doMySearch(s);
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
                                            addElemToAlbumsView(Album.toArray(array));
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

    private void addElemToAlbumsView(Album[] albums) {
        albumsView.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i =0 ;i<albums.length;i++){
            View elem = inflater.inflate(R.layout.item_album, null);
            TextView name = (TextView) elem.findViewById(R.id.name);
            name.setText(albums[i].getName());
            ImageView cover = (ImageView) elem.findViewById(R.id.cover);
//            System.out.println(artists[i].getImage());
            if (albums[i].hasCover())
                new DownloadImageTask(cover)
                        .execute(albums[i].getCover());
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
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
            CircleImageView image = (CircleImageView) elem.findViewById(R.id.image);
//            System.out.println(artists[i].getImage());
            if (artists[i].hasImage())
                new DownloadImageTask(image)
                        .execute(artists[i].getImage());

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

    public void addElemToTracksView(final Track[] tracks){
        tracksView.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i =0 ;i<tracks.length;i++){
            View elem = inflater.inflate(R.layout.item_track, null);
            TextView name = (TextView) elem.findViewById(R.id.name);
            name.setText(tracks[i].getName());
            TextView artist = (TextView) elem.findViewById(R.id.artist);
            artist.setText(tracks[i].getArtist());
            TextView album = (TextView) elem.findViewById(R.id.album);
            album.setText(tracks[i].getAlbum());
            final int finalI = i;
            elem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView artist = (TextView) view.findViewById(R.id.artist);
                    String message = name.getText()+" - "+artist.getText();
                    intent.putExtra("song", message);
                    intent.putExtra("track", tracks[finalI]);
                    startActivity(intent);
                }
            });
            tracksView.addView(elem);
        }

    }



    protected static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
