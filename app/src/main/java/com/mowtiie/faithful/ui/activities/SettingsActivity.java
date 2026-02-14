package com.mowtiie.faithful.ui.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.color.DynamicColors;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.data.thought.Contrast;
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
        private ListPreference listContrast;

        private SwitchPreferenceCompat switchDynamicColors;

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

            listContrast.setEntries(Contrast.getValues());
            listContrast.setEntryValues(Contrast.getValues());
            listContrast.setSummary(settingUtil.getContrast());
            listContrast.setValue(settingUtil.getContrast());
            listContrast.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedContrast = (String) newValue;
                if (selectedContrast.equals(Contrast.LOW.value)) requireActivity().setTheme(R.style.Theme_Faithful);
                if (selectedContrast.equals(Contrast.MEDIUM.value)) requireActivity().setTheme(R.style.Theme_Faithful_MediumContrast);
                if (selectedContrast.equals(Contrast.HIGH.value)) requireActivity().setTheme(R.style.Theme_Faithful_HighContrast);
                requireActivity().recreate();
                settingUtil.setContrast(selectedContrast);
                return true;
            });

            switchDynamicColors.setVisible(DynamicColors.isDynamicColorAvailable());
            switchDynamicColors.setChecked(settingUtil.isDynamicColors());
            switchDynamicColors.setOnPreferenceChangeListener((preference, isChecked) -> {
                settingUtil.setDynamicColors((boolean) isChecked);
                requireActivity().recreate();
                return true;
            });
        }

        private void setPreferences() {
            settingUtil = new SettingUtil(requireContext());

            listTheme = findPreference("theme");
            listContrast = findPreference("contrast");

            switchDynamicColors = findPreference("dynamic_colors");
        }
    }
}