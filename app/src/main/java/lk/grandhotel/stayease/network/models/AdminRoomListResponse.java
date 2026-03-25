package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminRoomListResponse {
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public AdminRoomListData data;

    public static class AdminRoomListData {
        @SerializedName("rooms") public List<AdminRoomModel> rooms;
    }
}