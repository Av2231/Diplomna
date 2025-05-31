package com.example.pmu.interfaces;

import com.example.pmu.models.PinMarker;

import java.util.ArrayList;

public interface DataParserListener {
    void onSuccess(ArrayList<PinMarker> data);
    void onFailure(String message);
}
