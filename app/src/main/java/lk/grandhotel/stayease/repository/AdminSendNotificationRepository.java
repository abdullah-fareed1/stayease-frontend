package lk.grandhotel.stayease.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSendNotificationRepository {

    private final Context context;

    public AdminSendNotificationRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void sendNotification(String title, String body, String targetType, String targetUserId,
                                 MutableLiveData<String> result,
                                 MutableLiveData<String> error) {
        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("body", body);
        requestBody.put("targetType", targetType);
        
        // Add userId only if target type is SPECIFIC
        if ("SPECIFIC".equals(targetType) && targetUserId != null && !targetUserId.isEmpty()) {
            requestBody.put("targetUserId", targetUserId);
        }

        // Make API call
        ApiClient.getService(context).sendNotification(requestBody)
            .enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().status) {
                        // Success - return the message
                        result.postValue(response.body().message);
                    } else {
                        // Error response from server
                        error.postValue(parseError(response, "Failed to send notification."));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Network error
                    error.postValue("Network error. Check your connection.");
                }
            });
    }

    // Helper method to parse error messages from response body
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
