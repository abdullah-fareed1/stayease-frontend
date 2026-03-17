package lk.grandhotel.stayease.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lk.grandhotel.stayease.local.db.AppDatabase;
import lk.grandhotel.stayease.local.db.entity.RoomEntity;
import lk.grandhotel.stayease.local.db.entity.SearchHistoryEntity;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.RoomDetailResponse;
import lk.grandhotel.stayease.network.models.RoomListResponse;
import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomRepository {

    private final Context context;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    public RoomRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getRooms(String category, Boolean available,
                         MutableLiveData<List<RoomModel>> result,
                         MutableLiveData<String> error) {
        executor.execute(() -> {
            long minAge = System.currentTimeMillis() - Constants.CACHE_EXPIRY_MS;
            List<RoomEntity> cached = AppDatabase.getInstance(context).roomDao().getCachedRooms(minAge);
            if (!cached.isEmpty()) {
                result.postValue(entitiesToModels(cached));
            }
        });

        ApiClient.getService(context)
                .getRooms(category, available, null, null, null, 1, 50)
                .enqueue(new Callback<RoomListResponse>() {
                    @Override
                    public void onResponse(Call<RoomListResponse> call, Response<RoomListResponse> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().status
                                && response.body().data != null
                                && response.body().data.rooms != null) {
                            List<RoomModel> rooms = response.body().data.rooms;
                            result.postValue(rooms);
                            executor.execute(() -> {
                                AppDatabase db = AppDatabase.getInstance(context);
                                db.roomDao().clearAll();
                                db.roomDao().insertAll(modelsToEntities(rooms));
                            });
                        } else if (result.getValue() == null) {
                            error.postValue("Failed to load rooms.");
                        }
                    }

                    @Override
                    public void onFailure(Call<RoomListResponse> call, Throwable t) {
                        if (result.getValue() == null) {
                            error.postValue("Network error. Check your connection.");
                        }
                    }
                });
    }

    public void saveSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) return;
        executor.execute(() -> {
            SearchHistoryEntity entry = new SearchHistoryEntity();
            entry.query = query.trim();
            entry.timestamp = System.currentTimeMillis();
            AppDatabase.getInstance(context).searchHistoryDao().insert(entry);
        });
    }

    public void getSearchHistory(MutableLiveData<List<String>> result) {
        executor.execute(() -> {
            List<SearchHistoryEntity> history = AppDatabase.getInstance(context).searchHistoryDao().getRecent();
            List<String> queries = new ArrayList<>();
            for (SearchHistoryEntity e : history) queries.add(e.query);
            result.postValue(queries);
        });
    }

    public void clearSearchHistory() {
        executor.execute(() -> AppDatabase.getInstance(context).searchHistoryDao().clearAll());
    }

    private List<RoomModel> entitiesToModels(List<RoomEntity> entities) {
        List<RoomModel> models = new ArrayList<>();
        for (RoomEntity e : entities) {
            RoomModel m = new RoomModel();
            m.id = e.id;
            m.title = e.title;
            m.description = e.description;
            m.category = e.type;
            m.pricePerNight = e.pricePerNight;
            m.maxGuests = e.maxGuests;
            m.averageRating = e.averageRating;
            m.reviewCount = e.reviewCount;
            m.availabilityStatus = e.isAvailable ? "AVAILABLE" : "TEMP_UNAVAILABLE";
            if (e.amenitiesJson != null) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                m.amenities = gson.fromJson(e.amenitiesJson, listType);
            }
            if (e.imagesJson != null) {
                Type listType = new TypeToken<List<RoomModel.ImageModel>>() {}.getType();
                m.images = gson.fromJson(e.imagesJson, listType);
            }
            if (e.primaryImageJson != null) {
                m.primaryImage = gson.fromJson(e.primaryImageJson, RoomModel.ImageModel.class);
            } else if (m.images != null && !m.images.isEmpty()) {
                for (RoomModel.ImageModel img : m.images) {
                    if (img.isPrimary) { m.primaryImage = img; break; }
                }
                if (m.primaryImage == null) m.primaryImage = m.images.get(0);
            }
            models.add(m);
        }
        return models;
    }

    private List<RoomEntity> modelsToEntities(List<RoomModel> models) {
        List<RoomEntity> entities = new ArrayList<>();
        for (RoomModel m : models) {
            RoomEntity e = new RoomEntity();
            e.id = m.id;
            e.title = m.title;
            e.description = m.description;
            e.type = m.category;
            e.pricePerNight = m.pricePerNight;
            e.maxGuests = m.maxGuests;
            e.averageRating = m.averageRating != null ? m.averageRating : 0;
            e.reviewCount = m.reviewCount;
            e.isAvailable = "AVAILABLE".equals(m.availabilityStatus);
            e.amenitiesJson = m.amenities != null ? gson.toJson(m.amenities) : null;
            e.imagesJson = m.images != null ? gson.toJson(m.images) : null;
            e.primaryImageJson = m.primaryImage != null ? gson.toJson(m.primaryImage) : null;
            e.cachedAt = System.currentTimeMillis();
            entities.add(e);
        }
        return entities;
    }

    public void getRoomById(String roomId,
                            MutableLiveData<RoomModel> result,
                            MutableLiveData<String> error) {
        ApiClient.getService(context).getRoomById(roomId).enqueue(new Callback<RoomDetailResponse>() {
            @Override
            public void onResponse(Call<RoomDetailResponse> call, Response<RoomDetailResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().status
                        && response.body().data != null
                        && response.body().data.room != null) {
                    result.postValue(response.body().data.room);
                } else {
                    error.postValue("Room not found.");
                }
            }

            @Override
            public void onFailure(Call<RoomDetailResponse> call, Throwable t) {
                error.postValue("Network error. Check your connection.");
            }
        });
    }
}