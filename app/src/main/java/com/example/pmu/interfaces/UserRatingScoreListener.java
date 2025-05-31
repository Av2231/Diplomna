package com.example.pmu.interfaces;

public interface UserRatingScoreListener {
    void onSuccess(String average_rate,String rate_score);
    void onFailure(String message);
}
