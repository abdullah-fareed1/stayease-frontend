package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityAdminSendNotificationBinding;
import lk.grandhotel.stayease.viewmodel.AdminSendNotificationViewModel;

public class AdminSendNotificationActivity extends AppCompatActivity {

    private ActivityAdminSendNotificationBinding binding;
    private AdminSendNotificationViewModel viewModel;
    private static final String TARGET_TYPE_ALL = "ALL";
    private static final String TARGET_TYPE_SPECIFIC = "SPECIFIC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSendNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        viewModel = new ViewModelProvider(this).get(AdminSendNotificationViewModel.class);
        setupListeners();
        setupObservers();
    }
    private void setupListeners() {
        binding.titleEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override public void afterTextChanged(Editable s) {}
        });
        binding.bodyEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override public void afterTextChanged(Editable s) {}
        });
        binding.sendButton.setOnClickListener(v -> sendNotification());
    }

    private void setupObservers() {
        viewModel.sendResult.observe(this, result -> {
            if (result != null) {
                Snackbar.make(binding.getRoot(), result, Snackbar.LENGTH_LONG).show();
                clearForm();
            }
        });
        viewModel.error.observe(this, error -> {
            if (error != null) Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
        viewModel.loading.observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.sendButton.setEnabled(!isLoading);
        });
    }

    private void updatePreview() {
        String title = binding.titleEditText.getText().toString().trim();
        String body = binding.bodyEditText.getText().toString().trim();
        binding.previewTitle.setText(title.isEmpty() ? "Notification Title" : title);
        binding.previewBody.setText(body.isEmpty() ? "Notification Message" : body);
    }

    private void sendNotification() {
        String title = binding.titleEditText.getText().toString().trim();
        String body = binding.bodyEditText.getText().toString().trim();
        if (title.isEmpty()) { binding.titleInputLayout.setError(getString(R.string.title_required)); return; }
        else binding.titleInputLayout.setError(null);
        if (body.isEmpty()) { binding.bodyInputLayout.setError(getString(R.string.body_required)); return; }
        else binding.bodyInputLayout.setError(null);
        viewModel.sendNotification(title, body, "ALL", null);
    }

    private void clearForm() {
        binding.titleEditText.setText("");
        binding.bodyEditText.setText("");
        updatePreview();
    }

}
