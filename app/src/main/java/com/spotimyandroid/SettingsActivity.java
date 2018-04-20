package com.spotimyandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

import java.io.File;

/**
 * Created by Jacopo on 18/03/2018.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Switch fast = (Switch) findViewById(R.id.fast);

        final String filename="settings.txt";
        File file = new File(this.getFilesDir(), filename);

        //TODO: usare sharedPreference


    }
}
