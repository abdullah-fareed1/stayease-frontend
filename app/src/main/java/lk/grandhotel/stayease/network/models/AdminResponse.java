package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AdminResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public AdminData data;

    public static class AdminData {
        @SerializedName("admin")
        public AdminModel admin;
        @SerializedName("accessToken")
        public String accessToken;
        @SerializedName("refreshToken")
        public String refreshToken;
    }
}