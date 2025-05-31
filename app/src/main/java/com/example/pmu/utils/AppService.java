package com.example.pmu.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppService {

    private static AppService instance = null;

    private Context context;
    private String language;
    private boolean openedFirst;
    private String savedMap;

    public static AppService getInstance() {
        if (instance == null) {
            instance = new AppService();
        }

        return instance;
    }

    public String getLanguage() {
        return language;
    }

    public String getSavedMap(){return savedMap;}

    public void setSavedMap(String savedMap){
        this.savedMap = savedMap;
        write();
    }

    public void setContext(Context context) {
        this.context = context;
        read();
    }

    public void setLanguage(String language) {
        this.language = language;
        write();
    }

    public boolean isOpenedFirst() {
        return openedFirst;
    }

    private void read() {
        SharedPreferences prefs = this.context.getSharedPreferences("PMU", Context.MODE_PRIVATE);
        language = prefs.getString("language", "");
        openedFirst = prefs.getBoolean("opened", false);
        savedMap = prefs.getString("map", "");
    }

    private void write() {
        SharedPreferences prefs = this.context.getSharedPreferences("PMU", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", language);
        editor.putBoolean("opened", true);
        editor.putString("map", savedMap);
        editor.apply();

    }

}