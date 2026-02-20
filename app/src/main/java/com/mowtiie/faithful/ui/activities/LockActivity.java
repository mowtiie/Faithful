package com.mowtiie.faithful.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mowtiie.faithful.R;
import com.mowtiie.faithful.databinding.ActivityLockBinding;
import com.mowtiie.faithful.util.LockUtil;
import com.mowtiie.faithful.util.PasswordUtil;

import java.util.Objects;

public class LockActivity extends FaithfulActivity {

    public static final String EXTRA_RETURN_CLASS = "extra_return_class";

    private ActivityLockBinding binding;

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
                    String returnClassName = getIntent().getStringExtra(EXTRA_RETURN_CLASS);
                    Class<?> returnClass = MainActivity.class;
                    if (returnClassName != null) {
                        try {
                            returnClass = Class.forName(returnClassName);
                        } catch (ClassNotFoundException e) {
                            Log.e("LockActivity", "Return class not found: " + returnClassName, e);
                        }
                    }

                    Intent intent = new Intent(this, returnClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    binding.passwordText.setText("");
                    binding.passwordLayout.setError(getString(R.string.field_app_password_incorrect_error));
                }
            } catch (Exception e) {
                Log.e("LockActivity", "Error verifying password", e);
                Toast.makeText(this, R.string.toast_unlock_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
