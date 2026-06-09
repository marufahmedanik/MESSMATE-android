package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private android.widget.EditText etName, etEmail, etUsername, etPassword, etConfirmPassword;
    private android.widget.Button btnSignup, btnNavLogin;
    private com.google.firebase.auth.FirebaseAuth mAuth;
    // Replace Firestore with Realtime Database
    private com.google.firebase.database.DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        // Initialize Realtime Database
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        btnNavLogin = findViewById(R.id.btn_nav_login);

        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                android.widget.Toast
                        .makeText(SignupActivity.this, "Please fill all fields", android.widget.Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                android.widget.Toast
                        .makeText(SignupActivity.this, "Passwords do not match", android.widget.Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            java.util.Map<String, Object> user = new java.util.HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("uid", userId);

                            // Save to Realtime Database: users/$uid
                            mDatabase.child("users").child(userId).setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        android.widget.Toast.makeText(SignupActivity.this, "Registration Successful",
                                                android.widget.Toast.LENGTH_SHORT).show();
                                        // Requirement: "Clicking Continue returns to the login page."
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        // Clear stack to prevent back button from returning to signup
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        android.widget.Toast.makeText(SignupActivity.this,
                                                "Error saving user data: " + e.getMessage(),
                                                android.widget.Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            android.widget.Toast.makeText(SignupActivity.this,
                                    "Authentication Failed: " + task.getException().getMessage(),
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Navigation to Login
        btnNavLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
