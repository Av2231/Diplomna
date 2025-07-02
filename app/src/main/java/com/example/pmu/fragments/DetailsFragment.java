package com.example.pmu.fragments;

import static java.lang.Integer.parseInt;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmu.R;
import com.example.pmu.interfaces.AlreadyRatedLocationListener;
import com.example.pmu.interfaces.LocationRatingListener;
import com.example.pmu.interfaces.NewLocationListener;
import com.example.pmu.interfaces.RateLocationListener;
import com.example.pmu.models.PinMarker;
import com.example.pmu.models.User;
import com.example.pmu.utils.ServerCommunication;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    TextView fromHoursTextView, toHoursTextView;
    @ViewById
    TextView locationName;
    @ViewById
    Button rateButton;
    @ViewById
    Button directionsButton;
    private PinMarker marker;
    @ViewById
    RatingBar ratingBar;
    @ViewById
    Button reserveButton;

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
    @Click
    void fromHoursTextView() {
        clearFocus();
        showTimePickerDialog(fromHoursTextView, marker.getFromTime(), marker.getToTime());
    }

    @Click
    void toHoursTextView() {
        clearFocus();
        showTimePickerDialog(toHoursTextView, marker.getFromTime(), marker.getToTime());
    }

    private void showTimePickerDialog(TextView targetEditText, String fromTimeStr, String toTimeStr) {
        Calendar calendar = Calendar.getInstance();

        int minHour = 0, minMinute = 0, maxHour = 23, maxMinute = 59;

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date fromTime = format.parse(fromTimeStr);
            Date toTime = format.parse(toTimeStr);

            Calendar fromCal = Calendar.getInstance();
            fromCal.setTime(fromTime);
            minHour = fromCal.get(Calendar.HOUR_OF_DAY);
            minMinute = fromCal.get(Calendar.MINUTE);

            Calendar toCal = Calendar.getInstance();
            toCal.setTime(toTime);
            maxHour = toCal.get(Calendar.HOUR_OF_DAY);
            maxMinute = toCal.get(Calendar.MINUTE);

            // Set the initial time to the start time
            calendar.set(Calendar.HOUR_OF_DAY, minHour);
            calendar.set(Calendar.MINUTE, minMinute);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error parsing time range", Toast.LENGTH_SHORT).show();
        }

        int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
        int initialMinute = calendar.get(Calendar.MINUTE);

        int finalMinHour = minHour;
        int finalMaxHour = maxHour;
        int finalMinMinute = minMinute;
        int finalMaxMinute = maxMinute;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> {
                    boolean isValid = (hourOfDay > finalMinHour || (hourOfDay == finalMinHour && minute >= finalMinMinute)) &&
                            (hourOfDay < finalMaxHour || (hourOfDay == finalMaxHour && minute <= finalMaxMinute));

                    if (isValid) {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        targetEditText.setText(formattedTime);
                    } else {
                        String start = String.format("%02d:%02d", finalMinHour, finalMinMinute);
                        String end = String.format("%02d:%02d", finalMaxHour, finalMaxMinute);
                        Toast.makeText(getActivity(), "Select time between " + start + " and " + end, Toast.LENGTH_SHORT).show();
                    }
                }, initialHour, initialMinute, true);

        timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE, getString(R.string.done), timePickerDialog);
        timePickerDialog.show();
    }
    @Click
    public void reserveButton(){
        String fromTime = fromHoursTextView.getText().toString();
        String toTime = toHoursTextView.getText().toString();

        if (fromTime.isEmpty() || toTime.isEmpty()) {
            Toast.makeText(getActivity(), "Please select both start and end time", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullFromTime = marker.getFromTime().split(" ")[0] + " " + fromTime;
        String fullToTime = marker.getToTime().split(" ")[0] + " " + toTime;

        ServerCommunication.sendDetailsForReservation(marker.getId(), fullFromTime, fullToTime, marker.getTitle(), marker.getType(),
                response -> Toast.makeText(getActivity(), "Reservation saved successfully", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getActivity(), "Failed to save reservation: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        );

    }

    private void checkLocation(){
        ServerCommunication.checkIfLocationIsExisting(marker.getId(), new NewLocationListener() {
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
        ServerCommunication.addNewLocation(locationId, new NewLocationListener() {
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
        ServerCommunication.rateLocation(generatedId, userId, rate, new RateLocationListener() {
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
        ServerCommunication.getLocationRating(generatedId, new LocationRatingListener() {
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
        ServerCommunication.checkIfUserRatedLocation(generatedId, userId, new AlreadyRatedLocationListener() {
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


