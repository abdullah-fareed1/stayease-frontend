package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class ReviewModel {
    @SerializedName("id")
    public String id;
    @SerializedName("userId")
    public String userId;
    @SerializedName("roomId")
    public String roomId;
    @SerializedName("bookingId")
    public String bookingId;
    @SerializedName("rating")
    public int rating;
    @SerializedName("comment")
    public String comment;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("user")
    public UserModel user;
}