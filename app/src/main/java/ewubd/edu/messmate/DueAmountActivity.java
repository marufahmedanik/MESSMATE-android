package ewubd.edu.messmate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DueAmountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        com.google.firebase.database.DatabaseReference mDatabase = com.google.firebase.database.FirebaseDatabase
                .getInstance().getReference();
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        if (isAdmin) {
            setContentView(R.layout.activity_expenses);
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());

            String userId = getIntent().getStringExtra("USER_ID");
            // IDs added to activity_expenses.xml previously
            android.widget.EditText etAmount = findViewById(R.id.et_expense_amount);
            android.widget.EditText etDate = findViewById(R.id.et_expense_date);
            android.widget.EditText etNote = findViewById(R.id.et_expense_note);
            android.widget.Button btnUpdate = findViewById(R.id.btn_update_expense);

            btnUpdate.setOnClickListener(v -> {
                String amount = etAmount.getText().toString();
                String date = etDate.getText().toString();
                String note = etNote.getText().toString();

                if (amount.isEmpty() || date.isEmpty()) {
                    android.widget.Toast
                            .makeText(this, "Please fill required fields", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                java.util.Map<String, Object> expense = new java.util.HashMap<>();
                expense.put("uid", userId);
                try {
                    expense.put("amount", Double.parseDouble(amount));
                } catch (NumberFormatException e) {
                    android.widget.Toast.makeText(this, "Invalid Amount", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                expense.put("date", date);
                expense.put("note", note); // Using note as title
                expense.put("timestamp", System.currentTimeMillis());

                mDatabase.child("expenses").push().setValue(expense)
                        .addOnSuccessListener(aVoid -> {
                            android.widget.Toast.makeText(this, "Expense Added", android.widget.Toast.LENGTH_SHORT)
                                    .show();
                            // Return to Admin Dashboard
                            android.content.Intent intent = new android.content.Intent(DueAmountActivity.this,
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
            setContentView(R.layout.activity_due_amount);
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());

            android.widget.TextView tvTotalDue = findViewById(R.id.tv_total_due_amount);
            android.widget.TextView tvTotalPayment = findViewById(R.id.tv_total_payment_amount);
            android.widget.TextView tvTotalExpenses = findViewById(R.id.tv_total_expenses_amount); // Bound new TextView
            android.widget.LinearLayout container = findViewById(R.id.ll_due_container);
            String currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

            if (!currentUid.isEmpty()) {
                // Fetch Expenses first
                mDatabase.child("expenses").orderByChild("uid").equalTo(currentUid)
                        .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                double localTotalExpenses = 0.0;
                                container.removeAllViews();
                                if (snapshot.exists()) {
                                    for (com.google.firebase.database.DataSnapshot doc : snapshot.getChildren()) {
                                        Double amount = doc.child("amount").getValue(Double.class);
                                        String title = doc.child("note").getValue(String.class); // Using note as 'Cost
                                                                                                 // Title'

                                        if (amount == null)
                                            amount = 0.0;
                                        if (title == null || title.isEmpty())
                                            title = "Expense";

                                        localTotalExpenses += amount;

                                        android.view.View view = getLayoutInflater().inflate(R.layout.item_due,
                                                container, false);
                                        android.widget.TextView tvTitle = view.findViewById(R.id.tv_due_title);
                                        android.widget.TextView tvAmount = view.findViewById(R.id.tv_due_amount);
                                        tvTitle.setText(title);
                                        tvAmount.setText(String.valueOf(amount));

                                        // Add to top (0 index) to show most recent first
                                        container.addView(view, 0);
                                    }
                                }

                                final double finalTotalExpenses = localTotalExpenses;
                                if (tvTotalExpenses != null)
                                    tvTotalExpenses.setText(String.format("%.2f", finalTotalExpenses));

                                // Nested call to fetch Payments to calculate Total Due
                                mDatabase.child("payments").orderByChild("uid").equalTo(currentUid)
                                        .addListenerForSingleValueEvent(
                                                new com.google.firebase.database.ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(
                                                            @androidx.annotation.NonNull com.google.firebase.database.DataSnapshot paymentSnapshot) {
                                                        double totalPayments = 0.0;
                                                        if (paymentSnapshot.exists()) {
                                                            for (com.google.firebase.database.DataSnapshot doc : paymentSnapshot
                                                                    .getChildren()) {
                                                                Double pAmount = doc.child("amount")
                                                                        .getValue(Double.class);
                                                                if (pAmount != null)
                                                                    totalPayments += pAmount;
                                                            }
                                                        }

                                                        double totalDue = finalTotalExpenses - totalPayments;
                                                        tvTotalDue.setText(String.format("%.2f", totalDue));
                                                        if (tvTotalPayment != null)
                                                            tvTotalPayment
                                                                    .setText(String.format("%.2f", totalPayments));
                                                    }

                                                    @Override
                                                    public void onCancelled(
                                                            @androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                                                        android.widget.Toast.makeText(DueAmountActivity.this,
                                                                "Error fetching payments",
                                                                android.widget.Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                            }

                            @Override
                            public void onCancelled(
                                    @androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                                android.widget.Toast.makeText(DueAmountActivity.this, "Error fetching expenses",
                                        android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
