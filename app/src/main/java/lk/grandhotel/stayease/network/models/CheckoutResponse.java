package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CheckoutResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public CheckoutData data;

    public static class CheckoutData {
        @SerializedName("bookings")
        public List<CheckoutBooking> bookings;
    }

    public static class CheckoutBooking {
        @SerializedName("bookingId")
        public String bookingId;
        @SerializedName("totalAmount")
        public double totalAmount;
        @SerializedName("paymentAmount")
        public double paymentAmount;
        @SerializedName("nights")
        public int nights;
    }
}