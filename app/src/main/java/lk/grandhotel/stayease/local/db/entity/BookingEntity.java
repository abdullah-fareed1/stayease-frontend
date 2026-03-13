package lk.grandhotel.stayease.local.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "booking_drafts")
public class BookingEntity {
    @PrimaryKey
    @NonNull
    public String id = "";
    public String roomId;
    public String roomTitle;
    public String checkIn;
    public String checkOut;
    public int guestCount;
    public double totalAmount;
    public String paymentType;
    public String status;
}