package com.example.focusflowperson2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText signup_username, signup_email, signup_password;
    Button signup_btnSignup, signup_btnAlreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //connect to user database
        UserDB db = new UserDB(this);

        signup_username = findViewById(R.id.signup_username);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);

        signup_btnSignup = findViewById(R.id.signup_btnSignup);
        signup_btnAlreadyAccount = findViewById(R.id.signup_btnAlreadyAccount);

        //Click the Signup button
        signup_btnSignup.setOnClickListener(v -> {
            //save the username, email and password
            String username = signup_username.getText().toString().trim();
            String email = signup_email.getText().toString().trim();
            String password = signup_password.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this,
                        "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            //if cannot match the email pattern
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignupActivity.this,
                        "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            //if signup successfully, save the user data into the database
            boolean success = db.insertUser(username, email, password);

            if (success) {
                Toast.makeText(SignupActivity.this,
                        "Account created successfully!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(SignupActivity.this,
                        "Username or Email already exists.", Toast.LENGTH_SHORT).show();
            }
        });

        //navigate to log in page
        signup_btnAlreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}