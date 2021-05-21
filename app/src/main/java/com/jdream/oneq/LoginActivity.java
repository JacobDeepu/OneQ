package com.jdream.oneq;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnRegister;
    private Button btnGuestLogin;
    private EditText etEmail;
    private EditText etPassword;
    private String email;
    private String password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        intViews();

        mAuth = FirebaseAuth.getInstance();

        // Login with email and password
        btnLogin.setOnClickListener(v -> {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();

            if (validateInputData()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign in success
                                Log.d(TAG, "signInWithEmail: success");
                            } else {
                                // Sign in error
                                Log.w(TAG, "signInWithEmail: failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnGuestLogin.setOnClickListener(v -> {
            // TODO: Implement method for guest login
        });

        // Register new user
        btnRegister.setOnClickListener(v -> {
            Intent registrationActivityIntent = new Intent(LoginActivity.this,
                    RegistrationActivity.class);
            startActivity(registrationActivityIntent);
        });
    }

    private void intViews() {
        btnLogin = findViewById(R.id.button_login);
        btnRegister = findViewById(R.id.button_register);
        btnGuestLogin = findViewById(R.id.button_guest_login);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
    }

    private boolean validateInputData() {
        if (email.trim().length() == 0) {
            etEmail.setError("Invalid Email");
            return false;
        }

        if (password.trim().length() == 0) {
            etPassword.setError("Invalid Password");
            return false;
        }

        return true;
    }

}