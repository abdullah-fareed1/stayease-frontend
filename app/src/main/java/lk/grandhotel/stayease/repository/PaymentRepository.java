package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.PaymentInitiateResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {

    private final Context context;

    public PaymentRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void initiatePayment(String bookingId, String paymentType,
                                MutableLiveData<PaymentInitiateResponse> result,
                                MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("bookingId", bookingId);
        body.put("paymentType", paymentType);

        ApiClient.getService(context).initiatePayment(body).enqueue(new Callback<PaymentInitiateResponse>() {
            @Override
            public void onResponse(Call<PaymentInitiateResponse> call, Response<PaymentInitiateResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    String msg = "Payment initiation failed.";
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
            public void onFailure(Call<PaymentInitiateResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }
}