package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.AdminResponse;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.DashboardResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {

    private final Context context;

    public AdminRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void login(String email, String password,
                      MutableLiveData<AdminResponse> result,
                      MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        ApiClient.getService(context).adminLogin(body).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().status) {
                        result.postValue(response.body());
                    } else {
                        error.postValue(response.body().message != null ? response.body().message : "Login failed");
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            error.postValue(json.optString("message", "Login failed"));
                        } else {
                            error.postValue("Login failed");
                        }
                    } catch (Exception e) {
                        error.postValue("Login failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                error.postValue("Network error. Please check your connection.");
            }
        });
    }

    public void forgotPassword(String email,
                               MutableLiveData<Boolean> result,
                               MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        ApiClient.getService(context).adminForgotPassword(body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                result.postValue(true);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                error.postValue("Network error. Please check your connection.");
            }
        });
    }

    public void resetPassword(String otp, String email, String newPassword,
                              MutableLiveData<Boolean> result,
                              MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("otp", otp);
        body.put("email", email);
        body.put("newPassword", newPassword);
        ApiClient.getService(context).adminResetPassword(body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(true);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            error.postValue(json.optString("message", "Invalid OTP or expired"));
                        } else {
                            error.postValue("Invalid OTP or expired");
                        }
                    } catch (Exception e) {
                        error.postValue("Reset failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                error.postValue("Network error. Please check your connection.");
            }
        });
    }

    public void getDashboard(MutableLiveData<DashboardResponse> result,
                             MutableLiveData<String> error) {
        ApiClient.getService(context).getAdminDashboard().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load dashboard");
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                error.postValue("Network error. Please check your connection.");
            }
        });
    }
}