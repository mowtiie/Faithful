package com.mowtiie.faithful.data;

public enum Theme {
    LIGHT("Light"),
    DARK("Dark"),
    BATTERY_SAVING("Battery Saving"),
    SYSTEM("System Default");

    public static final Theme[] themes;
    public final String value;

    static {
        themes = values();
    }

    Theme(String value) {
        this.value = value;
    }

    public static String[] getValues() {
        String[] values = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            values[i] = values()[i].value;
        }
        return values;
    }
}