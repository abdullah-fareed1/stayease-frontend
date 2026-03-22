package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class CartItemModel {
    @SerializedName("id")
    public String id;
    @SerializedName("roomId")
    public String roomId;
    @SerializedName("checkIn")
    public String checkIn;
    @SerializedName("checkOut")
    public String checkOut;
    @SerializedName("guestCount")
    public int guestCount;
    @SerializedName("nights")
    public int nights;
    @SerializedName("subtotal")
    public double subtotal;
    @SerializedName("isRoomAvailable")
    public boolean isRoomAvailable;
    @SerializedName("room")
    public RoomModel room;
}