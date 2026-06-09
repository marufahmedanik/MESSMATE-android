// Imports assuming already there or will be added if using full file replacement, but here using snippets
// Need to ensure imports are available. The file has imports in lines 1-6.
// I will rewrite the whole file content to be safe with imports and logic.

package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirming);

        try {
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());

            boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
            String mealId = getIntent().getStringExtra("MEAL_ID");
            String mealName = getIntent().getStringExtra("MEAL_NAME");
            double mealPrice = getIntent().getDoubleExtra("MEAL_PRICE", 0.0);

            TextView tvSummary = findViewById(R.id.tv_order_summary);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String summaryText = "Meal: " + (mealName != null ? mealName : "Unknown") + "\n" +
                    "Price: " + String.format("%.2f", mealPrice) + "\n" +
                    "Date: " + currentDate;
            if (tvSummary != null)
                tvSummary.setText(summaryText);

            Button btnAction = findViewById(R.id.btn_confirm_order);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            if (btnAction == null) {
                Toast.makeText(this, "Error: Confirm Button not found", Toast.LENGTH_LONG).show();
                return;
            }

            if (isAdmin) {
                btnAction.setText("Delete Meal");
                btnAction.setBackgroundColor(android.graphics.Color.RED);
                btnAction.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Meal")
                            .setMessage("Are you sure you want to delete this meal?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (mealId != null) {
                                    mDatabase.child("meals").child(mealId).removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ConfirmingActivity.this, "Meal Deleted",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                Intent intent = new Intent(ConfirmingActivity.this,
                                                        AdminDashboardActivity.class);
                                                intent.addFlags(
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(ConfirmingActivity.this,
                                                    "Error deleting meal", Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(this, "Error: Meal ID missing", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                                Intent intent = new Intent(ConfirmingActivity.this,
                                        AdminDashboardActivity.class);
                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .show();
                });

                Button btnUpdate = findViewById(R.id.btn_update_meal);
                if (btnUpdate != null) {
                    btnUpdate.setVisibility(android.view.View.VISIBLE);

                    btnUpdate.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Update Meal Details");

                        final android.widget.EditText inputName = new android.widget.EditText(this);
                        inputName.setHint("Meal Name");
                        inputName.setText(mealName);

                        final android.widget.EditText inputPrice = new android.widget.EditText(this);
                        inputPrice.setHint("Price");
                        inputPrice.setInputType(
                                android.text.InputType.TYPE_CLASS_NUMBER
                                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        inputPrice.setText(String.valueOf(mealPrice));

                        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
                        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
                        layout.setPadding(32, 16, 32, 16);
                        layout.addView(inputName);
                        layout.addView(inputPrice);
                        builder.setView(layout);

                        builder.setPositiveButton("Update", (dialog, which) -> {
                            String newName = inputName.getText().toString();
                            String newPriceStr = inputPrice.getText().toString();

                            if (!newName.isEmpty() && !newPriceStr.isEmpty()) {
                                double newPrice = Double.parseDouble(newPriceStr);
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", newName);
                                updates.put("price", newPrice);

                                if (mealId != null) {
                                    mDatabase.child("meals").child(mealId).updateChildren(updates)
                                            .addOnSuccessListener(a -> {
                                                Toast.makeText(this, "Meal Updated", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ConfirmingActivity.this,
                                                        AdminDashboardActivity.class);
                                                intent.addFlags(
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            });
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder.show();
                    });
                } else {
                    Toast.makeText(this, "Error: Update Button not found in layout", Toast.LENGTH_LONG).show();
                }
            } else {
                // User Mode
                btnAction.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Order")
                            .setMessage("Do you want to confirm the order with Price " + mealPrice + "?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
                                if (uid.isEmpty()) {
                                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Map<String, Object> order = new HashMap<>();
                                order.put("uid", uid);
                                order.put("mealId", mealId);
                                order.put("mealName", mealName);
                                order.put("price", mealPrice);
                                order.put("date", currentDate);
                                order.put("status", "Pending");
                                order.put("timestamp", System.currentTimeMillis());

                                mDatabase.child("orders").push().setValue(order)
                                        .addOnSuccessListener(doc -> {
                                            // Add to Expenses as well
                                            Map<String, Object> expense = new HashMap<>();
                                            expense.put("uid", uid);
                                            expense.put("amount", mealPrice);
                                            expense.put("date", currentDate);
                                            expense.put("note", "Meal: " + mealName);
                                            expense.put("timestamp", System.currentTimeMillis());

                                            mDatabase.child("expenses").push().setValue(expense);

                                            Toast.makeText(ConfirmingActivity.this, "Order Confirmed!",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                            Intent intent = new Intent(ConfirmingActivity.this,
                                                    DashboardActivity.class);
                                            intent.addFlags(
                                                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ConfirmingActivity.this, "Failed to place order",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in ConfirmingActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
