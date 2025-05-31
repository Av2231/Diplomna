package com.example.pmu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pmu.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View view;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.location_pop_up, null);
    }

    private void render(Marker marker, View view) {
        TextView textView = view.findViewById(R.id.popUpAddress);
        textView.setText(marker.getTitle());
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        render(marker, view);
        return view;
    }
}
