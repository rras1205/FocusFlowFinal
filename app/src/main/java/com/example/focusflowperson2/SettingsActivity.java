package com.example.focusflowperson2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchSound, switchVibration;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 1. Bind Views
        switchSound = findViewById(R.id.switch_sound);
        switchVibration = findViewById(R.id.switch_vibration);
        btnLogout = findViewById(R.id.btn_logout);

        // 2. Load Saved Preferences (Use a specific "AppConfig" file for settings)
        SharedPreferences configPrefs = getSharedPreferences("AppConfig", MODE_PRIVATE);

        // Default to true if not set yet
        boolean isSoundOn = configPrefs.getBoolean("sound_enabled", true);
        boolean isVibOn = configPrefs.getBoolean("vibration_enabled", true);

        switchSound.setChecked(isSoundOn);
        switchVibration.setChecked(isVibOn);

        // 3. Set Listeners to Save Changes Immediately
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            configPrefs.edit().putBoolean("sound_enabled", isChecked).apply();
        });

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            configPrefs.edit().putBoolean("vibration_enabled", isChecked).apply();
        });

        // 4. Log Out Logic
        btnLogout.setOnClickListener(v -> {
            // Clear User Data (Username, Email, etc.)
            SharedPreferences userPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
            userPrefs.edit().clear().apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to Login and clear the back stack (so they can't go back)
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 5. Navigation Setup
        Navigation.setUpNavigation(this);
        Navigation.highlightSelected(this, R.id.nav_settings);
    }
}