package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import lk.grandhotel.stayease.databinding.ActivityForgotPasswordBinding;
import lk.grandhotel.stayease.utils.ValidationUtils;
import lk.grandhotel.stayease.viewmodel.AuthViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSendOtp.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            binding.tilEmail.setError(null);

            if (!ValidationUtils.isValidEmail(email)) {
                binding.tilEmail.setError("Enter a valid email address.");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnSendOtp.setEnabled(false);
            viewModel.forgotPassword(email);
        });

        viewModel.forgotResult.observe(this, success -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSendOtp.setEnabled(true);
            if (Boolean.TRUE.equals(success)) {
                binding.layoutForm.setVisibility(View.GONE);
                binding.layoutSuccess.setVisibility(View.VISIBLE);
            }
        });

        viewModel.authError.observe(this, msg -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSendOtp.setEnabled(true);
            if (msg != null) binding.tilEmail.setError(msg);
        });

        binding.btnGoToReset.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });
    }
}