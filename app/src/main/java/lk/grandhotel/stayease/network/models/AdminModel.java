package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class AdminModel {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;
    @SerializedName("role")
    public String role;
}