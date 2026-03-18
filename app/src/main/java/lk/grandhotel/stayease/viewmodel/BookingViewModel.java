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
    public final MutableLiveData<String> bookingError = new MutableLiveData<>();

    public BookingViewModel(@NonNull Application application) {
        super(application);
        repository = new BookingRepository(application);
    }

    public void createBooking(String roomId, String checkIn, String checkOut,
                              int guestCount, String paymentType) {
        repository.createBooking(roomId, checkIn, checkOut, guestCount, paymentType,
                bookingResult, bookingError);
    }
}