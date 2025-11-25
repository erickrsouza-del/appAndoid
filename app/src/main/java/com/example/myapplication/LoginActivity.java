package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private TextView forgotPasswordTextView;
    private ProgressBar loadingProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        forgotPasswordTextView = findViewById(R.id.forgot_password);
        loadingProgressBar = findViewById(R.id.loading_progressbar);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            if (validateForm()) {
                loginUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        // Updated to open RegisterActivity
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        forgotPasswordTextView.setOnClickListener(v -> showForgotPasswordDialog());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            emailEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
            forgotPasswordTextView.setEnabled(false);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            emailEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
            forgotPasswordTextView.setEnabled(true);
        }
    }

    private void showForgotPasswordDialog() {
        // ... (code remains the same)
    }

    private void sendPasswordResetEmail(String email) {
        // ... (code remains the same)
    }

    private boolean validateForm() {
        boolean valid = true;
        String required = getString(R.string.required_field);

        if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            emailEditText.setError(required);
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
            passwordEditText.setError(required);
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void loginUser(String email, String password) {
        showLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed, task.getException().getMessage()), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // The registerUser method has been moved to RegisterActivity
}
