package com.example.split_thebill;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDouble;
    public static final int CONTACT_SELECTION_REQUEST = 1001;

    private FirebaseAuth mAuth;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (!isLoggedIn()) {
            launchLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        editTextDouble = findViewById(R.id.editTextDouble);

        findViewById(R.id.yourButtonId).setOnClickListener(view -> handleAmountEntry());

        ImageButton btnUserProfile = findViewById(R.id.btnUserProfile);
        btnUserProfile.setOnClickListener(view -> showUserProfile());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
        }
        saveLoginStatus(false);
    }

    private void handleAmountEntry() {
        String userInput = editTextDouble.getText().toString();

        if (!userInput.isEmpty()) {
            try {
                double totalBill = Double.parseDouble(userInput);

                Intent intent = new Intent(this, ContactSelectionActivity.class);
                intent.putExtra("totalBill", totalBill);
                startActivityForResult(intent, CONTACT_SELECTION_REQUEST);

            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    private void showUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            showUserInfoDialog(displayName, email);
        } else {
            Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserInfoDialog(String displayName, String email) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Profile");
        builder.setMessage("Email: " + email);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
