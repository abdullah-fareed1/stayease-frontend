package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import lk.grandhotel.stayease.network.models.AdminBookingModel;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.repository.AdminBookingRepository;

public class AdminBookingViewModel extends AndroidViewModel {

    private final AdminBookingRepository repository;

    public final MutableLiveData<List<AdminBookingModel>> bookings = new MutableLiveData<>();
    public final MutableLiveData<BookingModel> bookingResult = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public AdminBookingViewModel(@NonNull Application application) {
        super(application);
        repository = new AdminBookingRepository(application);
    }

    public void loadBookings(String status) {
        loading.setValue(true);
        repository.getBookings(status, null, bookings, error);
        bookings.observeForever(b -> { if (b != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void loadBookingById(String bookingId) {
        loading.setValue(true);
        repository.getBookingById(bookingId, bookingResult, error);
        bookingResult.observeForever(b -> { if (b != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void createWalkIn(String roomId, String checkIn, String checkOut, int guestCount,
                             String paymentType, String guestName, String guestEmail, String guestPhone) {
        loading.setValue(true);
        repository.createWalkIn(roomId, checkIn, checkOut, guestCount, paymentType,
                               guestName, guestEmail, guestPhone, bookingResult, error);
        bookingResult.observeForever(b -> { if (b != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void updateBookingStatus(String bookingId, String status) {
        loading.setValue(true);
        repository.updateBookingStatus(bookingId, status, bookingResult, error);
        bookingResult.observeForever(b -> { if (b != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }
}