package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import lk.grandhotel.stayease.databinding.ActivityMainBinding;
import lk.grandhotel.stayease.utils.TokenPrefs;
import lk.grandhotel.stayease.utils.UserPrefs;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogout.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> performLogout())
                        .setNegativeButton("Cancel", null)
                        .show()
        );
    }

    private void performLogout() {
        TokenPrefs.clearTokens(this);
        UserPrefs.clear(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}