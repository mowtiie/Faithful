package com.mowtiie.faithful.data;

import com.mowtiie.faithful.R;

public enum LockTimeout {

    IMMEDIATELY(0, R.string.timeout_immediately),
    SECONDS_30(30_000, R.string.timeout_30_seconds),
    MINUTE_1(60_000, R.string.timeout_1_minute),
    MINUTES_5(300_000, R.string.timeout_5_minutes),
    MINUTES_15(900_000, R.string.timeout_15_minutes);

    public final long milliseconds;
    public final int label;

    LockTimeout(long milliseconds, int label) {
        this.milliseconds = milliseconds;
        this.label = label;
    }

    public static LockTimeout fromName(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return SECONDS_30;
        }
    }
}