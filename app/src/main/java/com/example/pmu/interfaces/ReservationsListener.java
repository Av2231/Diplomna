package com.example.pmu.interfaces;

import com.example.pmu.models.LocationModel;

import java.util.ArrayList;

public interface ReservationsListener {
    void onSuccess(ArrayList<LocationModel> reservations);

    void onFailure(String message);

    void onErrorResponse(String message);
}
