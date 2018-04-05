package com.spotimyandroid.http;

import android.content.Context;

import com.spotimyandroid.resources.MyTorrent;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ApiHelper {
    private final Api api;

    public ApiHelper(Context context) {
        api=new Api(context);
    }

    public void getLyric(String artist, String track, final onLyricCallback callback) {
        api.lyric(artist, track, new Api.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    callback.onSuccess(parseLyric(result.getString("lyric")));
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void scraper(String artist, String album, final onTorrentCallback callback){
        try {
            Document doc = Jsoup.connect(api.torrentX1337URL(artist,album)).get();
            Elements table = doc.select(".table-list.table.table-responsive.table-striped");
            Elements lines = table.get(0).children().get(1).children();
            ArrayList<MyTorrent> myTorrents = new ArrayList();

            for (Element line : lines) {
                String name = line.select("td").get(0).text();
                String link = line.select("td").get(0).children().eq(1).attr("href");
                String seeds = line.select("td").get(1).text();
                String leeches = line.select("td").get(2).text();
                String date = line.select("td").get(3).text();
                String size  = line.select("td").get(4).text();

                Document doc2 = Jsoup.connect(api.magnetX1337URL(link)).get();
                Elements row = doc2.select(".download-links-dontblock.btn-wrap-list");
                String magnet = row.tagName("a").get(0).child(0).child(0).attr("href");
                MyTorrent myTorrent = new MyTorrent(name, link, seeds, leeches, date, size, magnet);
                myTorrents.add(myTorrent);
            }
            callback.onSuccess(myTorrents);
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }

    }



    public interface onLyricCallback{
        void onSuccess(String lyric);
        void onError(String err);
    }

    public interface onTorrentCallback{
        void onSuccess(List<MyTorrent> myTorrents);
        void onError(String err);
    }

    private String parseLyric(String s){
        s=s.replaceAll("&#xE8;","è");
        s=s.replaceAll("&#xE9;","é");
        s=s.replaceAll("&#xF2;","ò");
        s=s.replaceAll("&#xE0;","à");
        s=s.replaceAll("&#xEC;","ì");
        s=s.replaceAll("&#xF9;","ù");
        s=s.replaceAll("&#x2019;","'");
        return s;
    }
}
