package lk.grandhotel.stayease.network.models;

import com.google.gson.annotations.SerializedName;

public class PaginationMeta {
    @SerializedName("page") public int page;
    @SerializedName("pageSize") public int pageSize;
    @SerializedName("total") public int total;
    @SerializedName("totalPages") public int totalPages;
}