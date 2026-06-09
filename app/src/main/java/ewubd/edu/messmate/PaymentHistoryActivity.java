package ewubd.edu.messmate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        com.google.firebase.database.DatabaseReference mDatabase = com.google.firebase.database.FirebaseDatabase
                .getInstance().getReference();
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        if (isAdmin) {
            setContentView(R.layout.activity_payment);
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());

            String userId = getIntent().getStringExtra("USER_ID");
            // IDs added to activity_payment.xml previously
            android.widget.EditText etAmount = findViewById(R.id.et_payment_amount);
            android.widget.EditText etDate = findViewById(R.id.et_payment_date);
            android.widget.EditText etNote = findViewById(R.id.et_payment_note);
            android.widget.Button btnUpdate = findViewById(R.id.btn_update_payment);

            btnUpdate.setOnClickListener(v -> {
                String amount = etAmount.getText().toString();
                String date = etDate.getText().toString();
                String note = etNote.getText().toString();

                if (amount.isEmpty() || date.isEmpty()) {
                    android.widget.Toast
                            .makeText(this, "Please fill required fields", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                java.util.Map<String, Object> payment = new java.util.HashMap<>();
                payment.put("uid", userId);
                try {
                    payment.put("amount", Double.parseDouble(amount));
                } catch (NumberFormatException e) {
                    android.widget.Toast.makeText(this, "Invalid Amount", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                payment.put("date", date);
                payment.put("note", note);
                payment.put("timestamp", System.currentTimeMillis());

                mDatabase.child("payments").push().setValue(payment)
                        .addOnSuccessListener(aVoid -> {
                            android.widget.Toast.makeText(this, "Payment Updated", android.widget.Toast.LENGTH_SHORT)
                                    .show();
                            // Return to Admin Dashboard
                            android.content.Intent intent = new android.content.Intent(PaymentHistoryActivity.this,
                                    AdminDashboardActivity.class);
                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            android.widget.Toast
                                    .makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT)
                                    .show();
                        });
            });

        } else {
            setContentView(R.layout.activity_payment_history);
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());

            android.widget.LinearLayout container = findViewById(R.id.ll_payment_container);
            String currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

            if (!currentUid.isEmpty()) {
                mDatabase.child("payments").orderByChild("uid").equalTo(currentUid)
                        .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                container.removeAllViews();
                                if (!snapshot.exists()) {
                                    android.widget.Toast.makeText(PaymentHistoryActivity.this, "No payments found",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                for (com.google.firebase.database.DataSnapshot doc : snapshot.getChildren()) {
                                    Double amount = doc.child("amount").getValue(Double.class);
                                    String date = doc.child("date").getValue(String.class);

                                    android.view.View view = getLayoutInflater().inflate(R.layout.item_payment,
                                            container, false);

                                    android.widget.TextView tvDate = view.findViewById(R.id.tv_payment_date);
                                    android.widget.TextView tvAmount = view.findViewById(R.id.tv_payment_amount);

                                    if (date != null)
                                        tvDate.setText(date);
                                    if (amount != null)
                                        tvAmount.setText(String.valueOf(amount));

                                    container.addView(view, 0); // Reverse order for recent first
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                                android.widget.Toast.makeText(PaymentHistoryActivity.this,
                                        "Error: " + error.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
