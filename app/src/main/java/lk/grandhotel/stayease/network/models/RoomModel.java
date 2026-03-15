package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoomModel {
    @SerializedName("id") public String id;
    @SerializedName("title") public String title;
    @SerializedName("description") public String description;
    @SerializedName("category") public String category;
    @SerializedName("pricePerNight") public double pricePerNight;
    @SerializedName("maxGuests") public int maxGuests;
    @SerializedName("amenities") public List<String> amenities;
    @SerializedName("images") public List<ImageModel> images;
    @SerializedName("primaryImage") public ImageModel primaryImage;
    @SerializedName("availabilityStatus") public String availabilityStatus;
    @SerializedName("averageRating") public Double averageRating;
    @SerializedName("reviewCount") public int reviewCount;
    @SerializedName("reviews") public List<ReviewModel> reviews;

    public static class ImageModel {
        @SerializedName("id") public String id;
        @SerializedName("url") public String url;
        @SerializedName("isPrimary") public boolean isPrimary;
    }
}