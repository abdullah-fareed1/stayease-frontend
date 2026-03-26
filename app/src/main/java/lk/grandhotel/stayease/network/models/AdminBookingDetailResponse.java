package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AdminBookingDetailResponse {
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public AdminBookingDetailData data;

    public static class AdminBookingDetailData {
        @SerializedName("booking") public BookingModel booking;
    }
}