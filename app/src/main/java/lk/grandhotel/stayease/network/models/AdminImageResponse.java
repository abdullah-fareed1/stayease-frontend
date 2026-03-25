package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AdminImageResponse {
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public ImageData data;

    public static class ImageData {
        @SerializedName("image") public RoomModel.ImageModel image;
    }
}