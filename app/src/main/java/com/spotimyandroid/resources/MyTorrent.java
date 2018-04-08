package com.spotimyandroid.resources;


import android.os.Environment;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

public class MyTorrent  {
    private static final String TORRENT = "torrent";
    private TorrentStream torrentStream;
    private String name;
    private String link;
    private String magnet;
    private String seeds;
    private String leeches;
    private String size;
    private String date;
    private String title;


    public MyTorrent(String name, String link, String seeds, String leeches, String date, String size, String magnet) {
        this.name = name;
        this.link = link;
        this.magnet = magnet;
        this.seeds = seeds;
        this.leeches = leeches;
        this.size = size;
        this.date = date;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public String getSeeds() {
        return seeds;
    }

    public void setSeeds(String seeds) {
        this.seeds = seeds;
    }

    public String getLeeches() {
        return leeches;
    }

    public void setLeeches(String leeches) {
        this.leeches = leeches;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyTorrent myTorrent = (MyTorrent) o;
       return this.magnet.equals(myTorrent.magnet);
    }


    @Override
    public String toString() {
        return "MyTorrent{" +
                "name='" + name + '\'' +
                '}';
    }
}
