package com.mowtiie.faithful.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mowtiie.faithful.R;
import com.mowtiie.faithful.databinding.ActivityLockBinding;
import com.mowtiie.faithful.util.LockUtil;
import com.mowtiie.faithful.util.PasswordUtil;
import com.mowtiie.faithful.util.SettingUtil;

import java.util.Objects;

public class LockActivity extends AppCompatActivity {

    private ActivityLockBinding binding;
    private SettingUtil settingUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        settingUtil = new SettingUtil(this);
        binding.unlock.setOnClickListener(v -> {
            String input = Objects.requireNonNull(binding.passwordText.getText()).toString();
            String passwordHash = settingUtil.getPassword();

            try {
                if (input.isEmpty()) {
                    binding.passwordLayout.setError(getString(R.string.field_app_password_error));
                    return;
                }

                if (PasswordUtil.verifyPassword(input, passwordHash)) {
                    LockUtil.getInstance().updateLastUsed();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    binding.passwordText.setText("");
                    binding.passwordLayout.setError(getString(R.string.field_app_password_incorrect_error));
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}