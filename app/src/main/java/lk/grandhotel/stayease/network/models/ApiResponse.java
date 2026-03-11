package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
}