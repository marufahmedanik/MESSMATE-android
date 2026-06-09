package ewubd.edu.messmate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LogoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // Handle "No" button - Cancel logout
        findViewById(R.id.btn_no).setOnClickListener(v -> finish());

        // Handle "Yes" button - Perform logout
        findViewById(R.id.btn_yes).setOnClickListener(v -> {
            // Return to Login Activity and clear the back stack
            Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
