package com.spotimyandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spotimyandroid.R;
import com.spotimyandroid.resources.Track;

/**
 * Created by Jacopo on 11/03/2018.
 */

public class TracksAdapter  extends BaseAdapter {
    private final Context context;
    private Track[] tracks;


    public TracksAdapter(Context context, Track[] tracks) {
        this.context = context;
        this.tracks = tracks;

    }
    @Override
    public int getCount() {
        return tracks.length;
    }

    @Override
    public Object getItem(int i) {
        return tracks[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        if (row == null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_track, viewGroup, false);

        }


        TextView name = (TextView) row.findViewById(R.id.name);
        name.setText(((Track)getItem(i)).getName());
        TextView artist = (TextView) row.findViewById(R.id.artist);
        artist.setText(((Track)getItem(i)).getArtist());

        return row;


    }
}
