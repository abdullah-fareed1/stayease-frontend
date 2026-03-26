package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.AdminBookingDetailResponse;
import lk.grandhotel.stayease.network.models.AdminBookingListResponse;
import lk.grandhotel.stayease.network.models.AdminBookingModel;
import lk.grandhotel.stayease.network.models.BookingModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBookingRepository {

    private final Context context;

    public AdminBookingRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getBookings(String status, Integer page,
                            MutableLiveData<List<AdminBookingModel>> result,
                            MutableLiveData<String> error) {
        ApiClient.getService(context).getAdminBookings(status, page).enqueue(new Callback<AdminBookingListResponse>() {
            @Override
            public void onResponse(Call<AdminBookingListResponse> call, Response<AdminBookingListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.bookings);
                } else {
                    error.postValue(parseError(response, "Failed to load bookings."));
                }
            }
            @Override
            public void onFailure(Call<AdminBookingListResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void getBookingById(String bookingId,
                               MutableLiveData<BookingModel> result,
                               MutableLiveData<String> error) {
        ApiClient.getService(context).getAdminBookingById(bookingId).enqueue(new Callback<AdminBookingDetailResponse>() {
            @Override
            public void onResponse(Call<AdminBookingDetailResponse> call, Response<AdminBookingDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.booking);
                } else {
                    error.postValue(parseError(response, "Failed to load booking details."));
                }
            }
            @Override
            public void onFailure(Call<AdminBookingDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void createWalkIn(String roomId, String checkIn, String checkOut, int guestCount,
                             String paymentType, String guestName, String guestEmail, String guestPhone,
                             MutableLiveData<BookingModel> result,
                             MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("roomId", roomId);
        body.put("checkIn", checkIn);
        body.put("checkOut", checkOut);
        body.put("guestCount", guestCount);
        body.put("paymentType", paymentType);
        body.put("guestName", guestName);
        body.put("guestEmail", guestEmail);
        body.put("guestPhone", guestPhone);
        ApiClient.getService(context).createWalkIn(body).enqueue(new Callback<AdminBookingDetailResponse>() {
            @Override
            public void onResponse(Call<AdminBookingDetailResponse> call, Response<AdminBookingDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.booking);
                } else {
                    error.postValue(parseError(response, "Failed to create walk-in booking."));
                }
            }
            @Override
            public void onFailure(Call<AdminBookingDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void updateBookingStatus(String bookingId, String status,
                                    MutableLiveData<BookingModel> result,
                                    MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        ApiClient.getService(context).updateBookingStatus(bookingId, body).enqueue(new Callback<AdminBookingDetailResponse>() {
            @Override
            public void onResponse(Call<AdminBookingDetailResponse> call, Response<AdminBookingDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.booking);
                } else {
                    error.postValue(parseError(response, "Failed to update booking status."));
                }
            }
            @Override
            public void onFailure(Call<AdminBookingDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    private String parseError(Response<?> response, String defaultMsg) {
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(raw);
                return json.optString("message", defaultMsg);
            }
        } catch (Exception e) {
            // ignore
        }
        return defaultMsg;
    }
}