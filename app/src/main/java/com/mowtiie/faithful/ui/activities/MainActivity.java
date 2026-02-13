package com.mowtiie.faithful.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mowtiie.faithful.R;
import com.mowtiie.faithful.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends FaithfulActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        });

        binding.writeThought.setOnClickListener(v -> showNewThoughtDialog());
    }

    private void showNewThoughtDialog() {
        View newThoughtDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_thought, null, false);

        TextInputLayout thoughtContentLayout = newThoughtDialogView.findViewById(R.id.field_thought_content_layout);
        TextInputEditText thoughtContentText = newThoughtDialogView.findViewById(R.id.field_thought_content_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_new_thought)
                .setIcon(R.drawable.ic_thought)
                .setView(newThoughtDialogView)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_confirm, null);

        AlertDialog newThoughtDialog = builder.create();
        newThoughtDialog.setOnShowListener(dialog -> newThoughtDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String thoughtContent = Objects.requireNonNull(thoughtContentText.getText()).toString();
            if (thoughtContent.isEmpty()) {
                thoughtContentLayout.setError(getString(R.string.field_thought_content_empty_error));
                return;
            }
        }));
        newThoughtDialog.show();

        thoughtContentText.requestFocus();
        thoughtContentText.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(thoughtContentText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}