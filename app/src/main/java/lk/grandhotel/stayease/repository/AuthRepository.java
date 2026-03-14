package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.AuthResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final Context context;

    public AuthRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void login(String email, String password, String fcmToken, MutableLiveData<AuthResponse> result, MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        if (fcmToken != null) body.put("fcmToken", fcmToken);

        ApiClient.getService(context).login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
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
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                error.postValue("Network error. Please check your connection.");
            }
        });
    }

    public void register(String name, String email, String password, String phone, MutableLiveData<AuthResponse> result, MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);
        if (phone != null && !phone.isEmpty()) body.put("phone", phone);

        ApiClient.getService(context).register(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().status) {
                        result.postValue(response.body());
                    } else {
                        error.postValue(response.body().message != null ? response.body().message : "Registration failed");
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            org.json.JSONObject json = new org.json.JSONObject(raw);
                            error.postValue(json.optString("message", "Registration failed"));
                        } else {
                            error.postValue("Registration failed");
                        }
                    } catch (Exception e) {
                        error.postValue("Registration failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                error.postValue("Network error. Please check your connection.");
            }
        });
    }

    public void forgotPassword(String email, MutableLiveData<Boolean> result, MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        ApiClient.getService(context).forgotPassword(body).enqueue(new Callback<ApiResponse>() {
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

    public void resetPassword(String otp, String email, String newPassword, MutableLiveData<Boolean> result, MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("otp", otp);
        body.put("email", email);
        body.put("newPassword", newPassword);

        ApiClient.getService(context).resetPassword(body).enqueue(new Callback<ApiResponse>() {
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
}