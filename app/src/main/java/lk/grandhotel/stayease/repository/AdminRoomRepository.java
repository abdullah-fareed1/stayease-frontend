package lk.grandhotel.stayease.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.AdminImageResponse;
import lk.grandhotel.stayease.network.models.AdminRoomDetailResponse;
import lk.grandhotel.stayease.network.models.AdminRoomListResponse;
import lk.grandhotel.stayease.network.models.AdminRoomModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRoomRepository {

    private final Context context;

    public AdminRoomRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getRooms(MutableLiveData<List<AdminRoomModel>> result,
                         MutableLiveData<String> error) {
        ApiClient.getService(context).getAdminRooms().enqueue(new Callback<AdminRoomListResponse>() {
            @Override
            public void onResponse(Call<AdminRoomListResponse> call, Response<AdminRoomListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.rooms);
                } else {
                    error.postValue(parseError(response, "Failed to load rooms."));
                }
            }
            @Override
            public void onFailure(Call<AdminRoomListResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void createRoom(String title, String category, String description,
                           double pricePerNight, int maxGuests, List<String> amenities,
                           MutableLiveData<AdminRoomModel> result,
                           MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("category", category);
        body.put("description", description);
        body.put("pricePerNight", pricePerNight);
        body.put("maxGuests", maxGuests);
        body.put("amenities", amenities);
        ApiClient.getService(context).createRoom(body).enqueue(new Callback<AdminRoomDetailResponse>() {
            @Override
            public void onResponse(Call<AdminRoomDetailResponse> call, Response<AdminRoomDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.room);
                } else {
                    error.postValue(parseError(response, "Failed to create room."));
                }
            }
            @Override
            public void onFailure(Call<AdminRoomDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void updateRoom(String roomId, Map<String, Object> fields,
                           MutableLiveData<AdminRoomModel> result,
                           MutableLiveData<String> error) {
        ApiClient.getService(context).updateRoom(roomId, fields).enqueue(new Callback<AdminRoomDetailResponse>() {
            @Override
            public void onResponse(Call<AdminRoomDetailResponse> call, Response<AdminRoomDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.room);
                } else {
                    error.postValue(parseError(response, "Failed to update room."));
                }
            }
            @Override
            public void onFailure(Call<AdminRoomDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void setAvailability(String roomId, String status,
                                MutableLiveData<AdminRoomModel> result,
                                MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("availabilityStatus", status);
        ApiClient.getService(context).setRoomAvailability(roomId, body).enqueue(new Callback<AdminRoomDetailResponse>() {
            @Override
            public void onResponse(Call<AdminRoomDetailResponse> call, Response<AdminRoomDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status
                        && response.body().data != null) {
                    result.postValue(response.body().data.room);
                } else {
                    error.postValue(parseError(response, "Failed to update availability."));
                }
            }
            @Override
            public void onFailure(Call<AdminRoomDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    public void uploadImage(String roomId, android.net.Uri imageUri, boolean isPrimary,
                            MutableLiveData<Boolean> result,
                            MutableLiveData<String> error) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) { error.postValue("Could not read image file."); return; }
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType == null) mimeType = "image/jpeg";
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", "room_image.jpg", requestFile);
            RequestBody primaryBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isPrimary));
            ApiClient.getService(context).uploadRoomImage(roomId, part, primaryBody)
                    .enqueue(new Callback<AdminImageResponse>() {
                        @Override
                        public void onResponse(Call<AdminImageResponse> call, Response<AdminImageResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().status) {
                                result.postValue(true);
                            } else {
                                error.postValue(parseError(response, "Failed to upload image."));
                            }
                        }
                        @Override
                        public void onFailure(Call<AdminImageResponse> call, Throwable t) {
                            error.postValue("Network error. Check your connection.");
                        }
                    });
        } catch (IOException e) {
            error.postValue("Failed to read image.");
        }
    }

    public void deleteImage(String roomId, String imageId,
                            MutableLiveData<Boolean> result,
                            MutableLiveData<String> error) {
        ApiClient.getService(context).deleteRoomImage(roomId, imageId).enqueue(new Callback<lk.grandhotel.stayease.network.models.ApiResponse>() {
            @Override
            public void onResponse(Call<lk.grandhotel.stayease.network.models.ApiResponse> call, Response<lk.grandhotel.stayease.network.models.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    result.postValue(true);
                } else {
                    error.postValue(parseError(response, "Failed to delete image."));
                }
            }
            @Override
            public void onFailure(Call<lk.grandhotel.stayease.network.models.ApiResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }

    private <T> String parseError(Response<T> response, String fallback) {
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(raw);
                return json.optString("message", fallback);
            }
        } catch (Exception ignored) {}
        return fallback;
    }
}