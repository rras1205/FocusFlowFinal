package com.example.focusflowperson2;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class Navigation extends Activity {

    public static void setUpNavigation(final Activity activity) {

        ImageView navHome = activity.findViewById(R.id.nav_home);
        ImageView navAdd = activity.findViewById(R.id.nav_add);
        ImageView navProfile = activity.findViewById(R.id.nav_profile);
        ImageView navSettings = activity.findViewById(R.id.nav_settings);

        // Home
        navHome.setOnClickListener(v -> {
            if (!(activity instanceof MainActivity)) {
                Intent intent = new Intent(activity, MainActivity.class);
                // Clears the stack so Home becomes the top again
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0); // Remove animation for smoother feel
                activity.finish();
            }
        });

        // Add Session
        navAdd.setOnClickListener(v -> {
            if (!(activity instanceof AddSessionActivity)) {
                Intent intent = new Intent(activity, AddSessionActivity.class);
                activity.startActivity(intent);
                // We do NOT finish() here, so the user can press 'Back' to return to where they were
            }
        });

        // Profile
        navProfile.setOnClickListener(v -> {
            if (!(activity instanceof Profile)) {
                Intent intent = new Intent(activity, Profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });

        // Settings Listener
        navSettings.setOnClickListener(v -> {
            // Only launch if we aren't already on the settings page
            if (!(activity instanceof SettingsActivity)) {
                Intent intent = new Intent(activity, SettingsActivity.class);

                // Clear top so we don't stack activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });
    }

    public static void highlightSelected(Activity activity, int activeId) {
        ImageView navHome = activity.findViewById(R.id.nav_home);
        ImageView navAdd = activity.findViewById(R.id.nav_add);
        ImageView navProfile = activity.findViewById(R.id.nav_profile);
        ImageView navSettings = activity.findViewById(R.id.nav_settings);

        // Reset all icons to default
        int inactiveColor = ContextCompat.getColor(activity, R.color.white);
        navHome.setColorFilter(inactiveColor);
        navAdd.setColorFilter(inactiveColor);
        navProfile.setColorFilter(inactiveColor);
        navSettings.setColorFilter(inactiveColor);

        // change colour of the active icon
        int activeColor = ContextCompat.getColor(activity, R.color.black);
        ImageView activeIcon = activity.findViewById(activeId);
        activeIcon.setColorFilter(activeColor);
    }
}
