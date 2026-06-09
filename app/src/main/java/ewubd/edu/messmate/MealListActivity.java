package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MealListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        com.google.android.material.floatingactionbutton.FloatingActionButton btnAddMeal = findViewById(
                R.id.btn_add_meal);
        android.widget.LinearLayout container = findViewById(R.id.ll_meal_container);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        boolean isOrder = getIntent().getBooleanExtra("IS_ORDER", false);

        if (isAdmin && !isOrder) {
            btnAddMeal.setVisibility(android.view.View.VISIBLE);
            btnAddMeal.setOnClickListener(v -> {
                startActivity(new Intent(MealListActivity.this, AddMealActivity.class));
            });
        }

        com.google.firebase.database.DatabaseReference mDatabase = com.google.firebase.database.FirebaseDatabase
                .getInstance().getReference();

        String node = isOrder ? "orders" : "meals";

        mDatabase.child(node).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                container.removeAllViews();
                if (snapshot.exists()) {
                    for (com.google.firebase.database.DataSnapshot document : snapshot.getChildren()) {
                        String name = "";
                        Double price = 0.0;
                        String subText = ""; // Date for orders

                        // Parse Data
                        if (isOrder) {
                            name = document.child("mealName").getValue(String.class);
                            price = document.child("price").getValue(Double.class);
                            subText = document.child("date").getValue(String.class);
                        } else {
                            name = document.child("name").getValue(String.class);
                            price = document.child("price").getValue(Double.class);
                        }

                        String id = document.getKey();

                        if (name == null)
                            name = "Unknown";
                        if (price == null)
                            price = 0.0;
                        if (subText == null)
                            subText = "";

                        android.view.View view;
                        if (isOrder) {
                            view = getLayoutInflater().inflate(R.layout.item_meal_order, container, false);
                            android.widget.TextView tvName = view.findViewById(R.id.tv_dish_name);
                            android.widget.TextView tvPrice = view.findViewById(R.id.tv_price);
                            android.widget.TextView tvTime = view.findViewById(R.id.tv_time);

                            tvName.setText(name);
                            tvPrice.setText(String.format("%.2f", price));
                            tvTime.setText(subText);

                            // Optional: Add click to delete order logic if needed, currently just View
                            // Adding simple delete for Admin on LongClick or Click?
                            // User requirement: "If the admin selects Meal Order, it will show the list..."
                            // Verification checklist said "Click to Delete Order". Let's add confirm
                            // delete.
                            if (isAdmin) {
                                String finalName = name; // Create final copy
                                view.setOnClickListener(v -> {
                                    new androidx.appcompat.app.AlertDialog.Builder(MealListActivity.this)
                                            .setTitle("Delete Order")
                                            .setMessage("Delete order for " + finalName + "?")
                                            .setPositiveButton("Yes", (dialog, which) -> {
                                                mDatabase.child("orders").child(id).removeValue()
                                                        .addOnSuccessListener(aVoid -> {
                                                            android.widget.Toast.makeText(MealListActivity.this,
                                                                    "Order Deleted", android.widget.Toast.LENGTH_SHORT)
                                                                    .show();
                                                            container.removeView(view);
                                                        });
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                });
                            }
                        } else {
                            view = getLayoutInflater().inflate(R.layout.item_meal, container, false);
                            android.widget.TextView tvName = view.findViewById(R.id.tv_dish_name);
                            android.widget.TextView tvPrice = view.findViewById(R.id.tv_price);

                            tvName.setText(name);
                            tvPrice.setText(String.format("%.2f", price));

                            String finalName = name;
                            Double finalPrice = price;

                            view.setOnClickListener(v -> {
                                try {
                                    Intent intent = new Intent(MealListActivity.this, ConfirmingActivity.class);
                                    intent.putExtra("IS_ADMIN", isAdmin);
                                    intent.putExtra("MEAL_ID", id);
                                    intent.putExtra("MEAL_NAME", finalName);
                                    intent.putExtra("MEAL_PRICE", finalPrice);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    android.widget.Toast
                                            .makeText(MealListActivity.this, "Error opening meal: " + e.getMessage(),
                                                    android.widget.Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }

                        container.addView(view);
                    }
                } else {
                    android.widget.Toast.makeText(MealListActivity.this, isOrder ? "No orders found" : "No meals found",
                            android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                android.widget.Toast.makeText(MealListActivity.this, "Failed to load data: " + error.getMessage(),
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
