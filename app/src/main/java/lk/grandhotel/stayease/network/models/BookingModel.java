package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingModel {
    @SerializedName("id")
    public String id;
    @SerializedName("roomId")
    public String roomId;
    @SerializedName("userId")
    public String userId;
    @SerializedName("guestName")
    public String guestName;
    @SerializedName("guestEmail")
    public String guestEmail;
    @SerializedName("guestPhone")
    public String guestPhone;
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
    @SerializedName("cancelledAt")
    public String cancelledAt;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("room")
    public RoomModel room;
    @SerializedName("payments")
    public List<PaymentModel> payments;
}