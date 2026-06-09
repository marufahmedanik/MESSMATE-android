package ewubd.edu.messmate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Load User Data
        // Load User Data
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedName = prefs.getString("name", "User Name");
        String uid = prefs.getString("uid", ""); // Need UID to fetch fresh data

        android.widget.TextView tvName = findViewById(R.id.tv_profile_name);
        if (tvName != null) {
            tvName.setText(savedName); // Set initial value from prefs

            // Fetch fresh data if UID exists
            if (!uid.isEmpty()) {
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
                        .child("users").child(uid).child("name")
                        .get().addOnSuccessListener(snapshot -> {
                            String realName = snapshot.getValue(String.class);
                            if (realName != null && !realName.isEmpty()) {
                                tvName.setText(realName);
                                // Update prefs too
                                prefs.edit().putString("name", realName).apply();
                            }
                        });
            }
        }

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            startActivity(new android.content.Intent(ProfileActivity.this, LogoutActivity.class));
            // Do NOT finish() here, so that "No" in LogoutActivity returns to
            // ProfileActivity
        });
    }
}
