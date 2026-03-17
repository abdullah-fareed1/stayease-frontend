package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import lk.grandhotel.stayease.databinding.ActivityAdminLoginBinding;
import lk.grandhotel.stayease.utils.AdminPrefs;
import lk.grandhotel.stayease.utils.AdminTokenPrefs;
import lk.grandhotel.stayease.utils.ValidationUtils;
import lk.grandhotel.stayease.viewmodel.AdminViewModel;

public class AdminLoginActivity extends AppCompatActivity {

    private ActivityAdminLoginBinding binding;
    private AdminViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        binding.tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, AdminForgotPasswordActivity.class)));

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.tvBackToLogin.setOnClickListener(v -> finish());

        viewModel.loginResult.observe(this, response -> {
            if (response == null) return;
            hideLoading();
            if (response.status && response.data != null) {
                AdminTokenPrefs.saveTokens(this, response.data.accessToken, response.data.refreshToken);
                if (response.data.admin != null) {
                    AdminPrefs.saveAdmin(this,
                            response.data.admin.id,
                            response.data.admin.name,
                            response.data.admin.email,
                            response.data.admin.role);
                }
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (response.message != null) {
                Snackbar.make(binding.getRoot(), response.message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.authError.observe(this, msg -> {
            if (msg == null) return;
            hideLoading();
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required.");
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError("Invalid email format.");
            return;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required.");
            return;
        }

        showLoading();
        viewModel.login(email, password);
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnLogin.setEnabled(true);
    }
}