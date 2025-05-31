package com.example.pmu.interfaces;

public interface NewLocationListener {
    void onSuccess(String locationId);
    void onFailure(String message);
}
