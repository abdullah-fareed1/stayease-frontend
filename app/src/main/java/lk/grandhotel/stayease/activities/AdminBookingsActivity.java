package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityAdminBookingsBinding;
import lk.grandhotel.stayease.network.models.AdminBookingModel;
import lk.grandhotel.stayease.ui.admin.AdminBookingAdapter;
import lk.grandhotel.stayease.viewmodel.AdminBookingViewModel;

public class AdminBookingsActivity extends AppCompatActivity {

    private ActivityAdminBookingsBinding binding;
    private AdminBookingViewModel viewModel;
    private AdminBookingAdapter adapter;
    private String currentStatus = null; // null for all

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(AdminBookingViewModel.class);

        adapter = new AdminBookingAdapter(this::onBookingClick);

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookings.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadBookings(currentStatus));

        binding.fabCreateWalkIn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCreateWalkInActivity.class)));

        setupFilterChips();

        viewModel.bookings.observe(this, bookings -> {
            binding.swipeRefresh.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
            if (bookings == null || bookings.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvBookings.setVisibility(View.GONE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
                binding.rvBookings.setVisibility(View.VISIBLE);
                adapter.setBookings(bookings);
            }
        });

        viewModel.error.observe(this, msg -> {
            binding.swipeRefresh.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.loadBookings(currentStatus);
    }

    private void setupFilterChips() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentStatus = null;
            } else {
                Chip selectedChip = findViewById(checkedIds.get(0));
                String chipText = selectedChip.getText().toString();
                if ("All".equals(chipText)) {
                    currentStatus = null;
                } else {
                    currentStatus = chipText.toUpperCase().replace(" ", "_");
                }
            }
            binding.progressBar.setVisibility(View.VISIBLE);
            viewModel.loadBookings(currentStatus);
        });
    }

    private void onBookingClick(AdminBookingModel booking) {
        Intent intent = new Intent(this, AdminBookingDetailActivity.class);
        intent.putExtra("bookingId", booking.id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadBookings(currentStatus);
    }
}