package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;
    @SerializedName("phone")
    public String phone;
}