package com.example.pmu.fragments;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.interfaces.AlreadyRatedLocationListener;
import com.example.pmu.interfaces.LocationRatingListener;
import com.example.pmu.interfaces.NewLocationListener;
import com.example.pmu.interfaces.RateLocationListener;
import com.example.pmu.models.PinMarker;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(com.example.pmu.R.layout.fragment_details)
public class DetailsFragment extends BaseFragment {

    private String concatenated_location_id;
    private String generated_location_id;
    private boolean alreadyRated;
    private String rating;
    private boolean existing;
    @ViewById
    TextView locationRating;
    @ViewById
    TextView locationAddress;
    @ViewById
    TextView locationName;
    @ViewById
    Button rateButton;
    @ViewById
    Button directionsButton;
    private PinMarker marker;
    @ViewById
    RatingBar ratingBar;

    @Click
    void contentLayout() {
        clearFocus();
    }

    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        addLocation(marker.getId());
        new Handler().postDelayed(() -> checkLocation(), 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationName.setText(String.format("%s: %s", getString(R.string.name), marker.getTitle()));
        locationAddress.setText(String.format("%s: %s",getString(R.string.address), marker.getLocation()));
        checkLocation();
    }

    @Click
    public void rateButton() {
        String rate = String.valueOf(ratingBar.getRating());
        checkLocation();
        addRate(generated_location_id,User.getInstance().getId(), rate);
        getRating(generated_location_id);
    }

    @Click
    public void directionsButton() {
        Uri uri = Uri.parse("google.navigation:q=" + marker.getY() + "," + marker.getX() + "&mode=w");
        startActivity(new Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps"));
    }

    public void setMarker(PinMarker marker) {
        this.marker = marker;
    }

    @Click
    public void commentsButton() {
        CommentsFragment_ commentsFragment = new CommentsFragment_();
        commentsFragment.setLocationId(generated_location_id);
        addFragment(commentsFragment);
    }

    private void checkLocation(){
        RequestBuilder.checkIfLocationIsExisting(marker.getId(), new NewLocationListener() {
            @Override
            public void onSuccess(String locationId) {
                generated_location_id = locationId;
                if(generated_location_id.equals("0")){
                    ratingBar.setRating(0);
                    rateButton.setVisibility(View.VISIBLE);
                }else{
                    getRating(generated_location_id);
                    checkIfUserRatedLocation(generated_location_id,User.getInstance().getId());
                }
            }
            @Override
            public void onFailure(String message) {

            }
        });
    }
    private void addLocation(String locationId){
        RequestBuilder.addNewLocation(locationId, new NewLocationListener() {
            @Override
            public void onSuccess(String locationId) {
                generated_location_id = locationId;
            }
            @Override
            public void onFailure(String message) {
            }
        });
    }
    private void addRate(String generatedId,String userId,String rate){
        RequestBuilder.rateLocation(generatedId, userId, rate, new RateLocationListener() {
            @Override
            public void onSuccess() {
                ratingBar.setIsIndicator(true);
                rateButton.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(String message) {

            }
        });
    }
    private void getRating(String generatedId){
        RequestBuilder.getLocationRating(generatedId, new LocationRatingListener() {
            @Override
            public void onSuccess(String average_rating) {
                double rating = Double.parseDouble(average_rating);
                double result = Math.floor(rating*2)/2.0;
                ratingBar.setRating(Float.parseFloat(String.valueOf(result)));
            }
            @Override
            public void onFailure(String message) {
                locationRating.setText(message);
            }
        });
    }

    private void checkIfUserRatedLocation(String generatedId,String userId){
        RequestBuilder.checkIfUserRatedLocation(generatedId, userId, new AlreadyRatedLocationListener() {
            @Override
            public void onSuccess(String rated) {
                if(rated.equals("true")){
                    alreadyRated = true;
                    ratingBar.setIsIndicator(true);
                    rateButton.setVisibility(View.INVISIBLE);
                }else{
                    alreadyRated = false;
                    rateButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(String message) {
            }
        });
    }
}


