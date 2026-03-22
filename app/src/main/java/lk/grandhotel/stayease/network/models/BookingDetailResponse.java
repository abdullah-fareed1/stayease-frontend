package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class BookingDetailResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public BookingDetailData data;

    public static class BookingDetailData {
        @SerializedName("booking")
        public BookingModel booking;
        @SerializedName("paymentAmount")
        public double paymentAmount;
        @SerializedName("nights")
        public int nights;
    }
}