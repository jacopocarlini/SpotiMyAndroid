package com.spotimyandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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


        try {
            FileInputStream in = openFileInput("settings.txt");

            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            System.out.println(sb.length());
            if (sb.toString().equals("fast")){
                System.out.println("true");
                fast.setChecked(true);
            }
            else System.out.println("false");

        } catch (IOException e) {
            e.printStackTrace();

        }


        fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileContents="low";
                if (fast.isChecked()) fileContents="fast";

                try {
                    FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

                    outputStream.write(fileContents.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
