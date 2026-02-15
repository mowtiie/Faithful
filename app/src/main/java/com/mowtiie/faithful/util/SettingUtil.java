package com.mowtiie.faithful.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.data.Contrast;

public class SettingUtil {

    private final SharedPreferences sharedPreferences;

    public SettingUtil(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getTimestamp() {
        return sharedPreferences.getString("timestamp", "Formal");
    }

    public void setTimestamp(String value) {
        sharedPreferences.edit().putString("timestamp", value).apply();
    }

    public String getTheme() {
        return sharedPreferences.getString("theme", Theme.SYSTEM.value);
    }

    public void setTheme(String value) {
        sharedPreferences.edit().putString("theme", value).apply();
    }

    public String getContrast() {
        return sharedPreferences.getString("contrast", Contrast.LOW.value);
    }

    public void setContrast(String value) {
        sharedPreferences.edit().putString("Contrast", value).apply();
    }

    public boolean isDynamicColors() {
        return sharedPreferences.getBoolean("dynamic_colors", false);
    }

    public void setDynamicColors(boolean value) {
        sharedPreferences.edit().putBoolean("dynamic_colors", value).apply();
    }

    public void setScreenPrivacy(boolean value) {
        sharedPreferences.edit().putBoolean("screen_privacy", value).apply();
    }

    public boolean isScreenPrivacyEnabled() {
        return sharedPreferences.getBoolean("screen_privacy", false);
    }
}