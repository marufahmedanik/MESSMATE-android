package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Menu Click Listeners
        findViewById(R.id.btn_member_list).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, MemberListActivity.class));
        });

        findViewById(R.id.btn_meal_option).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, MealListActivity.class));
        });

        findViewById(R.id.btn_payment_history).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, PaymentHistoryActivity.class));
        });

        findViewById(R.id.btn_due_amount).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DueAmountActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_menu).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
