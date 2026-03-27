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

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AdminSendNotificationViewModel.class);

        // Setup UI listeners
        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        // Title text change listener for preview
        binding.titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Body text change listener for preview
        binding.bodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // RadioGroup listener for showing/hiding userId field
        binding.targetTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioAllGuests) {
                binding.userIdInputLayout.setVisibility(View.GONE);
                binding.userIdEditText.setText("");
            } else if (checkedId == R.id.radioSpecificGuest) {
                binding.userIdInputLayout.setVisibility(View.VISIBLE);
            }
        });

        // Send button click listener
        binding.sendButton.setOnClickListener(v -> sendNotification());
    }

    private void setupObservers() {
        // Observe send result
        viewModel.sendResult.observe(this, result -> {
            if (result != null) {
                showSuccess(result);
                clearForm();
            }
        });

        // Observe errors
        viewModel.error.observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });

        // Observe loading state
        viewModel.loading.observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.sendButton.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.sendButton.setEnabled(true);
            }
        });
    }

    private void updatePreview() {
        String title = binding.titleEditText.getText().toString().trim();
        String body = binding.bodyEditText.getText().toString().trim();

        // Update preview title
        if (title.isEmpty()) {
            binding.previewTitle.setText("Notification Title");
        } else {
            binding.previewTitle.setText(title);
        }

        // Update preview body
        if (body.isEmpty()) {
            binding.previewBody.setText("Notification Message");
        } else {
            binding.previewBody.setText(body);
        }
    }

    private void sendNotification() {
        // Get input values
        String title = binding.titleEditText.getText().toString().trim();
        String body = binding.bodyEditText.getText().toString().trim();
        String userId = binding.userIdEditText.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(title, body, userId)) {
            return;
        }

        // Determine target type
        String targetType = binding.targetTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioAllGuests 
                ? TARGET_TYPE_ALL 
                : TARGET_TYPE_SPECIFIC;

        // Send notification via ViewModel
        viewModel.sendNotification(title, body, targetType, userId);
    }

    private boolean validateInputs(String title, String body, String userId) {
        // Check title
        if (title.isEmpty()) {
            binding.titleInputLayout.setError(getString(R.string.title_required));
            return false;
        } else {
            binding.titleInputLayout.setError(null);
        }

        // Check body
        if (body.isEmpty()) {
            binding.bodyInputLayout.setError(getString(R.string.body_required));
            return false;
        } else {
            binding.bodyInputLayout.setError(null);
        }

        // Check userId if specific guest is selected
        if (binding.targetTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioSpecificGuest) {
            if (userId.isEmpty()) {
                binding.userIdInputLayout.setError(getString(R.string.user_id_required));
                return false;
            } else {
                binding.userIdInputLayout.setError(null);
            }
        }

        return true;
    }

    private void clearForm() {
        binding.titleEditText.setText("");
        binding.bodyEditText.setText("");
        binding.userIdEditText.setText("");
        binding.targetTypeRadioGroup.check(R.id.radioAllGuests);
        binding.userIdInputLayout.setVisibility(View.GONE);
        updatePreview();
    }

    private void showSuccess(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
    }
}
