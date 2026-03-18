package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityBookingBinding;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.viewmodel.BookingViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private BookingViewModel viewModel;

    private String roomId;
    private String roomTitle;
    private double pricePerNight;
    private int maxGuests;
    private String roomImageUrl;

    private long checkInMs = -1;
    private long checkOutMs = -1;
    private int guestCount = 1;

    private final SimpleDateFormat displayFmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private final SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isoFmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        roomId       = getIntent().getStringExtra("roomId");
        roomTitle    = getIntent().getStringExtra("roomTitle");
        pricePerNight = getIntent().getDoubleExtra("pricePerNight", 0);
        maxGuests    = getIntent().getIntExtra("maxGuests", 1);
        roomImageUrl = getIntent().getStringExtra("roomImageUrl");

        binding.tvRoomTitle.setText(roomTitle != null ? roomTitle : "");
        binding.tvPricePerNight.setText(String.format(Locale.getDefault(), "$%.0f / night", pricePerNight));
        binding.tvMaxGuests.setText("max " + maxGuests + " guests");
        binding.tvGuestCount.setText("1");

        if (roomImageUrl != null) {
            Glide.with(this)
                    .load(roomImageUrl)
                    .placeholder(R.drawable.ic_onboard_browse)
                    .centerCrop()
                    .into(binding.ivRoomImage);
        }

        binding.btnGuestMinus.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                binding.tvGuestCount.setText(String.valueOf(guestCount));
                updatePriceBreakdown();
            }
        });

        binding.btnGuestPlus.setOnClickListener(v -> {
            if (guestCount < maxGuests) {
                guestCount++;
                binding.tvGuestCount.setText(String.valueOf(guestCount));
                updatePriceBreakdown();
            } else {
                Snackbar.make(binding.getRoot(),
                        "Maximum " + maxGuests + " guests allowed for this room",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnSelectDates.setOnClickListener(v -> openDatePicker());

        binding.rgPaymentType.setOnCheckedChangeListener((group, checkedId) -> updatePriceBreakdown());

        binding.btnBookNow.setOnClickListener(v -> attemptBookNow());

        binding.btnAddToCart.setOnClickListener(v -> attemptAddToCart());

        observeViewModel();
    }

    private void openDatePicker() {
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select Check-in & Check-out")
                        .setCalendarConstraints(constraints)
                        .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            checkInMs  = selection.first;
            checkOutMs = selection.second;

            if (checkInMs==checkOutMs) {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.setTimeInMillis(checkOutMs);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                checkOutMs = cal.getTimeInMillis();
            }

            int nights = calculateNights();
            String checkInStr  = displayFmt.format(new Date(checkInMs));
            String checkOutStr = displayFmt.format(new Date(checkOutMs));

            binding.btnSelectDates.setText(checkInStr + "  →  " + checkOutStr);
            binding.tvDatesSummary.setText(nights + " night" + (nights != 1 ? "s" : ""));
            binding.tvDatesSummary.setVisibility(View.VISIBLE);
            updatePriceBreakdown();
        });

        picker.show(getSupportFragmentManager(), "date_range_picker");
    }

    private int calculateNights() {
        if (checkInMs < 0 || checkOutMs < 0) return 0;
        return (int) ((checkOutMs - checkInMs) / (1000L * 60 * 60 * 24));
    }

    private void updatePriceBreakdown() {
        int nights = calculateNights();
        if (nights <= 0) {
            binding.cardPriceBreakdown.setVisibility(View.GONE);
            return;
        }
        double subtotal  = pricePerNight * nights;
        boolean isPartial = binding.rbPartial.isChecked();
        double amountDue = isPartial ? subtotal * 0.5 : subtotal;

        binding.tvBreakdownNights.setText(
                String.format(Locale.getDefault(), "$%.0f × %d night%s",
                        pricePerNight, nights, nights != 1 ? "s" : ""));
        binding.tvBreakdownSubtotal.setText(
                String.format(Locale.getDefault(), "$%.2f", subtotal));
        binding.tvAmountDue.setText(
                String.format(Locale.getDefault(), "$%.2f", amountDue));

        binding.cardPriceBreakdown.setVisibility(View.VISIBLE);
    }

    private void attemptBookNow() {
        if (!validateInputs()) return;

        String paymentType = binding.rbPartial.isChecked() ? "PARTIAL" : "FULL";
        String checkIn  = isoFmt.format(new Date(checkInMs));
        String checkOut = isoFmt.format(new Date(checkOutMs));

        showLoading(true);
        viewModel.createBooking(roomId, checkIn, checkOut, guestCount, paymentType);
    }

    private void attemptAddToCart() {
        if (!validateInputs()) return;

        String checkIn  = isoFmt.format(new Date(checkInMs));
        String checkOut = isoFmt.format(new Date(checkOutMs));

        Map<String, Object> body = new HashMap<>();
        body.put("roomId", roomId);
        body.put("checkIn", checkIn);
        body.put("checkOut", checkOut);
        body.put("guestCount", guestCount);

        showLoading(true);
        ApiClient.getService(this).addToCart(body).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    Snackbar.make(binding.getRoot(), "Added to cart!", Snackbar.LENGTH_LONG)
                            .setAction("View Cart", v ->
                                    startActivity(new Intent(BookingActivity.this, CartActivity.class)))
                            .show();
                } else {
                    String msg = "Failed to add to cart";
                    try {
                        if (response.errorBody() != null) {
                            org.json.JSONObject json = new org.json.JSONObject(response.errorBody().string());
                            msg = json.optString("message", msg);
                        }
                    } catch (Exception ignored) {}
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showLoading(false);
                Snackbar.make(binding.getRoot(), "Network error. Check your connection.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        if (checkInMs < 0 || checkOutMs < 0) {
            Snackbar.make(binding.getRoot(), "Please select your check-in and check-out dates", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void observeViewModel() {
        viewModel.bookingResult.observe(this, response -> {
            showLoading(false);
            if (response != null && response.status && response.data != null) {
                boolean isPartial = binding.rbPartial.isChecked();
                double total     = response.data.totalAmount;
                double amountDue = isPartial ? total * 0.5 : total;

                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("bookingId",    response.data.id);
                intent.putExtra("paymentType",  isPartial ? "PARTIAL" : "FULL");
                intent.putExtra("amountDue",    amountDue);
                intent.putExtra("totalAmount",  total);
                intent.putExtra("roomTitle",    roomTitle);
                intent.putExtra("checkIn",      displayFmt.format(new Date(checkInMs)));
                intent.putExtra("checkOut",     displayFmt.format(new Date(checkOutMs)));
                startActivity(intent);
                finish();
            }
        });

        viewModel.bookingError.observe(this, msg -> {
            showLoading(false);
            if (msg != null) {
                String display = msg.toLowerCase().contains("available") || msg.toLowerCase().contains("conflict")
                        ? "These dates are no longer available. Please select different dates."
                        : msg;
                Snackbar.make(binding.getRoot(), display, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnBookNow.setEnabled(!show);
        binding.btnAddToCart.setEnabled(!show);
    }
}