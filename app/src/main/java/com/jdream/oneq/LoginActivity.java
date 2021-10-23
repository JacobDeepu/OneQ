package com.jdream.oneq;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jdream.oneq.business.MainBusinessActivity;
import com.jdream.oneq.user.MainUserActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivTop;
    private ImageView ivBottom;
    private String email;
    private String password;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Find screen height and width
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        // Initialize views
        initViews();

        // Set Image view dimension
        ivTop.getLayoutParams().width = (int) (width * 0.35);
        ivTop.getLayoutParams().height = (int) (height * 0.25);

        ivBottom.getLayoutParams().width = (int) (width * 0.4);
        ivBottom.getLayoutParams().height = (int) (height * 0.15);

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

        // Register new user
        btnRegister.setOnClickListener(v -> {
            Intent registrationActivityIntent = new Intent(LoginActivity.this,
                    RegistrationActivity.class);
            startActivity(registrationActivityIntent);
            LoginActivity.this.finish();
        });
    }

    private void initViews() {
        btnLogin = findViewById(R.id.button_login);
        btnRegister = findViewById(R.id.button_register);
        ivTop = findViewById(R.id.image_view_top);
        ivBottom = findViewById(R.id.image_view_bottom);
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
        Intent MainActivity;
        if (isUser) {
            MainActivity = new Intent(LoginActivity.this, MainUserActivity.class);
        }
        else {
            MainActivity = new Intent(LoginActivity.this, MainBusinessActivity.class);
        }

        startActivity(MainActivity);
        LoginActivity.this.finish();
    }
}