package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class BookingModel {
    @SerializedName("id")
    public String id;
    @SerializedName("roomId")
    public String roomId;
    @SerializedName("userId")
    public String userId;
    @SerializedName("checkIn")
    public String checkIn;
    @SerializedName("checkOut")
    public String checkOut;
    @SerializedName("guestCount")
    public int guestCount;
    @SerializedName("totalAmount")
    public double totalAmount;
    @SerializedName("status")
    public String status;
    @SerializedName("paymentType")
    public String paymentType;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("room")
    public RoomModel room;
}