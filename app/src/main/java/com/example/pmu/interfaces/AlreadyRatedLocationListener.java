package com.example.pmu.interfaces;

public interface AlreadyRatedLocationListener {
    void onSuccess(String rated);
    void onFailure(String message);
}
