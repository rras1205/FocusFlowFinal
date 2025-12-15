package com.example.focusflowperson2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ApiException;

public class LoginActivity extends AppCompatActivity {

    EditText login_username, login_password;
    Button login_btnLogin, login_btnNeedAccount, login_btnGoogle;

    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //connect to user database
        UserDB db = new UserDB(this);

        login_username = findViewById(R.id.login_username);
        login_password = findViewById(R.id.login_password);

        login_btnLogin = findViewById(R.id.login_btnLogin);
        login_btnNeedAccount = findViewById(R.id.login_btnNeedAccount);
        login_btnGoogle = findViewById(R.id.login_btnGoogle);

        // Click the Login button
        login_btnLogin.setOnClickListener(v -> {
            String username = login_username.getText().toString().trim();
            String password = login_password.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Please enter username and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //to check the username and password to match or not
            boolean valid = db.checkUser(username, password);

            if (valid) {
                Toast.makeText(LoginActivity.this,
                        "Login successful",
                        Toast.LENGTH_SHORT).show();

                // save username and email into SharedPreferences for the profile page to display the username and email
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                String email = db.getEmailByUsername(username);
                editor.putString("username", username);
                editor.putString("email", email);
                editor.apply();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));


            } else {
                Toast.makeText(LoginActivity.this,
                        "Incorrect username or password",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // navigate to Signup page
        login_btnNeedAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // google login set up
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // click the continue with Google btn
        Button btnGoogle = findViewById(R.id.login_btnGoogle);
        btnGoogle.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    // get username and email
                    String email = account.getEmail();
                    String username = email != null ? email.split("@")[0] : "User";

                    //  save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.apply();

                    // navigate to homepage
                    Toast.makeText(this, "Google Sign-In Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}