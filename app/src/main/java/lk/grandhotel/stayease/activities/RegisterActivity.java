package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import lk.grandhotel.stayease.databinding.ActivityRegisterBinding;
import lk.grandhotel.stayease.utils.TokenPrefs;
import lk.grandhotel.stayease.utils.UserPrefs;
import lk.grandhotel.stayease.utils.ValidationUtils;
import lk.grandhotel.stayease.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.tvSignIn.setOnClickListener(v -> finish());

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        viewModel.authResult.observe(this, response -> {
            hideLoading();
            if (response != null && response.status && response.data != null) {
                TokenPrefs.saveTokens(this, response.data.accessToken, response.data.refreshToken);
                if (response.data.user != null) {
                    UserPrefs.saveUser(this,
                            response.data.user.id,
                            response.data.user.name,
                            response.data.user.email,
                            response.data.user.phone);
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        viewModel.authError.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    private void attemptRegister() {
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
        String confirm = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";

        binding.tilName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
        binding.tilPhone.setError(null);

        if (!ValidationUtils.isValidName(name)) {
            binding.tilName.setError("Name must be at least 2 characters.");
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError("Invalid email format.");
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.setError("Min 8 chars, 1 uppercase, 1 lowercase, 1 number.");
            return;
        }
        if (!ValidationUtils.passwordsMatch(password, confirm)) {
            binding.tilConfirmPassword.setError("Passwords do not match.");
            return;
        }
        if (!phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
            binding.tilPhone.setError("Invalid phone number format.");
            return;
        }

        showLoading();
        viewModel.register(name, email, password, phone.isEmpty() ? null : phone);
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnRegister.setEnabled(true);
    }
}