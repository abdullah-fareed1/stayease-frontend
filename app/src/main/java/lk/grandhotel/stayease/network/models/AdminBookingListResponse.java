package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminBookingListResponse {
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public AdminBookingListData data;

    public static class AdminBookingListData {
        @SerializedName("bookings") public List<AdminBookingModel> bookings;
        @SerializedName("pagination") public PaginationMeta pagination;
    }
}