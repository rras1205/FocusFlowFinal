package com.example.focusflowperson2;

import android.content.SharedPreferences; // <--- ADDED THIS IMPORT
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        // Bind UI Elements
        EditText etName = findViewById(R.id.etSessionName);
        RadioGroup radioGroup = findViewById(R.id.radioGroupPresets);
        LinearLayout layoutCustom = findViewById(R.id.layoutCustomInputs);
        EditText etWork = findViewById(R.id.etWorkDuration);
        EditText etBreak = findViewById(R.id.etBreakDuration);
        EditText etLongBreak = findViewById(R.id.etLongBreakDuration);

        Button btnSave = findViewById(R.id.btnSaveSession);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Show/Hide Custom fields based on selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCustom) {
                layoutCustom.setVisibility(View.VISIBLE);
            } else {
                layoutCustom.setVisibility(View.GONE);
            }
        });

        // Cancel Button Logic
        btnCancel.setOnClickListener(v -> {
            finish(); // Simply closes this screen and goes back to Home
        });

        // Save Button Logic
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            if (name.isEmpty()) name = "Focus Session"; // Default name

            int work = 25;
            int shortBreak = 5;
            int longBreak = 15;

            // Check which option is selected
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.rbStandard) {
                // Values already set to 25/5/15
            } else if (selectedId == R.id.rbLong) {
                work = 50;
                shortBreak = 10;
                longBreak = 20;
            } else {
                // Custom Logic
                String w = etWork.getText().toString();
                String b = etBreak.getText().toString();
                String lb = etLongBreak.getText().toString();

                if (w.isEmpty() || b.isEmpty() || lb.isEmpty()) {
                    Toast.makeText(this, "Please enter all custom times", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    work = Integer.parseInt(w);
                    shortBreak = Integer.parseInt(b);
                    longBreak = Integer.parseInt(lb);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Numbers are too large!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // --- FIX START: Get User Email & Use Updated Constructor ---

            // 1. Get current logged-in user email
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            String currentUserEmail = prefs.getString("email", "guest@local");

            // 2. Create Session with Email and Type
            // Order: email, name, work, short, long, type
            Session newSession = new Session(currentUserEmail, name, work, shortBreak, longBreak, "PRESET");

            AppDatabase.getDatabase(this).sessionDao().insert(newSession);

            // --- FIX END ---

            Toast.makeText(this, "Saved! Tap 'Load' on Home to use it.", Toast.LENGTH_LONG).show();

            finish();
        });

        // navigation
        Navigation.setUpNavigation(this);
        Navigation.highlightSelected(this, R.id.nav_add);
    }
}