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
        @SerializedName("stats")
        public Stats stats;
        @SerializedName("bestPerformingRooms")
        public List<BestRoom> bestPerformingRooms;
        @SerializedName("revenueByMonth")
        public List<MonthRevenue> revenueByMonth;
    }

    public static class Stats {
        @SerializedName("totalRooms")
        public int totalRooms;
        @SerializedName("availableRooms")
        public int availableRooms;
        @SerializedName("todayCheckIns")
        public int todayCheckIns;
        @SerializedName("todayCheckOuts")
        public int todayCheckOuts;
        @SerializedName("totalRevenue")
        public String totalRevenue;
        @SerializedName("monthlyRevenue")
        public String monthlyRevenue;
        @SerializedName("totalBookings")
        public int totalBookings;
        @SerializedName("pendingBookings")
        public int pendingBookings;
    }

    public static class BestRoom {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("bookingCount")
        public int bookingCount;
        @SerializedName("revenue")
        public String revenue;
    }

    public static class MonthRevenue {
        @SerializedName("month")
        public String month;
        @SerializedName("revenue")
        public String revenue;
    }
}