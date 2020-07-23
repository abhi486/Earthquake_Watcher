package com.example.earthquakewatcher.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.earthquakewatcher.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private View view;

    public CustomInfoWindow(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view  = layoutInflater.inflate(R.layout.custom_window_info,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Button moreinfo;
        TextView title,mag;
        moreinfo = view.findViewById(R.id.moreinfobutton);
        title = view.findViewById(R.id.wintitle);
        mag = view.findViewById(R.id.magnitudetext);

        title.setText(marker.getTitle());
        mag.setText(marker.getSnippet());
        return view;
    }
}
