package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import lk.grandhotel.stayease.databinding.ActivityWriteReviewBinding;
import lk.grandhotel.stayease.viewmodel.BookingsViewModel;

public class WriteReviewActivity extends AppCompatActivity {

    private ActivityWriteReviewBinding binding;
    private BookingsViewModel viewModel;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        bookingId = getIntent().getStringExtra("bookingId");
        String roomTitle = getIntent().getStringExtra("roomTitle");
        if (roomTitle != null) binding.tvRoomTitle.setText(roomTitle);

        viewModel = new ViewModelProvider(this).get(BookingsViewModel.class);

        viewModel.reviewSuccess.observe(this, success -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSubmit.setEnabled(true);
            if (Boolean.TRUE.equals(success)) {
                Snackbar.make(binding.getRoot(), "Review submitted! Thank you.", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.error.observe(this, msg -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSubmit.setEnabled(true);
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating();
            String comment = binding.etComment.getText() != null
                    ? binding.etComment.getText().toString().trim() : "";

            if (rating == 0) {
                Snackbar.make(binding.getRoot(), "Please select a star rating.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnSubmit.setEnabled(false);
            viewModel.submitReview(bookingId, (int) rating, comment);
        });
    }
}