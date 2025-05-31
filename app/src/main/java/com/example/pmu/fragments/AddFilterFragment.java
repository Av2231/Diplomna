package com.example.pmu.fragments;

import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.interfaces.DataParserListener;
import com.example.pmu.interfaces.SendDataLocation;
import com.example.pmu.models.PinMarker;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(com.example.pmu.R.layout.fragment_add_filter)
public class AddFilterFragment extends BaseFragment {

    @ViewById
    EditText typeEditText, regionEditText, cityEditText;

    private String type, region, city;

    private SendDataLocation sendDataLocation;
    @ViewById
    ProgressBar progressBar;
    @ViewById
    TextView errorWhileSearching;

    @Click
    void contentLayout() {
        clearFocus();
    }

    @Click
    void addFilterButton() {
        type = String.valueOf(typeEditText.getText());
        city = String.valueOf(cityEditText.getText());
        region = String.valueOf(regionEditText.getText());
        progressBar.setVisibility(View.VISIBLE);
        findLocations(type,city,region);
    }
    private void findLocations(String type,String city,String region){
        RequestBuilder.getLocationsFromAPI(type, city, region, new DataParserListener() {
            @Override
            public void onSuccess(ArrayList<PinMarker> data) {
                if(data.isEmpty()){
                    progressBar.setVisibility(View.INVISIBLE);
                    errorWhileSearching.setVisibility(View.VISIBLE);
                    errorWhileSearching.setText("There is no data. Please fix your request!");
                }else{
                    sendDataLocation.sendData(data);
                    ((MainActivity) getActivity()).popToHomePageFragment();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onFailure(String message) {
                showErrorAlertDialog(message);
            }
        });
    }
    public void setSendDataLocation(SendDataLocation sendDataLocation) {
        this.sendDataLocation = sendDataLocation;
    }
}