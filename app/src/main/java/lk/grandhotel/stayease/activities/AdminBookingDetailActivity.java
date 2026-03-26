package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityAdminBookingDetailBinding;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.network.models.PaymentModel;
import lk.grandhotel.stayease.viewmodel.AdminBookingViewModel;

public class AdminBookingDetailActivity extends AppCompatActivity {

    private ActivityAdminBookingDetailBinding binding;
    private AdminBookingViewModel viewModel;
    private String bookingId;
    private BookingModel currentBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        bookingId = getIntent().getStringExtra("bookingId");

        viewModel = new ViewModelProvider(this).get(AdminBookingViewModel.class);

        setupObservers();

        binding.btnUpdateStatus.setOnClickListener(v -> {
            if (currentBooking != null) showStatusUpdateDialog();
        });

        showLoading(true);
        viewModel.loadBookingById(bookingId);
    }

    private void setupObservers() {
        viewModel.bookingResult.observe(this, booking -> {
            hideLoading();
            if (booking != null) {
                currentBooking = booking;
                populateDetail(booking);
            }
        });

        viewModel.error.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    private void populateDetail(BookingModel booking) {
        binding.layoutContent.setVisibility(View.VISIBLE);

        // Guest info
        binding.tvGuestName.setText(booking.guestName != null ? booking.guestName : "");
        binding.tvGuestEmail.setText(booking.guestEmail != null ? booking.guestEmail : "");
        binding.tvGuestPhone.setText(booking.guestPhone != null ? booking.guestPhone : "");

        // Booking info
        if (booking.room != null) {
            binding.tvRoomTitle.setText(booking.room.title != null ? booking.room.title : "");
        }
        binding.tvCheckIn.setText(formatDate(booking.checkIn));
        binding.tvCheckOut.setText(formatDate(booking.checkOut));
        binding.tvGuestCount.setText(String.valueOf(booking.guestCount));
        binding.tvStatus.setText(booking.status != null ? booking.status : "");
        binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", booking.getTotalAmountDouble()));

        // Payment info
        if (booking.payments != null && !booking.payments.isEmpty()) {
            PaymentModel payment = booking.payments.get(0);
            binding.tvPaymentType.setText(payment.type != null ? payment.type : "");
            binding.tvPaymentAmount.setText(String.format(Locale.getDefault(), "$%.2f", payment.getAmountDouble()));
            binding.tvPaymentStatus.setText(payment.status != null ? payment.status : "");
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null) return "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    private void showStatusUpdateDialog() {
        String[] statuses = getValidNextStatuses(currentBooking.status);
        if (statuses.length == 0) {
            Snackbar.make(binding.getRoot(), "No valid status updates available.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Booking Status")
                .setItems(statuses, (dialog, which) -> {
                    String newStatus = statuses[which].toUpperCase().replace(" ", "_");
                    showLoading(true);
                    viewModel.updateBookingStatus(bookingId, newStatus);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String[] getValidNextStatuses(String currentStatus) {
        if (currentStatus == null) return new String[0];
        switch (currentStatus.toUpperCase()) {
            case "PENDING":
                return new String[]{"Confirmed", "Cancelled"};
            case "CONFIRMED":
                return new String[]{"Checked In", "Cancelled"};
            case "CHECKED_IN":
                return new String[]{"Checked Out", "Cancelled"};
            case "CHECKED_OUT":
            case "CANCELLED":
            default:
                return new String[0];
        }
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void hideLoading() {
        showLoading(false);
    }
}