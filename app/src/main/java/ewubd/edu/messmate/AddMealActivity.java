package ewubd.edu.messmate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends AppCompatActivity {

    private EditText etName, etPrice;
    private Button btnSave, btnCancel;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        etName = findViewById(R.id.et_meal_name);
        etPrice = findViewById(R.id.et_meal_price);
        btnSave = findViewById(R.id.btn_save_meal);
        btnCancel = findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            String key = mDatabase.child("meals").push().getKey();
            if (key != null) {
                Map<String, Object> meal = new HashMap<>();
                meal.put("name", name);
                meal.put("price", price);

                mDatabase.child("meals").child(key).setValue(meal)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Meal added successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Return to meal list
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to add meal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
