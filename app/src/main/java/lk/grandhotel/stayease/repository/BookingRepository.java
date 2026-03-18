package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.BookingDetailResponse;
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
                    String msg = "Booking failed";
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
            public void onFailure(Call<BookingDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }
}