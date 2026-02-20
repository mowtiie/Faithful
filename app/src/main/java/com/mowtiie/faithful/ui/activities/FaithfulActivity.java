package com.mowtiie.faithful.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.data.Contrast;
import com.mowtiie.faithful.util.LockUtil;
import com.mowtiie.faithful.util.SettingUtil;

public abstract class FaithfulActivity extends AppCompatActivity {

    protected SettingUtil settingUtil;

    private boolean lockRedirectPending = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingUtil = new SettingUtil(this);

        if (settingUtil.isScreenPrivacyEnabled()) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }

        String theme = settingUtil.getTheme();
        if (theme.equals(Theme.SYSTEM.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if (theme.equals(Theme.BATTERY_SAVING.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        else if (theme.equals(Theme.LIGHT.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (theme.equals(Theme.DARK.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        String contrast = settingUtil.getContrast();
        if (contrast.equals(Contrast.LOW.value)) setTheme(R.style.Theme_Faithful);
        else if (contrast.equals(Contrast.MEDIUM.value)) setTheme(R.style.Theme_Faithful_MediumContrast);
        else if (contrast.equals(Contrast.HIGH.value)) setTheme(R.style.Theme_Faithful_HighContrast);

        if (settingUtil.isDynamicColors()) DynamicColors.applyToActivityIfAvailable(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this instanceof LockActivity) return;

        if (!lockRedirectPending) {
            String passwordHash = settingUtil.getPassword();
            if (passwordHash != null && LockUtil.getInstance().shouldLock()) {
                lockRedirectPending = true;

                Intent intent = new Intent(this, LockActivity.class);
                intent.putExtra(LockActivity.EXTRA_RETURN_CLASS, getClass().getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lockRedirectPending = false;
        LockUtil.getInstance().updateLastUsed();
    }
}