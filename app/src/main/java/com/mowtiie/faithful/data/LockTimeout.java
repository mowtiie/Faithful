package com.mowtiie.faithful.data;

public enum LockTimeout {

    IMMEDIATELY("Immediately", 0),
    SECONDS_15("15 seconds", 15000),
    SECONDS_30("30 seconds", 30000),
    MINUTE_1("1 minute", 60000),
    MINUTES_5("5 minutes", 300000),
    MINUTES_15("15 minutes", 900000),
    MINUTES_30("30 minutes", 1800000),
    HOUR_1("1 hour", 3600000);

    public final String label;
    public final long milliseconds;

    LockTimeout(String label, long milliseconds) {
        this.label = label;
        this.milliseconds = milliseconds;
    }
}