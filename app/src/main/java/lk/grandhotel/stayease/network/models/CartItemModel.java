package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class CartItemModel {
    @SerializedName("id")
    public String id;
    @SerializedName("cartId")
    public String cartId;
    @SerializedName("roomId")
    public String roomId;
    @SerializedName("checkIn")
    public String checkIn;
    @SerializedName("checkOut")
    public String checkOut;
    @SerializedName("guestCount")
    public int guestCount;
    @SerializedName("room")
    public RoomModel room;
}