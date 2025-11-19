package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";

    private TextView userEmailTextView;
    private EditText phoneNumberEditText;
    private Button sendCodeButton;
    private EditText verificationCodeEditText;
    private Button verifyButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userEmailTextView = findViewById(R.id.user_email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        sendCodeButton = findViewById(R.id.send_code_button);
        verificationCodeEditText = findViewById(R.id.verification_code);
        verifyButton = findViewById(R.id.verify_button);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userEmailTextView.setText("Email: " + currentUser.getEmail());
        }

        sendCodeButton.setOnClickListener(v -> sendVerificationCode());
        verifyButton.setOnClickListener(v -> verifyPhoneNumberWithCode());
    }

    private void sendVerificationCode() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (phoneNumber.isEmpty() || !phoneNumber.startsWith("+")) {
            phoneNumberEditText.setError("Number must be in E.164 format, e.g. +5511999999999");
            return;
        }

        sendCodeButton.setEnabled(false);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        Toast.makeText(this, "Sending verification code...", Toast.LENGTH_SHORT).show();
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    linkPhoneWithUser(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.w(TAG, "onVerificationFailed", e);
                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error.";
                    Toast.makeText(ProfileActivity.this, "Verification failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    sendCodeButton.setEnabled(true);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    Log.d(TAG, "onCodeSent:" + verificationId);
                    mVerificationId = verificationId;

                    verificationCodeEditText.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "Code sent.", Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyPhoneNumberWithCode() {
        String code = verificationCodeEditText.getText().toString();
        if (code.isEmpty() || code.length() < 6) {
            verificationCodeEditText.setError("Required.");
            return;
        }
        if (mVerificationId == null || mVerificationId.isEmpty()) {
            Toast.makeText(this, "Please request a new code first.", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        linkPhoneWithUser(credential);
    }

    private void linkPhoneWithUser(PhoneAuthCredential credential) {
        if (currentUser == null) {
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
            return;
        }
        currentUser.updatePhoneNumber(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User phone number updated.");
                        Toast.makeText(ProfileActivity.this, "Phone number verified!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.w(TAG, "Unable to link phone number.", task.getException());
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(ProfileActivity.this, "Linking failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        sendCodeButton.setEnabled(true);
                    }
                });
    }
}