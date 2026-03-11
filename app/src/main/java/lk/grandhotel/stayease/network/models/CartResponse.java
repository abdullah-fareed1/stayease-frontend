package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public CartData data;

    public static class CartData {
        @SerializedName("id")
        public String id;
        @SerializedName("items")
        public List<CartItemModel> items;
    }
}