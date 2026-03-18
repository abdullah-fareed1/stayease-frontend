package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.BookingListResponse;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.repository.CartRepository;

public class CartViewModel extends AndroidViewModel {

    private final CartRepository repository;
    public final MutableLiveData<CartResponse>      cartData       = new MutableLiveData<>();
    public final MutableLiveData<String>            cartError      = new MutableLiveData<>();
    public final MutableLiveData<BookingListResponse> checkoutResult = new MutableLiveData<>();

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new CartRepository(application);
    }

    public void loadCart() {
        repository.getCart(cartData, cartError);
    }

    public void removeItem(String itemId) {
        repository.removeFromCart(itemId, cartData, cartError);
    }

    public void checkout() {
        repository.checkout(checkoutResult, cartError);
    }
}