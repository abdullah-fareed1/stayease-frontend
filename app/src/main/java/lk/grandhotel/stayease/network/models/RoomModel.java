package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoomModel {
    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("description")
    public String description;
    @SerializedName("type")
    public String type;
    @SerializedName("pricePerNight")
    public double pricePerNight;
    @SerializedName("maxGuests")
    public int maxGuests;
    @SerializedName("amenities")
    public List<String> amenities;
    @SerializedName("images")
    public List<String> images;
    @SerializedName("isAvailable")
    public boolean isAvailable;
    @SerializedName("averageRating")
    public double averageRating;
    @SerializedName("reviewCount")
    public int reviewCount;
}