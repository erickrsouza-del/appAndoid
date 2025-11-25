package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends BaseActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button createAccountButton;
    private ProgressBar loadingProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        confirmPasswordEditText = findViewById(R.id.register_confirm_password);
        createAccountButton = findViewById(R.id.button_create_account);
        loadingProgressBar = findViewById(R.id.register_loading_progressbar);

        createAccountButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateForm(email, password, confirmPassword)) {
            return;
        }

        showLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, R.string.reg_successful, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finishAffinity(); // Clear all previous activities
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.reg_failed, task.getException().getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateForm(String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("E-mail é obrigatório.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Senha é obrigatória.");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("A senha deve ter pelo menos 6 caracteres.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("As senhas não coincidem.");
            return false;
        }
        return true;
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            emailEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            confirmPasswordEditText.setEnabled(false);
            createAccountButton.setEnabled(false);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            emailEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            confirmPasswordEditText.setEnabled(true);
            createAccountButton.setEnabled(true);
        }
    }
}
