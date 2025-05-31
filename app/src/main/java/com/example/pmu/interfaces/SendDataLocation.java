package com.example.pmu.interfaces;

import com.example.pmu.models.PinMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SendDataLocation {
    void sendData(ArrayList<PinMarker> locations);
}
