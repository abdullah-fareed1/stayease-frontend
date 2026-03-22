package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityBookingBinding;
import lk.grandhotel.stayease.utils.DateUtils;
import lk.grandhotel.stayease.viewmodel.BookingViewModel;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private BookingViewModel viewModel;

    private String roomId;
    private String roomTitle;
    private double pricePerNight;
    private int maxGuests;
    private String roomImageUrl;

    private long checkInMs = 0;
    private long checkOutMs = 0;
    private int guestCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomId = getIntent().getStringExtra("roomId");
        roomTitle = getIntent().getStringExtra("roomTitle");
        pricePerNight = getIntent().getDoubleExtra("pricePerNight", 0);
        maxGuests = getIntent().getIntExtra("maxGuests", 1);
        roomImageUrl = getIntent().getStringExtra("roomImageUrl");

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        populateRoomSummary();
        setupGuestCounter();
        setupDatePicker();
        setupObservers();

        binding.btnBookNow.setOnClickListener(v -> attemptBooking());
        binding.btnAddToCart.setOnClickListener(v -> attemptAddToCart());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateRoomSummary() {
        binding.tvRoomTitle.setText(roomTitle != null ? roomTitle : "");
        binding.tvPricePerNight.setText(String.format(Locale.getDefault(), "$%.0f / night", pricePerNight));
        binding.tvMaxGuests.setText("Up to " + maxGuests + " guests");
        Glide.with(this)
                .load(roomImageUrl)
                .placeholder(R.drawable.ic_onboard_browse)
                .centerCrop()
                .into(binding.ivRoomImage);
    }

    private void setupGuestCounter() {
        updateGuestDisplay();
        binding.btnGuestMinus.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                updateGuestDisplay();
                updatePriceBreakdown();
            }
        });
        binding.btnGuestPlus.setOnClickListener(v -> {
            if (guestCount < maxGuests) {
                guestCount++;
                updateGuestDisplay();
                updatePriceBreakdown();
            } else {
                showSnackbar("Max " + maxGuests + " guests allowed.");
            }
        });
    }

    private void updateGuestDisplay() {
        binding.tvGuestCount.setText(String.valueOf(guestCount));
    }

    private void setupDatePicker() {
        binding.tvSelectDates.setOnClickListener(v -> openDatePicker());
        binding.btnSelectDates.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTitleText("Select Check-in & Check-out")
                .setCalendarConstraints(constraints)
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {
                checkInMs = selection.first;
                checkOutMs = selection.second;
                int nights = DateUtils.calculateNights(checkInMs, checkOutMs);
                if (nights < 1) {
                    showSnackbar("Minimum stay is 1 night.");
                    checkInMs = 0;
                    checkOutMs = 0;
                    return;
                }
                binding.tvSelectedDates.setText(
                        DateUtils.toDisplayString(checkInMs) + "  →  " + DateUtils.toDisplayString(checkOutMs));
                binding.tvNights.setText(nights + (nights == 1 ? " night" : " nights"));
                updatePriceBreakdown();
            }
        });

        picker.show(getSupportFragmentManager(), "date_range_picker");
    }

    private void updatePriceBreakdown() {
        if (checkInMs == 0 || checkOutMs == 0) {
            binding.layoutPriceBreakdown.setVisibility(View.GONE);
            return;
        }
        int nights = DateUtils.calculateNights(checkInMs, checkOutMs);
        double total = pricePerNight * nights;
        boolean isPartial = binding.rgPaymentType.getCheckedRadioButtonId() == R.id.rb_partial;
        double amountDue = isPartial ? total * 0.5 : total;

        binding.tvBreakdownNights.setText(String.format(Locale.getDefault(),
                "$%.0f × %d nights = $%.2f", pricePerNight, nights, total));
        binding.tvBreakdownDue.setText(String.format(Locale.getDefault(),
                "Amount due now: $%.2f%s", amountDue, isPartial ? " (50%)" : " (full)"));
        binding.layoutPriceBreakdown.setVisibility(View.VISIBLE);

        binding.rgPaymentType.setOnCheckedChangeListener((group, checkedId) -> updatePriceBreakdown());
    }

    private void attemptBooking() {
        if (!validateInputs()) return;
        boolean isPartial = binding.rgPaymentType.getCheckedRadioButtonId() == R.id.rb_partial;
        showLoading(true);
        viewModel.createBooking(
                roomId,
                DateUtils.toIsoString(checkInMs),
                DateUtils.toIsoString(checkOutMs),
                guestCount,
                isPartial ? "PARTIAL" : "FULL"
        );
    }

    private void attemptAddToCart() {
        if (!validateInputs()) return;
        showLoading(true);
        viewModel.addToCart(
                roomId,
                DateUtils.toIsoString(checkInMs),
                DateUtils.toIsoString(checkOutMs),
                guestCount
        );
    }

    private boolean validateInputs() {
        if (checkInMs == 0 || checkOutMs == 0) {
            showSnackbar("Please select check-in and check-out dates.");
            return false;
        }
        return true;
    }

    private void setupObservers() {
        viewModel.bookingResult.observe(this, response -> {
            showLoading(false);
            if (response != null && response.status && response.data != null && response.data.booking != null) {
                boolean isPartial = binding.rgPaymentType.getCheckedRadioButtonId() == R.id.rb_partial;
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("bookingId", response.data.booking.id);
                intent.putExtra("totalAmount", response.data.booking.getTotalAmountDouble());
                intent.putExtra("paymentAmount", response.data.paymentAmount);
                intent.putExtra("paymentType", isPartial ? "PARTIAL" : "FULL");
                intent.putExtra("roomTitle", roomTitle);
                startActivity(intent);
                finish();
            }
        });

        viewModel.cartAddResult.observe(this, success -> {
            showLoading(false);
            if (Boolean.TRUE.equals(success)) {
                Snackbar.make(binding.getRoot(), "Added to cart!", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.bottomBar)
                        .setAction("View Cart", v -> startActivity(new Intent(this, CartActivity.class)))
                        .show();
            }
        });

        viewModel.bookingError.observe(this, msg -> {
            showLoading(false);
            if (msg != null) showSnackbar(msg);
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.bottomBar)
                .show();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnBookNow.setEnabled(!show);
        binding.btnAddToCart.setEnabled(!show);
    }
}