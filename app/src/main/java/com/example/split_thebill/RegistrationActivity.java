package com.example.split_thebill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextNewUsername;
    private EditText editTextNewPassword;
    private EditText editTextEmail;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> handleRegistration());
    }

    private void handleRegistration() {
        String newUsername = editTextNewUsername.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();
        String email = editTextEmail.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, newPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
}}
