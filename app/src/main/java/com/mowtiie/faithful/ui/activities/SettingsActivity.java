package com.mowtiie.faithful.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.data.Database;
import com.mowtiie.faithful.data.Theme;
import com.mowtiie.faithful.data.Contrast;
import com.mowtiie.faithful.data.thought.Thought;
import com.mowtiie.faithful.data.thought.ThoughtRepository;
import com.mowtiie.faithful.databinding.ActivitySettingsBinding;
import com.mowtiie.faithful.util.DateTimeUtil;
import com.mowtiie.faithful.util.SettingUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

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
        private ThoughtRepository thoughtRepository;

        private Preference appLicense;
        private Preference appVersion;
        private Preference importThoughts;
        private Preference exportThoughts;

        private ListPreference listTheme;
        private ListPreference listContrast;
        private ListPreference listTimestamp;

        private SwitchPreferenceCompat switchDynamicColors;
        private SwitchPreferenceCompat switchScreenPrivacy;
        private SwitchPreferenceCompat switchAppLock;

        private int easterEggCounter = 0;

        private final ActivityResultLauncher<Intent> exportDataLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            exportJSON(data.getData());
                        }
                    }
                });

        private final ActivityResultLauncher<Intent> importDataLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            importJSON(data.getData());
                        }
                    }
                });

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
            setPreferences();

            importThoughts.setOnPreferenceClickListener(preference -> {
                importData();
                return true;
            });

            exportThoughts.setOnPreferenceClickListener(preference -> {
                exportData();
                return true;
            });

            try {
                PackageManager packageManager = requireContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(requireContext().getPackageName(), 0);
                appVersion.setSummary(packageInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

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

            String[] listTimestampItems = {"Dynamic", "Formal"};
            listTimestamp.setEntries(listTimestampItems);
            listTimestamp.setEntryValues(listTimestampItems);
            listTimestamp.setSummary(settingUtil.getTimestamp());
            listTimestamp.setValue(settingUtil.getTimestamp());
            listTimestamp.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedTimestamp = (String) newValue;
                listTimestamp.setSummary(selectedTimestamp);
                settingUtil.setTimestamp(selectedTimestamp);
                return true;
            });

            switchDynamicColors.setVisible(DynamicColors.isDynamicColorAvailable());
            switchDynamicColors.setChecked(settingUtil.isDynamicColors());
            switchDynamicColors.setOnPreferenceChangeListener((preference, isChecked) -> {
                settingUtil.setDynamicColors((boolean) isChecked);
                requireActivity().recreate();
                return true;
            });

            switchScreenPrivacy.setChecked(settingUtil.isScreenPrivacyEnabled());
            switchScreenPrivacy.setOnPreferenceChangeListener((preference, isChecked) -> {
                settingUtil.setScreenPrivacy((boolean) isChecked);
                requireActivity().recreate();
                return true;
            });

            appLicense.setOnPreferenceClickListener(preference -> {
                showLicenseDialog();
                return true;
            });

            appVersion.setOnPreferenceClickListener(preference -> {
                easterEggCounter++;
                if (easterEggCounter == 7) {
                    Toast.makeText(requireContext(), R.string.app_easter_egg, Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }

        private void importJSON(Uri uri) {
            StringBuilder jsonBuilder = new StringBuilder();

            try (Database database = new Database(requireContext())) {
                SQLiteDatabase writableDatabase = database.getWritableDatabase();
                InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                bufferedReader.close();

                JSONObject jsonImport = new JSONObject(jsonBuilder.toString());
                JSONArray savingJsonArray = jsonImport.getJSONArray(Database.TABLE_THOUGHT);

                try {
                    writableDatabase.beginTransaction();
                    for (int i = 0; i < savingJsonArray.length(); i++) {
                        JSONObject thoughtObject = savingJsonArray.getJSONObject(i);
                        ContentValues thoughtValues = new ContentValues();

                        thoughtValues.put(Database.COLUMN_THOUGHT_ID, thoughtObject.getString(Database.COLUMN_THOUGHT_ID));
                        thoughtValues.put(Database.COLUMN_THOUGHT_CONTENT, thoughtObject.getString(Database.COLUMN_THOUGHT_CONTENT));
                        thoughtValues.put(Database.COLUMN_THOUGHT_TIMESTAMP, thoughtObject.getLong(Database.COLUMN_THOUGHT_TIMESTAMP));
                        writableDatabase.insert(Database.TABLE_THOUGHT, null, thoughtValues);
                    }

                    writableDatabase.setTransactionSuccessful();
                    Toast.makeText(requireContext(), R.string.toast_import_successful, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("Import", "Something went wrong while importing thoughts", e);
                } finally {
                    writableDatabase.endTransaction();
                }
            } catch (Exception e) {
                Log.e("Import", "Something went wrong while importing", e);
            }
        }

        private void exportJSON(Uri uri) {
            ArrayList<Thought> thoughts = new ArrayList<>(thoughtRepository.getAll());
            JSONArray thoughtJsonArray = new JSONArray();

            try {
                for (Thought thought : thoughts) {
                    JSONObject thoughtObject = new JSONObject();
                    thoughtObject.put(Database.COLUMN_THOUGHT_ID, thought.getId());
                    thoughtObject.put(Database.COLUMN_THOUGHT_CONTENT, thought.getContent());
                    thoughtObject.put(Database.COLUMN_THOUGHT_TIMESTAMP, thought.getTimestamp());
                    thoughtJsonArray.put(thoughtObject);
                }
            } catch (Exception e) {
                Log.e("Export", "Something went wrong when collecting thoughts", e);
            }

            try {
                JSONObject jsonExport = new JSONObject();
                jsonExport.put(Database.TABLE_THOUGHT, thoughtJsonArray);

                OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write(jsonExport.toString().getBytes());
                    outputStream.close();
                }

                Toast.makeText(requireContext(), R.string.toast_export_successful, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Export", "Something went wrong when exporting", e);
            }
        }

        private void exportData() {
            if (noThoughtsFound()) {
                Toast.makeText(requireContext(), R.string.toast_no_thoughts, Toast.LENGTH_SHORT).show();
            } else {
                Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
                exportIntent.setType("application/json");
                exportIntent.putExtra(Intent.EXTRA_TITLE, "thoughts.json");
                exportDataLauncher.launch(exportIntent);
            }
        }

        private boolean noThoughtsFound() {
            try (ThoughtRepository thoughtRepository = new ThoughtRepository(requireContext())) {
                ArrayList<Thought> thoughts = new ArrayList<>(thoughtRepository.getAll());
                if (thoughts.isEmpty()) return true;
            }
            return false;
        }

        private void importData() {
            Intent importIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            importIntent.addCategory(Intent.CATEGORY_OPENABLE);
            importIntent.setType("application/json");
            importDataLauncher.launch(importIntent);
        }

        private void setPreferences() {
            settingUtil = new SettingUtil(requireContext());
            thoughtRepository = new ThoughtRepository(requireContext());

            listTheme = findPreference("theme");
            listContrast = findPreference("contrast");
            listTimestamp = findPreference("timestamp");

            switchDynamicColors = findPreference("dynamic_colors");
            switchScreenPrivacy = findPreference("screen_privacy");
            switchAppLock = findPreference("app_lock");

            appLicense = findPreference("app_license");
            appVersion = findPreference("app_version");
            importThoughts = findPreference("import");
            exportThoughts = findPreference("export");
        }

        private void showLicenseDialog() {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.preference_app_license)
                    .setIcon(R.drawable.ic_license)
                    .setMessage(readLicenseFromAssets())
                    .setPositiveButton(R.string.dialog_button_close, null);

            Dialog dialog = builder.create();
            dialog.show();
        }

        private String readLicenseFromAssets() {
            StringBuilder stringBuilder = new StringBuilder();
            AssetManager assetManager = requireContext().getAssets();

            try (InputStream inputStream = assetManager.open("license.txt")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return stringBuilder.toString();
        }
    }
}