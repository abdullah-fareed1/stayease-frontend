package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import lk.grandhotel.stayease.databinding.ActivityResetPasswordBinding;
import lk.grandhotel.stayease.utils.ValidationUtils;
import lk.grandhotel.stayease.viewmodel.AuthViewModel;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private AuthViewModel viewModel;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        email = getIntent().getStringExtra("email");

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnReset.setOnClickListener(v -> {
            String otp = binding.etOtp.getText() != null ? binding.etOtp.getText().toString().trim() : "";
            String newPassword = binding.etNewPassword.getText() != null ? binding.etNewPassword.getText().toString() : "";
            String confirm = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString() : "";

            binding.tilOtp.setError(null);
            binding.tilNewPassword.setError(null);
            binding.tilConfirmPassword.setError(null);

            if (otp.isEmpty()) {
                binding.tilOtp.setError("OTP is required.");
                return;
            }
            if (!ValidationUtils.isValidPassword(newPassword)) {
                binding.tilNewPassword.setError("Min 8 chars, 1 uppercase, 1 lowercase, 1 number.");
                return;
            }
            if (!ValidationUtils.passwordsMatch(newPassword, confirm)) {
                binding.tilConfirmPassword.setError("Passwords do not match.");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnReset.setEnabled(false);
            viewModel.resetPassword(otp, email, newPassword);
        });

        viewModel.resetResult.observe(this, success -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnReset.setEnabled(true);
            if (Boolean.TRUE.equals(success)) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Password Reset")
                        .setMessage("Your password has been reset successfully.")
                        .setPositiveButton("Sign In", (dialog, which) -> {
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        viewModel.authError.observe(this, msg -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnReset.setEnabled(true);
            if (msg != null) binding.tilOtp.setError(msg);
        });
    }
}