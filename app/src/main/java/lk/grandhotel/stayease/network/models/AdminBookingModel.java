package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AdminBookingModel {
    @SerializedName("id")
    public String id;
    @SerializedName("guestName")
    public String guestName;
    @SerializedName("guestEmail")
    public String guestEmail;
    @SerializedName("roomTitle")
    public String roomTitle;
    @SerializedName("checkIn")
    public String checkIn;
    @SerializedName("checkOut")
    public String checkOut;
    @SerializedName("status")
    public String status;
    @SerializedName("totalAmount")
    public String totalAmount;
    @SerializedName("createdAt")
    public String createdAt;

    public double getTotalAmountDouble() {
        try {
            return totalAmount != null ? Double.parseDouble(totalAmount) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}