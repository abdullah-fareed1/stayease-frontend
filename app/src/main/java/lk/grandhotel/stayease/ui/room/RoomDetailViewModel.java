package lk.grandhotel.stayease.ui.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.repository.RoomRepository;

public class RoomDetailViewModel extends AndroidViewModel {

    private final RoomRepository repository;

    public final MutableLiveData<RoomModel> room    = new MutableLiveData<>();
    public final MutableLiveData<String>    error   = new MutableLiveData<>();
    public final MutableLiveData<Boolean>   loading = new MutableLiveData<>(true);

    public RoomDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
    }

    public void loadRoom(String roomId) {
        loading.setValue(true);
        repository.getRoomById(roomId, room, error);
        room.observeForever(r -> { if (r != null) loading.setValue(false); });
    }
}