package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.BookingDetailResponse;
import lk.grandhotel.stayease.network.models.BookingListResponse;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.network.models.CheckoutResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {

    private final Context context;

    public BookingRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void createBooking(String roomId, String checkIn, String checkOut,
                              int guestCount, String paymentType,
                              MutableLiveData<BookingDetailResponse> result,
                              MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("roomId", roomId);
        body.put("checkIn", checkIn);
        body.put("checkOut", checkOut);
        body.put("guestCount", guestCount);
        body.put("paymentType", paymentType);

        ApiClient.getService(context).createBooking(body).enqueue(new Callback<BookingDetailResponse>() {
            @Override
            public void onResponse(Call<BookingDetailResponse> call, Response<BookingDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    String msg = "Booking failed.";
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            msg = json.optString("message", msg);
                        } else if (response.body() != null && response.body().message != null) {
                            msg = response.body().message;
                        }
                    } catch (Exception ignored) {}
                    error.postValue(msg);
                }
            }

            @Override
            public void onFailure(Call<BookingDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void addToCart(String roomId, String checkIn, String checkOut,
                          int guestCount,
                          MutableLiveData<Boolean> result,
                          MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("roomId", roomId);
        body.put("checkIn", checkIn);
        body.put("checkOut", checkOut);
        body.put("guestCount", guestCount);

        ApiClient.getService(context).addToCart(body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(true);
                } else {
                    String msg = "Could not add to cart.";
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            msg = json.optString("message", msg);
                        }
                    } catch (Exception ignored) {}
                    error.postValue(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void getCart(MutableLiveData<CartResponse> result, MutableLiveData<String> error) {
        ApiClient.getService(context).getCart().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load cart.");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void removeCartItem(String itemId,
                               MutableLiveData<Boolean> result,
                               MutableLiveData<String> error) {
        ApiClient.getService(context).removeFromCart(itemId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(true);
                } else {
                    error.postValue("Failed to remove item.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void checkoutCart(String paymentType,
                             MutableLiveData<CheckoutResponse> result,
                             MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("paymentType", paymentType);

        ApiClient.getService(context).checkoutCart(body).enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    String msg = "Checkout failed.";
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            msg = json.optString("message", msg);
                        }
                    } catch (Exception ignored) {}
                    error.postValue(msg);
                }
            }

            @Override
            public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }
}