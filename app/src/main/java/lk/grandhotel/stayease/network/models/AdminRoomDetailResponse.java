package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AdminRoomDetailResponse {
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public AdminRoomDetailData data;

    public static class AdminRoomDetailData {
        @SerializedName("room") public AdminRoomModel room;
    }
}