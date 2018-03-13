package com.spotimyandroid.resources;

import android.app.Application;
import android.media.MediaPlayer;

/**
 * Created by Jacopo on 13/03/2018.
 */


public class ApplicationSupport extends Application {
    private MediaPlayer mp;


    public MediaPlayer getMP() {
        return mp;
    }

    public void setMP(MediaPlayer mp) {
        this.mp = mp;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

}