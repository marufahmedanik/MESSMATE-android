package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private android.widget.EditText etUsername; // Note: Current layout uses 'Username' hint but typically Firebase uses
                                                // Email
    private android.widget.EditText etPassword;
    private android.widget.Button btnLogin, btnNavSignup, btnAdminLogin;
    private android.widget.TextView tvSignupLink;
    private com.google.firebase.auth.FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        // Note: Layout IDs might need adjustment based on reading.
        // Assuming et_username refers to an input that will take Email for Firebase (or
        // we map username to email)
        // For simplicity now, we'll treat the input as email if possible, or user
        // handles it.
        // Let's re-read layout to be sure. Activity_login.xml has et_username (hint:
        // Username) and et_email is not there?
        // Wait, Login usually takes Email/Pass for Firebase.
        // Let's grab views first.
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnNavSignup = findViewById(R.id.btn_nav_signup);
        tvSignupLink = findViewById(R.id.tv_signup_link);
        btnAdminLogin = findViewById(R.id.btn_admin_login);

        btnLogin.setOnClickListener(v -> {
            String email = etUsername.getText().toString().trim(); // Using username field as email for now
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(LoginActivity.this, "Please enter email and password",
                        android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            // Fetch user details from Realtime DB to save in session
                            com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(uid)
                                    .get().addOnSuccessListener(snapshot -> {
                                        String name = snapshot.child("name").getValue(String.class);
                                        String dbEmail = snapshot.child("email").getValue(String.class);

                                        if (name == null)
                                            name = "User";
                                        if (dbEmail == null)
                                            dbEmail = "";

                                        // Save to SharedPreferences
                                        android.content.SharedPreferences prefs = getSharedPreferences("UserSession",
                                                MODE_PRIVATE);
                                        android.content.SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("uid", uid);
                                        editor.putString("name", name);
                                        editor.putString("email", dbEmail);
                                        editor.apply();

                                        android.widget.Toast.makeText(LoginActivity.this, "Login Successful",
                                                android.widget.Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Fallback if DB fetch fails (still login, but might miss name)
                                        android.widget.Toast.makeText(LoginActivity.this,
                                                "Login Successful (Offline Mode)", android.widget.Toast.LENGTH_SHORT)
                                                .show();
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });

                        } else {
                            android.widget.Toast
                                    .makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(),
                                            android.widget.Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        });

        // Navigation to Sign Up
        btnNavSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        tvSignupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Admin Login Navigation
        btnAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}
