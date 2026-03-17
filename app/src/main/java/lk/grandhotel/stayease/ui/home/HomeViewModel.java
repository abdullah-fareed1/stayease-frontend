package lk.grandhotel.stayease.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.repository.RoomRepository;

public class HomeViewModel extends AndroidViewModel {

    private final RoomRepository repository;

    public final MutableLiveData<List<RoomModel>> rooms  = new MutableLiveData<>();
    public final MutableLiveData<String>           error  = new MutableLiveData<>();
    public final MutableLiveData<Boolean>          loading = new MutableLiveData<>(false);

    private String currentCategory = null;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
    }

    public void loadRooms(String category) {
        currentCategory = category;
        loading.setValue(true);
        repository.getRooms(category, true, rooms, error);
    }

    public void refresh() {
        loadRooms(currentCategory);
    }
}