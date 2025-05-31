package com.example.pmu.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageManager {

    public static final String BG = "bg";
    public static final String EN = "en";

    public static Locale backedUpLocale = null;

    public static void setApplicationLanguage(Context context) {
        String lang = AppService.getInstance().getLanguage();
        if (lang.isEmpty()) {
            lang = BG;
            AppService.getInstance().setLanguage(lang);
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        backedUpLocale = locale;
    }

    public static void changeLanguage(Context context) {
        String lang = AppService.getInstance().getLanguage();
        if (lang.equals(BG)) {
            lang = EN;
        } else {
            lang = BG;
        }

        AppService.getInstance().setLanguage(lang);
        setApplicationLanguage(context);
    }

    public static String getApplicationLanguage() {
        return AppService.getInstance().getLanguage();
    }

}