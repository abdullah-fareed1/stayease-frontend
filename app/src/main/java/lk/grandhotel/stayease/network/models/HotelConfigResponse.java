package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class HotelConfigResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public HotelConfigModel data;
}