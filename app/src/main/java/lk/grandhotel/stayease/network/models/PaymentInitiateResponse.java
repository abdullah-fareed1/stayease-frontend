package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class PaymentInitiateResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public PaymentData data;

    public static class PaymentData {
        @SerializedName("clientSecret")
        public String clientSecret;
        @SerializedName("paymentId")
        public String paymentId;
        @SerializedName("amount")
        public double amount;
    }
}