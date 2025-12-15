package com.example.focusflowperson2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductivitySummary extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvWorkSessions, tvBreakSessions, tvWorkTime, tvBreakTime;
    private ProgressBar progressWorkSessions, progressBreakSessions, progressWorkTime, progressBreakTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productivity_summary);

        btnBack = findViewById(R.id.prosum_btnBack);

        tvWorkSessions = findViewById(R.id.prosum_tvWorkSessions);
        tvBreakSessions = findViewById(R.id.prosum_tvBreakSessions);
        tvWorkTime = findViewById(R.id.prosum_tvWorkTime);
        tvBreakTime = findViewById(R.id.prosum_tvBreakTime);

        progressWorkSessions = findViewById(R.id.prosum_progressWorkSessions);
        progressBreakSessions = findViewById(R.id.prosum_progressBreakSessions);
        progressWorkTime = findViewById(R.id.prosum_progressWorkTime);
        progressBreakTime = findViewById(R.id.prosum_progressBreakTime);

        btnBack.setOnClickListener(view -> finish());

        loadStatistics();
    }

    private void loadStatistics() {
        // 1. Get Current User Email
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("email", "guest@local");

        AppDatabase db = AppDatabase.getDatabase(this);
        SessionDao dao = db.sessionDao();

        // 2. Get Specific Counts for THIS user using the new Queries
        int totalWorkSessions = dao.getWorkSessionCount(currentUserEmail);
        int totalBreakSessions = dao.getBreakSessionCount(currentUserEmail);

        // 3. Get Time
        int totalWorkTime = dao.getTotalWorkTime(currentUserEmail);
        int totalBreakTime = dao.getTotalBreakTime(currentUserEmail);

        // 4. Update UI
        tvWorkSessions.setText(String.valueOf(totalWorkSessions));
        tvBreakSessions.setText(String.valueOf(totalBreakSessions));
        tvWorkTime.setText(totalWorkTime + " min");
        tvBreakTime.setText(totalBreakTime + " min");

        // 5. Update Progress Bars (Dynamic Max)
        progressWorkSessions.setMax(Math.max(totalWorkSessions + 10, 25));
        progressWorkSessions.setProgress(totalWorkSessions);

        progressBreakSessions.setMax(Math.max(totalBreakSessions + 10, 10));
        progressBreakSessions.setProgress(totalBreakSessions);

        progressWorkTime.setMax(Math.max(totalWorkTime + 60, 600));
        progressWorkTime.setProgress(totalWorkTime);

        progressBreakTime.setMax(Math.max(totalBreakTime + 30, 200));
        progressBreakTime.setProgress(totalBreakTime);
    }
}