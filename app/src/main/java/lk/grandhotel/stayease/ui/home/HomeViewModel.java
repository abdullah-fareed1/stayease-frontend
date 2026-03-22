package lk.grandhotel.stayease.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.repository.BookingRepository;
import lk.grandhotel.stayease.repository.RoomRepository;

public class HomeViewModel extends AndroidViewModel {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public final MutableLiveData<List<RoomModel>> rooms   = new MutableLiveData<>();
    public final MutableLiveData<String>           error   = new MutableLiveData<>();
    public final MutableLiveData<Boolean>          loading = new MutableLiveData<>(false);
    public final MutableLiveData<Integer>          cartCount = new MutableLiveData<>(0);

    private String currentCategory = null;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        roomRepository    = new RoomRepository(application);
        bookingRepository = new BookingRepository(application);
    }

    public void loadRooms(String category) {
        currentCategory = category;
        loading.setValue(true);
        roomRepository.getRooms(category, true, rooms, error);
    }

    public void refresh() {
        loadRooms(currentCategory);
    }

    public void loadCartCount() {
        MutableLiveData<CartResponse> cartResult = new MutableLiveData<>();
        MutableLiveData<String> cartError = new MutableLiveData<>();
        bookingRepository.getCart(cartResult, cartError);
        cartResult.observeForever(response -> {
            if (response != null && response.data != null && response.data.cart != null
                    && response.data.cart.items != null) {
                cartCount.postValue(response.data.cart.items.size());
            } else {
                cartCount.postValue(0);
            }
        });
    }
}