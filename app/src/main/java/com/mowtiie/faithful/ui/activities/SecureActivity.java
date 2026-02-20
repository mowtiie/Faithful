package com.mowtiie.faithful.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mowtiie.faithful.util.SettingUtil;

public class SecureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingUtil settingUtil = new SettingUtil(this);
        String passwordHash = settingUtil.getPassword();

        if (passwordHash == null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Intent lockIntent = new Intent(this, LockActivity.class);
            lockIntent.putExtra(LockActivity.EXTRA_RETURN_CLASS, MainActivity.class.getName());
            startActivity(lockIntent);
        }
        finish();
    }
}