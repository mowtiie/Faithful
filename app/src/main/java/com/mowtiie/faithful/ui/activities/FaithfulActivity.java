package com.mowtiie.faithful.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mowtiie.faithful.util.SettingUtil;

public abstract class FaithfulActivity extends AppCompatActivity {

    protected SettingUtil settingUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        settingUtil = new SettingUtil(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }
}