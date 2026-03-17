package lk.grandhotel.stayease.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.repository.RoomRepository;

public class SearchViewModel extends AndroidViewModel {

    private final RoomRepository repository;

    public final MutableLiveData<List<RoomModel>> results     = new MutableLiveData<>();
    public final MutableLiveData<String>           error       = new MutableLiveData<>();
    public final MutableLiveData<Boolean>          loading     = new MutableLiveData<>(false);
    public final MutableLiveData<List<String>>     history     = new MutableLiveData<>();

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
    }

    public void search(String category, Boolean availableOnly) {
        loading.setValue(true);
        repository.getRooms(category, availableOnly, results, error);
        results.observeForever(r -> loading.setValue(false));
    }

    public void loadHistory() {
        repository.getSearchHistory(history);
    }

    public void saveQuery(String query) {
        repository.saveSearchQuery(query);
        loadHistory();
    }

    public void clearHistory() {
        repository.clearSearchHistory();
        history.setValue(null);
    }
}