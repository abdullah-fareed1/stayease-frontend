package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityAdminDashboardBinding;
import lk.grandhotel.stayease.network.models.DashboardResponse;
import lk.grandhotel.stayease.utils.AdminPrefs;
import lk.grandhotel.stayease.utils.AdminTokenPrefs;
import lk.grandhotel.stayease.viewmodel.AdminViewModel;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private AdminViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        String name = AdminPrefs.getAdminName(this);
        binding.tvAdminName.setText(name != null ? name : "Admin");

        String role = AdminPrefs.getAdminRole(this);
        binding.tvWelcome.setText(role != null ? role : "Staff");

        showLoading();
        viewModel.loadDashboard();

        viewModel.dashboardResult.observe(this, response -> {
            hideLoading();
            if (response != null && response.data != null) {
                populateDashboard(response.data);
            }
        });

        viewModel.authError.observe(this, msg -> {
            hideLoading();
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        // TODO: Navigate to AdminRoomsActivity (Phase 14)
        binding.btnManageRooms.setOnClickListener(v -> {
            // startActivity(new Intent(this, AdminRoomsActivity.class));
            Snackbar.make(binding.getRoot(), "Room management coming soon.", Snackbar.LENGTH_SHORT).show();
        });

        // TODO: Navigate to AdminBookingsActivity (Phase 15)
        binding.btnManageBookings.setOnClickListener(v -> {
            // startActivity(new Intent(this, AdminBookingsActivity.class));
            Snackbar.make(binding.getRoot(), "Booking management coming soon.", Snackbar.LENGTH_SHORT).show();
        });

        // TODO: Navigate to AdminSendNotificationActivity (Phase 16)
        binding.btnSendNotification.setOnClickListener(v -> {
            // startActivity(new Intent(this, AdminSendNotificationActivity.class));
            Snackbar.make(binding.getRoot(), "Notifications coming soon.", Snackbar.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.logout))
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(getString(R.string.logout), (dialog, which) -> performLogout())
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    private void populateDashboard(DashboardResponse.DashboardData data) {
        binding.layoutContent.setVisibility(View.VISIBLE);

        binding.tvTotalRooms.setText(String.valueOf(data.totalRooms));
        binding.tvAvailableRooms.setText(String.valueOf(data.availableRooms));
        binding.tvCheckins.setText(String.valueOf(data.todayCheckIns));
        binding.tvCheckouts.setText(String.valueOf(data.todayCheckOuts));
        binding.tvMonthlyRevenue.setText(String.format("$%.2f", data.monthlyRevenue));
        binding.tvTotalRevenue.setText(String.format("$%.2f", data.totalRevenue));
        binding.tvTotalBookings.setText(String.valueOf(data.totalBookings));
        binding.tvPendingBookings.setText(String.valueOf(data.pendingBookings));

        if (data.bestPerformingRooms != null && !data.bestPerformingRooms.isEmpty()) {
            binding.containerBestRooms.removeAllViews();
            for (DashboardResponse.BestRoom room : data.bestPerformingRooms) {
                View row = getLayoutInflater().inflate(R.layout.item_best_room, binding.containerBestRooms, false);
                ((TextView) row.findViewById(R.id.tv_room_title)).setText(room.title);
                ((TextView) row.findViewById(R.id.tv_room_bookings)).setText(room.bookingCount + " bookings");
                ((TextView) row.findViewById(R.id.tv_room_revenue)).setText("");
                binding.containerBestRooms.addView(row);
            }
        } else {
            TextView empty = new TextView(this);
            empty.setText(getString(R.string.no_data));
            empty.setTextColor(getResources().getColor(R.color.on_surface_variant, getTheme()));
            binding.containerBestRooms.addView(empty);
        }
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutContent.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void performLogout() {
        AdminTokenPrefs.clearTokens(this);
        AdminPrefs.clear(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}