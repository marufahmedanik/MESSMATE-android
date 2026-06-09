package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Menu Button
        findViewById(R.id.btn_menu).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // 1. Delete Member -> Member List (Mode: DELETE)
        findViewById(R.id.btn_delete_member).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MemberListActivity.class);
            intent.putExtra("IS_ADMIN", true);
            intent.putExtra("MODE", "DELETE");
            startActivity(intent);
        });

        // 2. Make Payment -> Member List (Select Member first) -> Payment Form
        findViewById(R.id.btn_make_payment).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MemberListActivity.class);
            intent.putExtra("IS_ADMIN", true);
            intent.putExtra("MODE", "PAYMENT");
            startActivity(intent);
        });

        // 3. Add Expenses -> Member List (Select Member first) -> Expense Form
        findViewById(R.id.btn_add_expenses).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MemberListActivity.class);
            intent.putExtra("IS_ADMIN", true);
            intent.putExtra("MODE", "EXPENSE");
            startActivity(intent);
        });

        // 4. Meal Order -> View Orders (Reuse MealListActivity with IS_ORDER=true)
        findViewById(R.id.btn_meal_order).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MealListActivity.class);
            intent.putExtra("IS_ADMIN", true);
            intent.putExtra("IS_ORDER", true);
            startActivity(intent);
        });

        // 5. Edit Meal Menu -> Meal List (Reuse same list for now)
        findViewById(R.id.btn_edit_meal_menu).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MealListActivity.class);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });
    }
}
