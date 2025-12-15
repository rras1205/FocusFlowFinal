package com.example.focusflowperson2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvTimer, tvQuote, tvSessionType, tvCycleCount;
    private Button btnStartPause, btnReset, btnLoad, btnAiAssistant;

    private ToneGenerator toneGen;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;

    // Timer Variables
    private long timeLeftInMillis;
    private long endTime;

    // Session Configuration
    private long workDurationMillis = 25 * 60 * 1000L;
    private long breakDurationMillis = 5 * 60 * 1000L;
    private long longBreakDurationMillis = 15 * 60 * 1000L;
    private String currentSessionName = "Focus Session";

    // Cycle Logic
    private boolean isWorkSession = true;
    private int pomodorosCompleted = 0;

    private String[] quotes = {
            "Focus on being productive instead of busy.",
            "The secret of getting ahead is getting started.",
            "It always seems impossible until it's done."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        } catch (Exception e) { e.printStackTrace(); }

        tvTimer = findViewById(R.id.tvTimer);
        tvQuote = findViewById(R.id.tvQuote);
        tvSessionType = findViewById(R.id.tvSessionType);
        tvCycleCount = findViewById(R.id.tvCycleCount);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);
        btnLoad = findViewById(R.id.btnLoad);
        btnAiAssistant = findViewById(R.id.btnAiAssistant);

        displayRandomQuote();

        btnAiAssistant.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AIActivity.class)));

        btnStartPause.setOnClickListener(v -> {
            if (isTimerRunning) pauseTimer();
            else startTimer();
        });

        // FIXED: Reset Options
        btnReset.setOnClickListener(v -> showResetConfirmationDialog());

        // FIXED: Load with Delete Option
        btnLoad.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
                Toast.makeText(this, "Timer paused for loading", Toast.LENGTH_SHORT).show();
            }
            showLoadSessionDialog();
        });

        Navigation.setUpNavigation(this);
        Navigation.highlightSelected(this, R.id.nav_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        restoreTimerState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTimerState();
    }

    private void startTimer() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        // Schedule Notification
        scheduleNotification(timeLeftInMillis);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                playNotificationSound();
                vibratePhone();
                handleSessionComplete();
            }
        }.start();

        isTimerRunning = true;
        btnStartPause.setText("Pause");
        btnStartPause.setBackgroundColor(Color.parseColor("#F44336"));
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        cancelNotification(); // Cancel notification if paused
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        btnStartPause.setText("Start");
        btnStartPause.setBackgroundColor(Color.parseColor("#38588E"));
    }

    private void handleSessionComplete() {
        // Correctly save types so Productivity Summary works
        if (isWorkSession) {
            saveCompletedSession("HISTORY_WORK");
            pomodorosCompleted++;

            // Switch to Break
            isWorkSession = false;

            if (pomodorosCompleted % 4 == 0) {
                timeLeftInMillis = longBreakDurationMillis;
                tvSessionType.setText("LONG BREAK");
                tvQuote.setText("Time to recharge completely.");
            } else {
                timeLeftInMillis = breakDurationMillis;
                tvSessionType.setText("SHORT BREAK");
                tvQuote.setText("Stretch your legs!");
            }
        } else {
            if (pomodorosCompleted % 4 == 0) {
                saveCompletedSession("HISTORY_LONG_BREAK");
            } else {
                saveCompletedSession("HISTORY_BREAK");
            }

            // Switch to Work
            isWorkSession = true;
            timeLeftInMillis = workDurationMillis;
            tvSessionType.setText(currentSessionName);
            tvQuote.setText("Back to Work!");
        }

        updateStatusUI();
        updateCountDownText();
        btnStartPause.setText("Start");
        btnStartPause.setBackgroundColor(Color.parseColor("#38588E"));
    }

    // --- FIXED: CYCLE DISPLAY LOGIC ---
    private void updateStatusUI() {
        // Example: P=0 -> Cycle 1 Work
        // Example: P=1 (just finished work) -> Cycle 1 Break
        // Example: P=4 (finished 4 works) -> Long Break

        if (pomodorosCompleted > 0 && pomodorosCompleted % 4 == 0 && !isWorkSession) {
            tvCycleCount.setText("4 Cycles Completed! 🎉");
            tvSessionType.setTextColor(Color.parseColor("#009688"));
        } else {
            // Calculate cycle number.
            // If working: (0/4)+1 = 1. (1/4)+1 = 1 (Wait, 1/4 is 0 in integer math).
            // P=0 -> Cycle 1. P=1 -> Cycle 1 (Break). P=2 -> Cycle 2.
            // Formula: (P / 4) is usually 0. We want 1, 2, 3, 4.
            // Let's use simple logic:

            int currentCycle = (pomodorosCompleted % 4);
            // If work session, it's the start of a new pomodoro unit.
            if (isWorkSession) {
                currentCycle += 1; // 0->1, 1->2...
                tvCycleCount.setText("Cycle " + currentCycle + "/4: Work");
                tvSessionType.setTextColor(Color.parseColor("#6200EE"));
            } else {
                // If break session, we are still completing the previous cycle unit.
                // P=1 means we finished Work 1. So we are in Break 1.
                tvCycleCount.setText("Cycle " + currentCycle + "/4: Short Break");
                tvSessionType.setTextColor(Color.parseColor("#009688"));
            }
        }
    }

    // --- FIXED: RESET DIALOG ---
    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Options");

        builder.setNegativeButton("Reset Timer", (dialog, which) -> {
            pauseTimer();
            // Reset to full duration of CURRENT session type
            if (isWorkSession) timeLeftInMillis = workDurationMillis;
            else if (pomodorosCompleted % 4 == 0) timeLeftInMillis = longBreakDurationMillis;
            else timeLeftInMillis = breakDurationMillis;

            updateCountDownText();
        });

        builder.setPositiveButton("Restart Cycle", (dialog, which) -> {
            pauseTimer();
            // Full Reset
            isWorkSession = true;
            pomodorosCompleted = 0;
            timeLeftInMillis = workDurationMillis;
            tvSessionType.setText(currentSessionName);
            updateStatusUI();
            updateCountDownText();
            Toast.makeText(this, "Cycle Restarted", Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    // --- FIXED: LOAD AND DELETE LOGIC ---
    private void showLoadSessionDialog() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("email", "guest@local");

        List<Session> presets = AppDatabase.getDatabase(this)
                .sessionDao()
                .getPresetsForUser(currentUserEmail);

        if (presets.isEmpty()) {
            Toast.makeText(this, "No saved presets.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[presets.size()];
        for (int i = 0; i < presets.size(); i++) {
            Session s = presets.get(i);
            names[i] = s.sessionName + " (" + s.workDuration + "m / " + s.breakDuration + "m)";
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Preset")
                .setItems(names, (dialog, which) -> {
                    Session selected = presets.get(which);
                    // Show Action Dialog (Load or Delete)
                    showPresetActionDialog(selected);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPresetActionDialog(Session session) {
        new AlertDialog.Builder(this)
                .setTitle(session.sessionName)
                .setMessage("What do you want to do?")
                .setPositiveButton("Load", (dialog, which) -> {
                    applySessionSettings(session);
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    AppDatabase.getDatabase(this).sessionDao().delete(session);
                    Toast.makeText(this, "Preset Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    // --- NOTIFICATION LOGIC ---
    private void scheduleNotification(long durationInMillis) {
        try {
            Intent intent = new Intent(this, NotificationReceiver.class);
            // Determine title/message based on what mode we are CURRENTLY in
            String title = isWorkSession ? "Focus Session Complete!" : "Break is over!";
            String msg = isWorkSession ? "Time to take a break." : "Ready to get back to work?";

            intent.putExtra("title", title);
            intent.putExtra("message", msg);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long triggerTime = System.currentTimeMillis() + durationInMillis;

            if (alarmManager != null) {
                // Check for permission on Android 12+ (API 31+)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    } else {
                        // Fallback: Use standard set() if exact permission is missing to prevent crash
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }
                } else {
                    // Older Android versions
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            }
        } catch (SecurityException e) {
            // This catches the specific crash you are seeing
            e.printStackTrace();
            Toast.makeText(this, "Notification permission required for background timer", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    // --- STANDARD HELPERS ---
    private void applySessionSettings(Session session) {
        currentSessionName = session.sessionName;
        workDurationMillis = session.workDuration * 60 * 1000L;
        breakDurationMillis = session.breakDuration * 60 * 1000L;
        longBreakDurationMillis = session.longBreakDuration * 60 * 1000L;

        // When loading a preset, we typically restart the cycle logic to avoid confusion
        isWorkSession = true;
        timeLeftInMillis = workDurationMillis;
        tvSessionType.setText(currentSessionName);

        updateCountDownText();
        updateStatusUI();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void saveCompletedSession(String type) {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("email", "guest@local");

        Session s = new Session(
                currentUserEmail,
                currentSessionName,
                (int)(workDurationMillis/60000),
                (int)(breakDurationMillis/60000),
                (int)(longBreakDurationMillis/60000),
                type
        );
        new Thread(() -> AppDatabase.getDatabase(this).sessionDao().insert(s)).start();
    }

    private void saveTimerState() {
        SharedPreferences prefs = getSharedPreferences("TimerState", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("configWork", workDurationMillis);
        editor.putLong("configBreak", breakDurationMillis);
        editor.putLong("configLong", longBreakDurationMillis);
        editor.putString("configName", currentSessionName);
        editor.putBoolean("isWorkSession", isWorkSession);
        editor.putInt("pomodorosCompleted", pomodorosCompleted);
        editor.putLong("timeLeft", timeLeftInMillis);
        editor.putBoolean("isRunning", isTimerRunning);
        editor.putLong("endTime", endTime);
        editor.apply();

        if (countDownTimer != null) countDownTimer.cancel();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void restoreTimerState() {
        SharedPreferences prefs = getSharedPreferences("TimerState", MODE_PRIVATE);
        workDurationMillis = prefs.getLong("configWork", 25 * 60 * 1000L);
        breakDurationMillis = prefs.getLong("configBreak", 5 * 60 * 1000L);
        longBreakDurationMillis = prefs.getLong("configLong", 15 * 60 * 1000L);
        currentSessionName = prefs.getString("configName", "Focus Session");
        isWorkSession = prefs.getBoolean("isWorkSession", true);
        pomodorosCompleted = prefs.getInt("pomodorosCompleted", 0);
        timeLeftInMillis = prefs.getLong("timeLeft", workDurationMillis);
        isTimerRunning = prefs.getBoolean("isRunning", false);
        endTime = prefs.getLong("endTime", 0);

        updateStatusUI();

        if (isTimerRunning) {
            long currentTime = System.currentTimeMillis();
            timeLeftInMillis = endTime - currentTime;
            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                isTimerRunning = false;
                updateCountDownText();
                handleSessionComplete();
            } else {
                startTimer();
            }
        } else {
            updateCountDownText();
        }
    }

    private void displayRandomQuote() {
        int randomIndex = new Random().nextInt(quotes.length);
        tvQuote.setText(quotes[randomIndex]);
    }

    private void playNotificationSound() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        if (!prefs.getBoolean("sound_enabled", true)) return;
        try { if (toneGen != null) toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); }
        catch (Exception e) {}
    }

    private void vibratePhone() {
        SharedPreferences prefs = getSharedPreferences("AppConfig", MODE_PRIVATE);
        if (!prefs.getBoolean("vibration_enabled", true)) return;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGen != null) toneGen.release();
    }
}