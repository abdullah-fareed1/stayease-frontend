package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class RoomDetailResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public RoomModel data;
}