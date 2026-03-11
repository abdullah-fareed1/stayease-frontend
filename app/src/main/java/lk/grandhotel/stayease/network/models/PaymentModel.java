package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class PaymentModel {
    @SerializedName("id")
    public String id;
    @SerializedName("bookingId")
    public String bookingId;
    @SerializedName("amount")
    public double amount;
    @SerializedName("type")
    public String type;
    @SerializedName("status")
    public String status;
    @SerializedName("stripePaymentIntentId")
    public String stripePaymentIntentId;
    @SerializedName("paidAt")
    public String paidAt;
    @SerializedName("createdAt")
    public String createdAt;
}