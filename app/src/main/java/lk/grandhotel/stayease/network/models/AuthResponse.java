package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public AuthData data;

    public static class AuthData {
        @SerializedName("user")
        public UserModel user;
        @SerializedName("accessToken")
        public String accessToken;
        @SerializedName("refreshToken")
        public String refreshToken;
    }
}