package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class BookingDetailResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public BookingModel data;
}