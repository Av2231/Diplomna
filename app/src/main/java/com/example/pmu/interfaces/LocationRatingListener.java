package com.example.pmu.interfaces;

public interface LocationRatingListener {
    void onSuccess(String average_rating);
    void onFailure(String message);
}
