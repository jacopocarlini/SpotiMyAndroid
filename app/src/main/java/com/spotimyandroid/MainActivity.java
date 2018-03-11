package com.spotimyandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.spotimyandroid.http.Api;
import com.spotimyandroid.adapters.TracksAdapter;
import com.spotimyandroid.resources.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private SearchView searchView;

    private Api server;
    private ListView tracksView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_search:
                    mTextMessage.setText(R.string.title_search);
                    return true;
                case R.id.navigation_library:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        server = new Api(this);
        initview();


    }

    private void initview() {
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        this.searchView=(SearchView) findViewById(R.id.search);
        this.tracksView = (ListView) findViewById(R.id.tracksView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                doMySearch(s);
                return false;
            }
        });

    }


    private void doMySearch(String query) {
        server.findTrack(query, new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray array = result.getJSONArray("items");
//                    String []r =new String[array.length()];
//                    for (int i =0; i<array.length(); i++) {
//                        r[i]=array.getJSONObject(i).getString("name")
//                                +" - "
//                                +array.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
//
//                    }
                    final TracksAdapter adapter = new TracksAdapter(getApplicationContext(), Track.toArray(array));
                    tracksView.setAdapter(adapter);
                    tracksView.setOnItemClickListener(new MyClickListener());

                } catch (JSONException e) {
                    System.out.println("errore");
                    e.printStackTrace();
                }

            }
        });

        System.out.println("do search");

    }



    private class MyClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
            LinearLayout track = (LinearLayout) view.findViewById(R.id.track);
//            String message =  ((TextView)track.getChildAt(0)).getText()
//                    +" - "
//                    +((TextView)tracksView.getChildAt(1)).getText();
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView artist = (TextView) view.findViewById(R.id.artist);
            String message = name.getText()+" - "+artist.getText();
            System.out.println(message);
            intent.putExtra("song", message);
            startActivity(intent);
        }
    }
}
