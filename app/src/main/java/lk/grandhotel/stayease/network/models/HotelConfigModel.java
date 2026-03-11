package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class HotelConfigModel {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("address")
    public String address;
    @SerializedName("phone")
    public String phone;
    @SerializedName("email")
    public String email;
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("description")
    public String description;
}