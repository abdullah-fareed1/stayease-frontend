package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.network.models.CheckoutResponse;
import lk.grandhotel.stayease.repository.BookingRepository;

public class CartViewModel extends AndroidViewModel {

    private final BookingRepository repository;

    public final MutableLiveData<CartResponse> cartResult = new MutableLiveData<>();
    public final MutableLiveData<CheckoutResponse> checkoutResult = new MutableLiveData<>();
    public final MutableLiveData<Boolean> removeResult = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new BookingRepository(application);
    }

    public void loadCart() {
        loading.setValue(true);
        repository.getCart(cartResult, error);
        cartResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void removeItem(String itemId) {
        repository.removeCartItem(itemId, removeResult, error);
    }

    public void checkout(String paymentType) {
        loading.setValue(true);
        repository.checkoutCart(paymentType, checkoutResult, error);
        checkoutResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }
}