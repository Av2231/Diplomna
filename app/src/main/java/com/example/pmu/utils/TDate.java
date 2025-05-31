package com.example.pmu.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TDate {

    public static String convertToDayStringForCurrentLocale(Date date) {
        return convertToDayStringForLocale(date, new Locale(LanguageManager.getApplicationLanguage()));
    }
    public static String getTimeZoneStringForBuildType() {
            return "Europe/Sofia";
    }

    public static String convertToDayStringForLocale(Date date, Locale locale) {
        if (date == null || date.getTime() == 0) {
            return "";
        }

        DateFormat df = new SimpleDateFormat("dd", locale);
        df.setTimeZone(TimeZone.getTimeZone(TDate.getTimeZoneStringForBuildType()));
        return df.format(date);
    }

    public static String convertToStringForCurrentLocale(Date date) {
        return convertToStringForLocale(date, new Locale(LanguageManager.getApplicationLanguage()));
    }

    public static String convertToStringForLocale(Date date, Locale locale) {
        if (date == null || date.getTime() == 0) {
            return "";
        }

        DateFormat df;
        if (locale.getLanguage().equals("bg")) {
            df = new SimpleDateFormat("dd/MM/yyyy", locale);
        } else if (locale.getLanguage().equals("en")) {
            df = new SimpleDateFormat("MM/dd/yyyy", locale);
        } else {
            return "";
        }

        df.setTimeZone(TimeZone.getTimeZone(TDate.getTimeZoneStringForBuildType()));
        return df.format(date);
    }
}