package com.example.pmu.fragments;

import static android.os.Build.VERSION_CODES.R;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.utils.LanguageManager;
import com.google.android.gms.maps.GoogleMap;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(com.example.pmu.R.layout.fragment_settings)
public class SettingsFragment extends BaseFragment {
    @ViewById
    TextView changeLanguageButton;
    @ViewById
    ProgressBar progressBar;
    @ViewById
    ImageView terrainMapButton;
    @ViewById
    ImageView normalMapButton;
    @ViewById
    ImageView satelliteMapButton;
    @Click
    void changeLanguageButton(){
        progressBar.setVisibility(View.VISIBLE);
        LanguageManager.changeLanguage(getActivity());
        ((MainActivity) getActivity()).resetApplication();
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Click
    void normalMapButton(){
        progressBar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).changeMapView(GoogleMap.MAP_TYPE_NORMAL);
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Click
    void terrainMapButton(){
        progressBar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).changeMapView(GoogleMap.MAP_TYPE_TERRAIN);
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Click
    void satelliteMapButton(){
        progressBar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).changeMapView(GoogleMap.MAP_TYPE_SATELLITE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
