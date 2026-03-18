package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import lk.grandhotel.stayease.R;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID   = "bookingId";
    public static final String EXTRA_PAYMENT_TYPE = "paymentType";
    public static final String EXTRA_AMOUNT_DUE   = "amountDue";
    public static final String EXTRA_TOTAL_AMOUNT = "totalAmount";
    public static final String EXTRA_ROOM_TITLE   = "roomTitle";
    public static final String EXTRA_CHECK_IN     = "checkIn";
    public static final String EXTRA_CHECK_OUT    = "checkOut";

    protected String bookingId;
    protected String paymentType;
    protected double amountDue;
    protected double totalAmount;
    protected String roomTitle;
    protected String checkIn;
    protected String checkOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bookingId   = getIntent().getStringExtra(EXTRA_BOOKING_ID);
        paymentType = getIntent().getStringExtra(EXTRA_PAYMENT_TYPE);
        amountDue   = getIntent().getDoubleExtra(EXTRA_AMOUNT_DUE, 0);
        totalAmount = getIntent().getDoubleExtra(EXTRA_TOTAL_AMOUNT, 0);
        roomTitle   = getIntent().getStringExtra(EXTRA_ROOM_TITLE);
        checkIn     = getIntent().getStringExtra(EXTRA_CHECK_IN);
        checkOut    = getIntent().getStringExtra(EXTRA_CHECK_OUT);
    }
}