package com.mowtiie.faithful.util;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public static String getStringDateTime(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
        return dateFormat.format(new Date(timeStamp));
    }

    public static String getPrettyStringDateTime(long timestamp){
        if (timestamp <= 0) return "";

        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        boolean isFuture = diff < 0;

        long duration = Math.abs(diff);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);

        String timeSpan;

        if (seconds < 60) {
            return isFuture ? "moments from now" : "just now";
        } else if (minutes < 60) {
            timeSpan = minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (hours < 24) {
            timeSpan = hours + (hours == 1 ? " hour" : " hours");
        } else if (days < 7) {
            timeSpan = days + (days == 1 ? " day" : " days");
        } else if (days < 30) {
            long weeks = days / 7;
            timeSpan = weeks + (weeks == 1 ? " week" : " weeks");
        } else if (days < 365) {
            long months = days / 30;
            timeSpan = months + (months == 1 ? " month" : " months");
        } else {
            long years = days / 365;
            timeSpan = years + (years == 1 ? " year" : " years");
        }

        return isFuture ? "in " + timeSpan : timeSpan + " ago";
    }
}