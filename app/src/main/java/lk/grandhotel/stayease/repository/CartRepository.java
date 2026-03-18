package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.BookingListResponse;
import lk.grandhotel.stayease.network.models.CartResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private final Context context;

    public CartRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getCart(MutableLiveData<CartResponse> result,
                        MutableLiveData<String> error) {
        ApiClient.getService(context).getCart().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load cart");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void removeFromCart(String itemId,
                               MutableLiveData<CartResponse> result,
                               MutableLiveData<String> error) {
        ApiClient.getService(context).removeFromCart(itemId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to remove item");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                error.postValue("Network error");
            }
        });
    }

    public void checkout(MutableLiveData<BookingListResponse> result,
                         MutableLiveData<String> error) {
        ApiClient.getService(context).checkoutCart().enqueue(new Callback<BookingListResponse>() {
            @Override
            public void onResponse(Call<BookingListResponse> call, Response<BookingListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    String msg = "Checkout failed";
                    try {
                        if (response.errorBody() != null) {
                            org.json.JSONObject json = new org.json.JSONObject(response.errorBody().string());
                            msg = json.optString("message", msg);
                        }
                    } catch (Exception ignored) {}
                    error.postValue(msg);
                }
            }

            @Override
            public void onFailure(Call<BookingListResponse> call, Throwable t) {
                error.postValue("Network error");
            }
        });
    }
}