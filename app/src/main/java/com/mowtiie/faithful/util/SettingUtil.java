package com.mowtiie.faithful.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.data.thought.Contrast;

public class SettingUtil {

    private final SharedPreferences sharedPreferences;

    public SettingUtil(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
}