package com.example.pmu.interfaces;

public interface UserCommentScoreListener {
    void onSuccess(String score);
    void onFailure(String message);
}
