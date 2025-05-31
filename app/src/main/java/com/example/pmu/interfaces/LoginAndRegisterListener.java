package com.example.pmu.interfaces;

public interface LoginAndRegisterListener {
    void onSuccess();

    void onFailure(String message);

    void onErrorResponse(String message);
}
