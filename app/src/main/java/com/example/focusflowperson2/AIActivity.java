package com.example.focusflowperson2;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AIActivity extends AppCompatActivity {

    private EditText etInput;
    private TextView tvChatLog;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        etInput = findViewById(R.id.etInput);
        tvChatLog = findViewById(R.id.tvChatLog);
        btnSend = findViewById(R.id.btnSend);

        // Welcome Message
        appendMessage("AI: Hi! I am your FocusFlow assistant. Feeling tired, stressed, or distracted? Let me know!");

        btnSend.setOnClickListener(v -> {
            String userMsg = etInput.getText().toString().trim();
            if (!userMsg.isEmpty()) {
                appendMessage("You: " + userMsg);
                etInput.setText("");

                // Simulate AI thinking delay
                new Handler().postDelayed(() -> {
                    String response = getAIResponse(userMsg.toLowerCase());
                    appendMessage("AI: " + response);
                }, 800);
            }
        });

        // Setup Back Button to go Home
        Navigation.setUpNavigation(this);
    }

    private void appendMessage(String msg) {
        tvChatLog.append(msg + "\n\n");
    }

    // Simple keyword-based AI logic
    // Inside AIActivity.java

    private String getAIResponse(String input) {
        input = input.toLowerCase(); // Convert to lowercase for easier matching

        // 1. ENERGY & HEALTH
        if (input.contains("tired") || input.contains("sleepy") || input.contains("exhausted")) {
            return "Low energy? Try a 20-minute power nap (NASA nap) or drink a glass of cold water. Don't rely solely on caffeine!";
        }
        else if (input.contains("hungry") || input.contains("snack")) {
            return "Brain food is important! Try nuts, berries, or dark chocolate. Avoid heavy sugars that cause energy crashes.";
        }

        // 2. EMOTIONAL STATE
        else if (input.contains("stress") || input.contains("anxious") || input.contains("panic")) {
            return "Take a deep breath. Try the 4-7-8 technique: Inhale for 4s, Hold for 7s, Exhale for 8s. You can do this.";
        }
        else if (input.contains("bored") || input.contains("boring")) {
            return "Boredom often means the task is too easy or too hard. Try gamifying it—how much can you finish in the next 10 minutes?";
        }
        else if (input.contains("overwhelm")) {
            return "Just focus on ONE thing. Write down the single most important task and hide the rest of the list.";
        }

        // 3. PRODUCTIVITY BLOCKS
        else if (input.contains("distract")) {
            return "Distractions are the enemy! Clear your desk, put your phone in another room, and try working for just 5 minutes to break the resistance.";
        }
        else if (input.contains("procrastinat") || input.contains("stuck") || input.contains("lazy")) {
            return "The '5-Minute Rule' helps here: Promise yourself you'll do the task for just 5 minutes. Usually, you'll keep going after that.";
        }
        else if (input.contains("motivat")) {
            return "Motivation is unreliable; discipline is key. Don't wait to 'feel' like doing it. Action leads to motivation, not the other way around.";
        }

        // 4. PLANNING & STRATEGY
        else if (input.contains("plan") || input.contains("schedule") || input.contains("to do")) {
            return "Try the 'Eat the Frog' method: Identify your hardest task and do it first thing in the morning.";
        }
        else if (input.contains("break") || input.contains("pause")) {
            return "Breaks are productive! Use the Pomodoro technique: 25 minutes of focus followed by a 5-minute break.";
        }

        // 5. GREETINGS & MISC
        else if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return "Hello there! I'm ready to help you focus. How are you feeling right now?";
        }
        else if (input.contains("thanks") || input.contains("thank you")) {
            return "You're welcome! Now, let's get back to work!";
        }

        // 6. FALLBACK (If no keywords match)
        else {
            return "I see. Sometimes the hardest part is just starting. Why not set a timer for 25 minutes and see how far you get?";
        }
    }
}