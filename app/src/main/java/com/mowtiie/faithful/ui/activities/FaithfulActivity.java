package com.mowtiie.faithful.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.data.thought.Contrast;
import com.mowtiie.faithful.util.SettingUtil;

public abstract class FaithfulActivity extends AppCompatActivity {

    protected SettingUtil settingUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        settingUtil = new SettingUtil(this);
        super.onCreate(savedInstanceState);

        if (settingUtil.isScreenPrivacyEnabled()) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }

        String theme = settingUtil.getTheme();
        if (theme.equals(Theme.SYSTEM.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (theme.equals(Theme.BATTERY_SAVING.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        if (theme.equals(Theme.LIGHT.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (theme.equals(Theme.DARK.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        String contrast = settingUtil.getContrast();
        if (contrast.equals(Contrast.LOW.value)) setTheme(R.style.Theme_Faithful);
        if (contrast.equals(Contrast.MEDIUM.value)) setTheme(R.style.Theme_Faithful_MediumContrast);
        if (contrast.equals(Contrast.HIGH.value)) setTheme(R.style.Theme_Faithful_HighContrast);

        if (settingUtil.isDynamicColors()) DynamicColors.applyToActivityIfAvailable(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }
}