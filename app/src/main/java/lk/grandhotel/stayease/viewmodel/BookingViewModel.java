package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.BookingDetailResponse;
import lk.grandhotel.stayease.repository.BookingRepository;

public class BookingViewModel extends AndroidViewModel {

    private final BookingRepository repository;

    public final MutableLiveData<BookingDetailResponse> bookingResult = new MutableLiveData<>();
    public final MutableLiveData<Boolean> cartAddResult = new MutableLiveData<>();
    public final MutableLiveData<String> bookingError = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public BookingViewModel(@NonNull Application application) {
        super(application);
        repository = new BookingRepository(application);
    }

    public void createBooking(String roomId, String checkIn, String checkOut,
                              int guestCount, String paymentType) {
        loading.setValue(true);
        repository.createBooking(roomId, checkIn, checkOut, guestCount, paymentType,
                bookingResult, bookingError);
        bookingResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        bookingError.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void addToCart(String roomId, String checkIn, String checkOut, int guestCount) {
        loading.setValue(true);
        repository.addToCart(roomId, checkIn, checkOut, guestCount, cartAddResult, bookingError);
        cartAddResult.observeForever(r -> { if (r != null) loading.setValue(false); });
        bookingError.observeForever(e -> { if (e != null) loading.setValue(false); });
    }
}