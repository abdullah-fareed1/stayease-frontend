package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.Map;
import lk.grandhotel.stayease.network.models.AdminRoomModel;
import lk.grandhotel.stayease.repository.AdminRoomRepository;

public class AdminRoomViewModel extends AndroidViewModel {

    private final AdminRoomRepository repository;

    public final MutableLiveData<List<AdminRoomModel>> rooms = new MutableLiveData<>();
    public final MutableLiveData<AdminRoomModel> roomResult = new MutableLiveData<>();
    public final MutableLiveData<Boolean> actionSuccess = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public AdminRoomViewModel(@NonNull Application application) {
        super(application);
        repository = new AdminRoomRepository(application);
    }

    public void loadRooms() {
        loading.setValue(true);
        repository.getRooms(rooms, error);
        rooms.observeForever(r -> { if (r != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void createRoom(String title, String category, String description,
                           double price, int maxGuests, List<String> amenities) {
        loading.setValue(true);
        repository.createRoom(title, category, description, price, maxGuests, amenities, roomResult, error);
        roomResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void updateRoom(String roomId, Map<String, Object> fields) {
        loading.setValue(true);
        repository.updateRoom(roomId, fields, roomResult, error);
        roomResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void setAvailability(String roomId, String status) {
        loading.setValue(true);
        repository.setAvailability(roomId, status, roomResult, error);
        roomResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void uploadImage(String roomId, Uri imageUri, boolean isPrimary) {
        loading.setValue(true);
        repository.uploadImage(roomId, imageUri, isPrimary, actionSuccess, error);
        actionSuccess.observeForever(s -> { if (s != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void deleteImage(String roomId, String imageId) {
        loading.setValue(true);
        repository.deleteImage(roomId, imageId, actionSuccess, error);
        actionSuccess.observeForever(s -> { if (s != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }
}