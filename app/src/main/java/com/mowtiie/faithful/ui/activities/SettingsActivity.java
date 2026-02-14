package com.mowtiie.faithful.ui.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.databinding.ActivitySettingsBinding;
import com.mowtiie.faithful.util.SettingUtil;

public class SettingsActivity extends FaithfulActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private SettingUtil settingUtil;

        private ListPreference listTheme;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
            setPreferences();

            listTheme.setEntries(Theme.getValues());
            listTheme.setEntryValues(Theme.getValues());
            listTheme.setSummary(settingUtil.getTheme());
            listTheme.setValue(settingUtil.getTheme());
            listTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedTheme = (String) newValue;
                if (selectedTheme.equals(Theme.SYSTEM.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                if (selectedTheme.equals(Theme.BATTERY_SAVING.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                if (selectedTheme.equals(Theme.LIGHT.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (selectedTheme.equals(Theme.DARK.value)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                settingUtil.setTheme(selectedTheme);
                return true;
            });
        }

        private void setPreferences() {
            settingUtil = new SettingUtil(requireContext());

            listTheme = findPreference("theme");
        }
    }
}