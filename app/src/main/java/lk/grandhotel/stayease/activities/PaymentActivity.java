package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.io.File;
import java.util.Locale;

import lk.grandhotel.stayease.BuildConfig;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityPaymentBinding;
import lk.grandhotel.stayease.utils.ReceiptGenerator;
import lk.grandhotel.stayease.viewmodel.PaymentViewModel;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private PaymentViewModel viewModel;
    private PaymentSheet paymentSheet;

    private String bookingId;
    private double totalAmount;
    private double paymentAmount;
    private String paymentType;
    private String roomTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        bookingId = getIntent().getStringExtra("bookingId");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        paymentAmount = getIntent().getDoubleExtra("paymentAmount", 0);
        paymentType = getIntent().getStringExtra("paymentType");
        roomTitle = getIntent().getStringExtra("roomTitle");

        PaymentConfiguration.init(getApplicationContext(), BuildConfig.STRIPE_PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        populateSummary();
        setupObservers();

        binding.btnPayNow.setOnClickListener(v -> initiatePayment());
    }

    private void populateSummary() {
        binding.tvRoomTitle.setText(roomTitle != null ? roomTitle : "");
        binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));
        binding.tvPaymentType.setText("PARTIAL".equals(paymentType) ? "50% advance" : "Full payment");
        binding.tvPaymentAmount.setText(String.format(Locale.getDefault(), "$%.2f", paymentAmount));
    }

    private void initiatePayment() {
        showLoading(true, "Creating payment...");
        viewModel.initiatePayment(bookingId, paymentType);
    }

    private void setupObservers() {
        viewModel.paymentInitResult.observe(this, response -> {
            if (response != null && response.status && response.data != null) {
                showLoading(false, null);
                paymentSheet.presentWithPaymentIntent(
                        response.data.clientSecret,
                        new PaymentSheet.Configuration.Builder("Grand Horizon Hotels")
                                .build()
                );
            }
        });

        viewModel.paymentError.observe(this, msg -> {
            showLoading(false, null);
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onPaymentSheetResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            handlePaymentSuccess();
        } else if (result instanceof PaymentSheetResult.Canceled) {
            Snackbar.make(binding.getRoot(), "Payment cancelled.", Snackbar.LENGTH_SHORT).show();
            binding.btnPayNow.setEnabled(true);
        } else if (result instanceof PaymentSheetResult.Failed) {
            String msg = ((PaymentSheetResult.Failed) result).getError().getMessage();
            Snackbar.make(binding.getRoot(), "Payment failed: " + msg, Snackbar.LENGTH_LONG).show();
            binding.btnPayNow.setEnabled(true);
        }
    }

    private void handlePaymentSuccess() {
        File receiptFile = null;
        try {
            receiptFile = ReceiptGenerator.generateReceipt(this, bookingId, roomTitle,
                    totalAmount, paymentAmount, paymentType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final File finalReceipt = receiptFile;

        new MaterialAlertDialogBuilder(this)
                .setTitle("Payment Successful!")
                .setMessage(String.format(Locale.getDefault(),
                        "Your payment of $%.2f has been processed.\n\nBooking reference: %s",
                        paymentAmount, bookingId))
                .setPositiveButton("My Bookings", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("navigate_to", R.id.nav_bookings);
                    startActivity(intent);
                    finish();
                })
                .setNeutralButton(finalReceipt != null ? "View Receipt" : null,
                        finalReceipt != null ? (dialog, which) -> shareReceipt(finalReceipt) : null)
                .setCancelable(false)
                .show();
    }

    private void shareReceipt(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this,
                    "lk.grandhotel.stayease.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open Receipt"));
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), "Could not open receipt.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show, String statusText) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnPayNow.setEnabled(!show);
        if (statusText != null) {
            binding.tvStatus.setText(statusText);
            binding.tvStatus.setVisibility(View.VISIBLE);
        } else {
            binding.tvStatus.setVisibility(View.GONE);
        }
    }
}