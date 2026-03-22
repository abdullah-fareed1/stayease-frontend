package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingListResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public BookingListData data;

    public static class BookingListData {
        @SerializedName("bookings")
        public List<BookingModel> bookings;
        @SerializedName("meta")
        public PaginationMeta meta;
    }
}