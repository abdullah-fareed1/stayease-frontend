package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import lk.grandhotel.stayease.databinding.ActivityLoginBinding;
import lk.grandhotel.stayease.utils.TokenPrefs;
import lk.grandhotel.stayease.utils.UserPrefs;
import lk.grandhotel.stayease.utils.ValidationUtils;
import lk.grandhotel.stayease.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        binding.tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        binding.btnSignIn.setOnClickListener(v -> attemptLogin());

        viewModel.authResult.observe(this, response -> {
            if (response == null) return;
            hideLoading();
            if (response.status && response.data != null) {
                TokenPrefs.saveTokens(this, response.data.accessToken, response.data.refreshToken);
                if (response.data.user != null) {
                    UserPrefs.saveUser(this,
                            response.data.user.id,
                            response.data.user.name,
                            response.data.user.email,
                            response.data.user.phone);
                }
                viewModel.clearAuthResult();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (response.message != null) {
                Snackbar.make(binding.getRoot(), response.message, Snackbar.LENGTH_LONG).show();
                viewModel.clearAuthResult();
            }
        });

        viewModel.authError.observe(this, msg -> {
            if (msg == null) return;
            hideLoading();
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            viewModel.clearAuthResult();
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

        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                String fcmToken = task.isSuccessful() ? task.getResult() : null;
                viewModel.login(email, password, fcmToken);
            });
        } catch (Exception e) {
            viewModel.login(email, password, null);
        }
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSignIn.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnSignIn.setEnabled(true);
    }
}