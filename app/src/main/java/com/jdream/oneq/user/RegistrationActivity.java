package com.jdream.oneq.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jdream.oneq.R;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = com.jdream.oneq.user.RegistrationActivity.class.getSimpleName();
    private Button btnRegister;
    private EditText etName;
    private EditText etPinCode;
    private EditText etPhone;
    private ProgressBar progressBar;
    private String name;
    private String phoneString;
    private String pinCodeString;
    private long phone;
    private int pinCode;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_user);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        initViews();

        btnRegister.setOnClickListener(v -> {
            getInputData();

            if (validateInputData()) writeDataToDb();
        });
    }

    private void initViews() {
        btnRegister = findViewById(R.id.button_register);
        etName = findViewById(R.id.edit_text_name);
        etPhone = findViewById(R.id.edit_text_phone);
        etPinCode = findViewById(R.id.edit_text_pin_code);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void getInputData() {
        name = etName.getText().toString();

        phone = 0;
        phoneString = etPhone.getText().toString();
        if (phoneString.trim().length() != 0) {
            phone = Long.parseLong(phoneString);
        }

        pinCode = 0;
        pinCodeString = etPinCode.getText().toString();
        if (pinCodeString.trim().length() != 0) {
            pinCode = Integer.parseInt(pinCodeString);
        }

    }

    private boolean validateInputData() {
        if (name.trim().length() == 0) {
            etName.setError("Name required");
            return false;
        }

        if (phoneString.trim().length() == 0) {
            etPhone.setError("Phone Number required");
            return false;
        } else if (phoneString.trim().length() != 10) {
            etPhone.setError("Invalid Phone Number");
            return false;
        }

        if (pinCodeString.trim().length() == 0) {
            etPinCode.setError("Pin Code required");
            return false;
        } else if (pinCodeString.trim().length() != 6) {
            etPinCode.setError("Invalid Pin Code");
            return false;
        }

        return true;
    }

    private void writeDataToDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mCurrentUser.getUid();

        Map<String, Object> inputData = new HashMap<>();

        inputData.put("Name", name);
        inputData.put("Phone", phone);
        inputData.put("PinCode", pinCode);

        progressBar.setVisibility(View.VISIBLE);

        db.collection("User")
                .document(userId)
                .set(inputData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);
                updateUI();
                Log.d(TAG, "Registration: success");
            } else {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Registration: failure");
                Toast.makeText(com.jdream.oneq.user.RegistrationActivity.this,
                        "Failed to Register", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        Intent MainActivity = new Intent(RegistrationActivity.this, MainUserActivity.class);
        startActivity(MainActivity);
        RegistrationActivity.this.finish();
    }
}