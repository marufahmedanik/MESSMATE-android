package ewubd.edu.messmate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MemberListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        String mode = getIntent().getStringExtra("MODE"); // DELETE, PAYMENT, EXPENSE

        android.widget.LinearLayout container = findViewById(R.id.ll_member_container);
        // Initialize Realtime DB
        com.google.firebase.database.DatabaseReference mDatabase = com.google.firebase.database.FirebaseDatabase
                .getInstance().getReference();

        // Fetch Users
        mDatabase.child("users").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                container.removeAllViews();
                if (!snapshot.exists()) {
                    android.widget.Toast
                            .makeText(MemberListActivity.this, "No members found", android.widget.Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                for (com.google.firebase.database.DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Assuming user data is stored as a Map or User object. Safe way is map.
                    String name = userSnapshot.child("name").getValue(String.class);
                    String userId = userSnapshot.child("uid").getValue(String.class);

                    // Fallback using key if uid is missing in body
                    if (userId == null)
                        userId = userSnapshot.getKey();
                    if (name == null)
                        name = "Unknown Member";

                    android.view.View chunk = getLayoutInflater().inflate(R.layout.item_member, container, false);
                    android.widget.TextView tvName = chunk.findViewById(R.id.tv_member_name);
                    tvName.setText(name);

                    final String finalUserId = userId;
                    final String finalName = name;

                    // Handle Admin Modes
                    if (isAdmin) {
                        if ("DELETE".equals(mode)) {
                            // Show Delete Button
                            android.view.View btnDelete = chunk.findViewById(R.id.btn_delete);
                            btnDelete.setVisibility(android.view.View.VISIBLE);
                            btnDelete.setOnClickListener(v -> {
                                new androidx.appcompat.app.AlertDialog.Builder(MemberListActivity.this)
                                        .setTitle("Delete Member")
                                        .setMessage("Are you sure you want to delete " + finalName + "?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            // Delete from Realtime DB
                                            mDatabase.child("users").child(finalUserId).removeValue()
                                                    .addOnSuccessListener(aVoid -> {
                                                        android.widget.Toast.makeText(MemberListActivity.this,
                                                                "User deleted", android.widget.Toast.LENGTH_SHORT)
                                                                .show();
                                                        container.removeView(chunk);
                                                        // Return to Admin Dashboard
                                                        android.content.Intent intent = new android.content.Intent(
                                                                MemberListActivity.this, AdminDashboardActivity.class);
                                                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    });
                                        })
                                        .setNegativeButton("No", (dialog, which) -> {
                                            dialog.dismiss();
                                            // Return to Admin Dashboard
                                            android.content.Intent intent = new android.content.Intent(
                                                    MemberListActivity.this, AdminDashboardActivity.class);
                                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .show();
                            });
                        } else if ("PAYMENT".equals(mode)) {
                            // Select Member -> Go to Payment Page
                            chunk.setOnClickListener(v -> {
                                android.content.Intent intent = new android.content.Intent(MemberListActivity.this,
                                        PaymentHistoryActivity.class);
                                intent.putExtra("IS_ADMIN", true);
                                intent.putExtra("USER_ID", finalUserId);
                                intent.putExtra("USER_NAME", finalName);
                                startActivity(intent);
                            });
                        } else if ("EXPENSE".equals(mode)) {
                            // Select Member -> Go to Expense Page
                            chunk.setOnClickListener(v -> {
                                android.content.Intent intent = new android.content.Intent(MemberListActivity.this,
                                        DueAmountActivity.class);
                                intent.putExtra("IS_ADMIN", true);
                                intent.putExtra("USER_ID", finalUserId);
                                intent.putExtra("USER_NAME", finalName);
                                startActivity(intent);
                            });
                        }
                    }

                    container.addView(chunk);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                android.widget.Toast.makeText(MemberListActivity.this, "Error: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
