package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.HotelConfigResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelConfigRepository {

    private final Context context;

    public HotelConfigRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getHotelConfig(MutableLiveData<HotelConfigResponse> result,
                               MutableLiveData<String> error) {
        ApiClient.getService(context).getHotelConfig().enqueue(new Callback<HotelConfigResponse>() {
            @Override
            public void onResponse(Call<HotelConfigResponse> call, Response<HotelConfigResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load hotel info.");
                }
            }

            @Override
            public void onFailure(Call<HotelConfigResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }
}