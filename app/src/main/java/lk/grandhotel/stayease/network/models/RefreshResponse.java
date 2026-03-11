package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class RefreshResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public RefreshData data;

    public static class RefreshData {
        @SerializedName("accessToken")
        public String accessToken;
        @SerializedName("refreshToken")
        public String refreshToken;
    }
}