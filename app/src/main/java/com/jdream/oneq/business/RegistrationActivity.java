package com.jdream.oneq.business;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jdream.oneq.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private FloatingActionButton btnSelectImage;
    private Button btnRegister;
    private TextView tvSelectImage;
    private EditText etCompanyName;
    private EditText etAddress;
    private EditText etPinCode;
    private EditText etPhone;
    private EditText etDescription;
    private ProgressBar progressBar;
    private AutoCompleteTextView acCategoryItems;
    private AutoCompleteTextView acTypeItems;
    private String companyName;
    private String address;
    private String description;
    private String phoneString;
    private String pinCodeString;
    private String category;
    private String type;
    private long phone;
    private int pinCode;
    private String imageUrl;
    private FirebaseUser mCurrentUser;

    // ActivityResultLauncher for getting image uri
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) uploadImage(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_business);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        initViews();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_dropdown_item);
        acCategoryItems.setAdapter(adapter);

        acCategoryItems.setOnItemClickListener((parent, view, position, id) -> {
            category = parent.getItemAtPosition(position).toString();
            acCategoryItems.setError(null);
            setDropDownListItems(category);
        });

        acTypeItems.setOnItemClickListener((parent, view, position, id) -> {
            type = parent.getItemAtPosition(position).toString();
            acTypeItems.setError(null);
        });

        // Select image to upload
        btnSelectImage.setOnClickListener(v -> {
            mGetContent.launch("image/*");
            tvSelectImage.setError(null);
        });

        btnRegister.setOnClickListener(v -> {
            getInputData();

            if (validateInputData()) writeDataToDb();
        });
    }

    private void initViews() {
        btnSelectImage = findViewById(R.id.button_select_image);
        btnRegister = findViewById(R.id.button_register);
        tvSelectImage = findViewById(R.id.text_select_image);
        etCompanyName = findViewById(R.id.edit_text_company_name);
        etAddress = findViewById(R.id.edit_text_address);
        etPinCode = findViewById(R.id.edit_text_pin_code);
        etPhone = findViewById(R.id.edit_text_phone);
        etDescription = findViewById(R.id.edit_text_description);
        progressBar = findViewById(R.id.progress_bar);
        acCategoryItems = findViewById(R.id.category_items);
        acTypeItems = findViewById(R.id.type_items);
    }

    private void setDropDownListItems(String category) {
        switch (category) {
            case "Hotel": {
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                        RegistrationActivity.this, R.array.type_hotel, android.R.layout.simple_spinner_dropdown_item);
                acTypeItems.setAdapter(adapter1);
                break;
            }
            case "Office": {
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                        RegistrationActivity.this, R.array.type_office, android.R.layout.simple_spinner_dropdown_item);
                acTypeItems.setAdapter(adapter1);
                break;
            }
            case "Shop": {
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                        RegistrationActivity.this, R.array.type_shop, android.R.layout.simple_spinner_dropdown_item);
                acTypeItems.setAdapter(adapter1);
                break;
            }
            case "Bank": {
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                        RegistrationActivity.this, R.array.type_bank, android.R.layout.simple_spinner_dropdown_item);
                acTypeItems.setAdapter(adapter1);
                break;
            }
            case "Hospital": {
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                        RegistrationActivity.this, R.array.type_hospital, android.R.layout.simple_spinner_dropdown_item);
                acTypeItems.setAdapter(adapter1);
                break;
            }
            case "Other": {
                // TODO Implement methods for type other
                break;
            }
            default:
                break;
        }
    }

    private void uploadImage(Uri filePath) {
        String userId = mCurrentUser.getUid();
        StorageReference mStorageReference = FirebaseStorage.getInstance()
                .getReference().child("images/" + userId);

        progressBar.setVisibility(View.VISIBLE);

        mStorageReference.putFile(filePath)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageReference.getDownloadUrl()
                                .addOnCompleteListener(task -> {
                                    tvSelectImage.setText(R.string.upload_image_success);
                                    imageUrl = Objects.requireNonNull(task.getResult()).toString();
                                    progressBar.setVisibility(View.GONE);
                                    Log.d(TAG, "onComplete: Upload Success");
                                }))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this,
                            "Failed to Upload Image", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: Upload Failure");
                });
    }

    private void getInputData() {
        companyName = etCompanyName.getText().toString();
        address = etAddress.getText().toString();

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

        description = etDescription.getText().toString();

    }

    private boolean validateInputData() {
        if (companyName.trim().length() == 0) {
            etCompanyName.setError("Name required");
            return false;
        }

        if (address.trim().length() == 0) {
            etAddress.setError("Address required");
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

        if (description.trim().length() == 0) {
            etDescription.setError("Description required");
            return false;
        }

        if (category == null) {
            acCategoryItems.setError("Category required");
            return false;
        }

        if (type == null) {
            acTypeItems.setError("Type required");
            return false;
        }

        if (imageUrl == null) {
            tvSelectImage.setError("Upload Image");
            return false;
        }

        return true;
    }

    private void writeDataToDb() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mCurrentUser.getUid();

        Map<String, Object> inputData = new HashMap<>();

        inputData.put("Name", companyName);
        inputData.put("Address", address);
        inputData.put("Phone", phone);
        inputData.put("PinCode", pinCode);
        inputData.put("Description", description);
        inputData.put("Category", category);
        inputData.put("Type", type);
        inputData.put("ImageUrl", imageUrl);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(companyName)
                .build();

        progressBar.setVisibility(View.VISIBLE);

        db.collection("Business").document(category)
                .collection(type).document(userId)
                .set(inputData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mCurrentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "Business profile updated.");
                            }
                        });
                progressBar.setVisibility(View.GONE);
                updateUI();
                Log.d(TAG, "Registration: success");
            } else {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Registration: failure");
                Toast.makeText(RegistrationActivity.this,
                        "Failed to Register", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        Intent MainActivity = new Intent(RegistrationActivity.this, MainBusinessActivity.class);
        startActivity(MainActivity);
        RegistrationActivity.this.finish();
    }
}