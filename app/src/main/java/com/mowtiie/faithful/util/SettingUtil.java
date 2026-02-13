package com.mowtiie.faithful.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class SettingUtil {

    private final SharedPreferences sharedPreferences;

    public SettingUtil(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}