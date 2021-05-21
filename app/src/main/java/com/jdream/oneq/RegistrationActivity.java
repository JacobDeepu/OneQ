package com.jdream.oneq;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private Button btnContinue;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etRePassword;
    private RadioGroup rgSelectedType;
    private RadioButton rbSelected;
    private String email;
    private String password;
    private String rePassword;
    private int selectedRbId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        intViews();

        mAuth = FirebaseAuth.getInstance();

        btnContinue.setOnClickListener(v -> {

            getInputData();

            if (validateInputData()) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign up success
                                Log.d(TAG, "signUpWithEmail:success");
                                rbSelected = findViewById(selectedRbId);
                                String selectedButtonString = rbSelected.getText().toString();
                                Intent RegistrationActivity;

                                // Start Registration Activity based on selected type
                                if (selectedButtonString.equals("User")) {
                                    RegistrationActivity = new Intent(RegistrationActivity.this,
                                            com.jdream.oneq.user.RegistrationActivity.class);
                                } else {
                                    RegistrationActivity = new Intent(RegistrationActivity.this,
                                            com.jdream.oneq.business.RegistrationActivity.class);
                                }
                                startActivity(RegistrationActivity);
                                RegistrationActivity.this.finish();

                            } else {
                                // Sign up error
                                Log.w(TAG, "signUpWithEmail:failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Registration Failed !!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void intViews() {
        btnContinue = findViewById(R.id.button_continue);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        etRePassword = findViewById(R.id.edit_text_re_password);
        rgSelectedType = findViewById(R.id.radio_group_type);
    }

    private void getInputData() {
        selectedRbId = rgSelectedType.getCheckedRadioButtonId();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        rePassword = etRePassword.getText().toString();

    }

    public boolean validateInputData() {
        if (email.trim().length() == 0) {
            etEmail.setError("Email-ID required");
            return false;
        }

        if (password.trim().length() == 0) {
            etPassword.setError("Password required");
            return false;
        } else if (password.trim().length() < 6) {
            etPassword.setError("Minimum 6 characters");
            return false;
        }

        if (!password.equals(rePassword)) {
            etRePassword.setError("Password not matching");
            return false;
        }

        if (selectedRbId == -1) {
            Toast.makeText(RegistrationActivity.this,
                    "Please select type", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}