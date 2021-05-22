package com.jdream.oneq;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jdream.oneq.user.MainUserActivity;

import java.util.Objects;

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
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        intViews();

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null)
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null) {
            checkUser(mCurrentUser);
        }

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
                                mCurrentUser = mAuth.getCurrentUser();
                                checkUser(mCurrentUser);
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

    public void checkUser(FirebaseUser currentUser) {
        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check userId exists in users db
        db.collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    boolean isUser = false;
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            if (document.getId().equals(userId)) {
                                isUser = true;
                                break;
                            }
                        }
                        // Update UI based on user
                        updateUI(isUser);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void updateUI(boolean isUser) {
        if (isUser) {
            Intent MainUserActivity = new Intent(LoginActivity.this, MainUserActivity.class);
            startActivity(MainUserActivity);
            LoginActivity.this.finish();
        }
    }
}