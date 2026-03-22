package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityBookingDetailBinding;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.network.models.PaymentModel;
import lk.grandhotel.stayease.ui.bookings.PaymentHistoryAdapter;
import lk.grandhotel.stayease.utils.DateUtils;
import lk.grandhotel.stayease.viewmodel.BookingsViewModel;

public class BookingDetailActivity extends AppCompatActivity {

    private ActivityBookingDetailBinding binding;
    private BookingsViewModel viewModel;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        bookingId = getIntent().getStringExtra("bookingId");
        viewModel = new ViewModelProvider(this).get(BookingsViewModel.class);

        binding.rvPayments.setLayoutManager(new LinearLayoutManager(this));

        viewModel.bookingDetail.observe(this, booking -> {
            if (booking != null) {
                hideLoading();
                populateDetail(booking);
            }
        });

        viewModel.cancelSuccess.observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                hideLoading();
                Snackbar.make(binding.getRoot(), "Booking cancelled successfully.", Snackbar.LENGTH_SHORT).show();
                viewModel.loadBookingDetail(bookingId);
            }
        });

        viewModel.error.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        showLoading();
        viewModel.loadBookingDetail(bookingId);
    }

    private void populateDetail(BookingModel b) {
        String roomTitle = (b.room != null && b.room.title != null) ? b.room.title : "Room";
        binding.tvRoomTitle.setText(roomTitle);
        binding.tvBookingRef.setText("Ref: " + b.id);
        binding.tvCheckIn.setText("Check-in: " + DateUtils.toDisplayString(b.checkIn));
        binding.tvCheckOut.setText("Check-out: " + DateUtils.toDisplayString(b.checkOut));
        binding.tvGuests.setText(b.guestCount + " guest" + (b.guestCount > 1 ? "s" : ""));
        binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", b.getTotalAmountDouble()));
        binding.tvStatus.setText(b.status != null ? b.status.replace("_", " ") : "");

        if (b.room != null && b.room.primaryImage != null) {
            Glide.with(this)
                    .load(b.room.primaryImage.url)
                    .placeholder(R.drawable.ic_onboard_browse)
                    .centerCrop()
                    .into(binding.ivRoomImage);
        }

        if (b.payments != null && !b.payments.isEmpty()) {
            binding.rvPayments.setVisibility(View.VISIBLE);
            binding.tvNoPayments.setVisibility(View.GONE);
            binding.rvPayments.setAdapter(new PaymentHistoryAdapter(b.payments));
            setupPayRemainingButton(b);
        } else {
            binding.rvPayments.setVisibility(View.GONE);
            binding.tvNoPayments.setVisibility(View.VISIBLE);
            binding.btnPayRemaining.setVisibility(View.GONE);
        }

        setupCancelButton(b);
        setupWriteReviewButton(b);
    }

    private void setupCancelButton(BookingModel b) {
        boolean isCancellable = ("PENDING".equals(b.status) || "CONFIRMED".equals(b.status))
                && isMoreThan24HoursAway(b.checkIn);
        binding.btnCancelBooking.setVisibility(isCancellable ? View.VISIBLE : View.GONE);
        binding.btnCancelBooking.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Cancel Booking")
                        .setMessage("Are you sure you want to cancel this booking? This cannot be undone.")
                        .setPositiveButton("Cancel Booking", (dialog, which) -> {
                            showLoading();
                            viewModel.cancelBooking(bookingId);
                        })
                        .setNegativeButton("Keep Booking", null)
                        .show());
    }

    private void setupWriteReviewButton(BookingModel b) {
        boolean canReview = "CHECKED_OUT".equals(b.status);
        binding.btnWriteReview.setVisibility(canReview ? View.VISIBLE : View.GONE);
        binding.btnWriteReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("bookingId", b.id);
            intent.putExtra("roomTitle", b.room != null ? b.room.title : "");
            startActivity(intent);
        });
    }

    private void setupPayRemainingButton(BookingModel b) {
        double totalPaid = 0;
        if (b.payments != null) {
            for (PaymentModel p : b.payments) {
                if ("PAID".equals(p.status)) totalPaid += p.getAmountDouble();
            }
        }
        boolean hasRemaining = "CONFIRMED".equals(b.status)
                && totalPaid < b.getTotalAmountDouble();
        binding.btnPayRemaining.setVisibility(hasRemaining ? View.VISIBLE : View.GONE);
        if (hasRemaining) {
            double remaining = b.getTotalAmountDouble() - totalPaid;
            binding.btnPayRemaining.setText(String.format(Locale.getDefault(),
                    "Pay Remaining $%.2f", remaining));
            binding.btnPayRemaining.setOnClickListener(v -> {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("bookingId", b.id);
                intent.putExtra("totalAmount", b.getTotalAmountDouble());
                intent.putExtra("paymentAmount", remaining);
                intent.putExtra("paymentType", "FULL");
                intent.putExtra("roomTitle", b.room != null ? b.room.title : "");
                startActivity(intent);
            });
        }
    }

    private boolean isMoreThan24HoursAway(String checkInStr) {
        if (checkInStr == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date checkIn = sdf.parse(checkInStr);
            if (checkIn == null) return false;
            long diff = checkIn.getTime() - System.currentTimeMillis();
            return diff > 24L * 60 * 60 * 1000;
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date checkIn = sdf2.parse(checkInStr.substring(0, 10));
                if (checkIn == null) return false;
                long diff = checkIn.getTime() - System.currentTimeMillis();
                return diff > 24L * 60 * 60 * 1000;
            } catch (ParseException ex) {
                return false;
            }
        }
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutContent.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
    }
}