package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public DashboardData data;

    public static class DashboardData {
        @SerializedName("totalRooms")
        public int totalRooms;
        @SerializedName("availableRooms")
        public int availableRooms;
        @SerializedName("todayCheckIns")
        public int todayCheckIns;
        @SerializedName("todayCheckOuts")
        public int todayCheckOuts;
        @SerializedName("totalRevenue")
        public double totalRevenue;
        @SerializedName("monthlyRevenue")
        public double monthlyRevenue;
        @SerializedName("totalBookings")
        public int totalBookings;
        @SerializedName("pendingBookings")
        public int pendingBookings;
        @SerializedName("bestPerformingRooms")
        public List<BestRoom> bestPerformingRooms;
        @SerializedName("revenueByMonth")
        public List<MonthRevenue> revenueByMonth;
    }

    public static class BestRoom {
        @SerializedName("roomId")
        public String roomId;
        @SerializedName("title")
        public String title;
        @SerializedName("bookingCount")
        public int bookingCount;
        @SerializedName("category")
        public String category;
    }

    public static class MonthRevenue {
        @SerializedName("month")
        public String month;
        @SerializedName("revenue")
        public double revenue;
    }
}