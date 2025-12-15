package com.example.focusflowperson2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private ImageView profileImg;
    private TextView txtProfileUsername;
    private TextView txtProfileEmail;
    private Button btnSummary, btnSave;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImg = findViewById(R.id.profile_img);
        txtProfileUsername = findViewById(R.id.txt_profile_username);
        txtProfileEmail = findViewById(R.id.txt_profile_email);
        btnSummary = findViewById(R.id.profile_btn_summary);
        btnSave = findViewById(R.id.profile_btn_save);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username", "Unknown");
        String email = prefs.getString("email", "No email");
        String uriString = prefs.getString("profile_image_uri", "");

        txtProfileUsername.setText(username);
        txtProfileEmail.setText(email);

        if (!uriString.isEmpty()) {
            profileImg.setImageURI(Uri.parse(uriString));
        }

        txtProfileUsername.setOnClickListener(v -> showEditDialog("Edit Username", txtProfileUsername));

        profileImg.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK);
            gallery.setType("image/*");
            startActivityForResult(gallery, PICK_IMAGE);
        });

        btnSummary.setOnClickListener(v -> startActivity(new Intent(Profile.this, ProductivitySummary.class)));

        btnSave.setOnClickListener(v -> saveChanges());

        Navigation.setUpNavigation(this);
        Navigation.highlightSelected(this, R.id.nav_profile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            selectedImageUri = data.getData();
            profileImg.setImageURI(selectedImageUri);
        }
    }

    private void showEditDialog(String title, TextView targetTextView){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(targetTextView.getText().toString());
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String val = input.getText().toString().trim();
            if(!val.isEmpty()) targetTextView.setText(val);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveChanges() {
        String newUsername = txtProfileUsername.getText().toString().trim();
        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : "";

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String currentEmail = prefs.getString("email", "");

        editor.putString("username", newUsername);
        if(!imageUriString.isEmpty()) editor.putString("profile_image_uri", imageUriString);
        editor.apply();

        // Update DB (Ignoring removed fields like phone/gender)
        UserDB db = new UserDB(this);
        // Note: You might need to update updateUser in UserDB to remove phone/gender args,
        // or just pass empty strings here if you don't want to touch UserDB structure.
        if (!currentEmail.isEmpty()) {
            db.updateUser(currentEmail, newUsername, "", "", imageUriString);
        }
        Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
    }
}