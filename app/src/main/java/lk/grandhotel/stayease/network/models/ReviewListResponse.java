package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewListResponse {
    @SerializedName("status")
    public boolean status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<ReviewModel> data;
}