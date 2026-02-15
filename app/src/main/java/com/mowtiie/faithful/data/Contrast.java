package com.mowtiie.faithful.data;

public enum Contrast {
    LOW("Low"),
    MEDIUM( "Medium"),
    HIGH("High");

    private static final Contrast[] contrasts;
    public final String value;

    static {
        contrasts = values();
    }

    Contrast(String value) {
        this.value = value;
    }

    public static String[] getValues() {
        String[] values = new String[contrasts.length];
        for (int i = 0; i < contrasts.length; i++) {
            values[i] = contrasts[i].value;
        }
        return values;
    }
}