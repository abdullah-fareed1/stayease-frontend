package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class HotelConfigResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public HotelConfigData data;

    public static class HotelConfigData {
        @SerializedName("config")
        public Map<String, Object> config;
    }
}