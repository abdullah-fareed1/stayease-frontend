package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminRoomModel {
    @SerializedName("id") public String id;
    @SerializedName("title") public String title;
    @SerializedName("category") public String category;
    @SerializedName("description") public String description;
    @SerializedName("pricePerNight") public String pricePerNight;
    @SerializedName("maxGuests") public int maxGuests;
    @SerializedName("amenities") public List<String> amenities;
    @SerializedName("availabilityStatus") public String availabilityStatus;
    @SerializedName("images") public List<RoomModel.ImageModel> images;
    @SerializedName("primaryImage") public RoomModel.ImageModel primaryImage;
    @SerializedName("averageRating") public Double averageRating;
    @SerializedName("reviewCount") public int reviewCount;

    public double getPriceDouble() {
        try { return pricePerNight != null ? Double.parseDouble(pricePerNight) : 0.0; }
        catch (NumberFormatException e) { return 0.0; }
    }
}