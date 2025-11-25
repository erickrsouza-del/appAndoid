package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
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

    private EditText phoneNumberEditText;
    private EditText verificationCodeEditText;
    private Button sendCodeButton, verifyButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView userEmailTextView = findViewById(R.id.user_email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        verificationCodeEditText = findViewById(R.id.verification_code);
        sendCodeButton = findViewById(R.id.send_code_button);
        verifyButton = findViewById(R.id.verify_button);

        if (currentUser != null) {
            userEmailTextView.setText(getString(R.string.user_email_label, currentUser.getEmail()));
        }

        sendCodeButton.setOnClickListener(v -> sendVerificationCode());
        verifyButton.setOnClickListener(v -> verifyPhoneNumberWithCode());
    }

    private void sendVerificationCode() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.startsWith("+")) {
            phoneNumberEditText.setError(getString(R.string.phone_format_error));
            return;
        }

        String sanitizedPhoneNumber = phoneNumber.replaceAll("[\\s\\(\\)\\-]", "");

        sendCodeButton.setEnabled(false);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(sanitizedPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        Toast.makeText(this, R.string.sending_code_toast, Toast.LENGTH_SHORT).show();
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            linkPhoneWithUser(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ProfileActivity.this, getString(R.string.phone_verification_failed, e.getMessage()), Toast.LENGTH_LONG).show();
            sendCodeButton.setEnabled(true);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            mVerificationId = verificationId;
            verificationCodeEditText.setVisibility(View.VISIBLE);
            verifyButton.setVisibility(View.VISIBLE);
            Toast.makeText(ProfileActivity.this, R.string.code_sent_toast, Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyPhoneNumberWithCode() {
        String code = verificationCodeEditText.getText().toString();
        if (TextUtils.isEmpty(code) || code.length() < 6) {
            verificationCodeEditText.setError(getString(R.string.required_field));
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        linkPhoneWithUser(credential);
    }

    private void linkPhoneWithUser(PhoneAuthCredential credential) {
        if (currentUser == null) return;
        currentUser.updatePhoneNumber(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, R.string.phone_linked_toast, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.phone_link_failed, task.getException().getMessage()), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
