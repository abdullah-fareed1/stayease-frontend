package lk.grandhotel.stayease.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import lk.grandhotel.stayease.local.db.AppDatabase;
import lk.grandhotel.stayease.local.db.entity.RoomEntity;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.RoomListResponse;
import lk.grandhotel.stayease.network.models.RoomModel;
import retrofit2.Response;

public class SyncWorker extends Worker {

    private final Gson gson = new Gson();

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Response<RoomListResponse> response = ApiClient.getService(getApplicationContext())
                    .getRooms(null, true, null, null, null, 1, 50)
                    .execute();

            if (response.isSuccessful()
                    && response.body() != null
                    && response.body().status
                    && response.body().data != null
                    && response.body().data.rooms != null) {

                List<RoomModel> rooms = response.body().data.rooms;
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                db.roomDao().clearAll();
                db.roomDao().insertAll(modelsToEntities(rooms));
                return Result.success();
            }
            return Result.retry();
        } catch (Exception e) {
            return Result.retry();
        }
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
}