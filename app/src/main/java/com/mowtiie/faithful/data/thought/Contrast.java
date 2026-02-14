package com.mowtiie.faithful.data.thought;

public enum Contrast {
    LOW("contrast_low", "Low"),
    MEDIUM("contrast_medium", "Medium"),
    HIGH("contrast_high", "High");

    private static final Contrast[] contrasts;
    public final String value;
    public final String name;

    static {
        contrasts = values();
    }

    Contrast(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String[] toNameArray() {
        String[] strings = new String[contrasts.length];
        for (int i = 0; i < contrasts.length; i++) {
            strings[i] = contrasts[i].name;
        }
        return strings;
    }

    public static String[] toValueArray() {
        String[] strings = new String[contrasts.length];
        for (int i = 0; i < contrasts.length; i++) {
            strings[i] = contrasts[i].value;
        }
        return strings;
    }

    public static String getName(String value) {
        for (Contrast contrast : contrasts) {
            if (contrast.value.equals(value)) {
                return contrast.name;
            }
        }
        return null;
    }
}