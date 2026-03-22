package lk.grandhotel.stayease.ui.map;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.HotelConfigResponse;
import lk.grandhotel.stayease.repository.HotelConfigRepository;

public class MapViewModel extends AndroidViewModel {

    private final HotelConfigRepository repository;

    public final MutableLiveData<HotelConfigResponse> hotelConfig = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = new HotelConfigRepository(application);
    }

    public void loadHotelConfig() {
        repository.getHotelConfig(hotelConfig, error);
    }
}