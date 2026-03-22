package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.BookingDetailResponse;
import lk.grandhotel.stayease.network.models.BookingListResponse;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.repository.BookingRepository;

public class BookingsViewModel extends AndroidViewModel {

    private final BookingRepository repository;

    public final MutableLiveData<List<BookingModel>> bookings      = new MutableLiveData<>();
    public final MutableLiveData<BookingModel>       bookingDetail  = new MutableLiveData<>();
    public final MutableLiveData<Boolean>            cancelSuccess  = new MutableLiveData<>();
    public final MutableLiveData<Boolean>            reviewSuccess  = new MutableLiveData<>();
    public final MutableLiveData<String>             error          = new MutableLiveData<>();
    public final MutableLiveData<Boolean>            loading        = new MutableLiveData<>(false);

    public BookingsViewModel(@NonNull Application application) {
        super(application);
        repository = new BookingRepository(application);
    }

    public void loadAllBookings() {
        loading.setValue(true);
        repository.getMyBookings(null, bookings, error);
        bookings.observeForever(b -> loading.setValue(false));
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void loadBookingDetail(String bookingId) {
        loading.setValue(true);
        repository.getBookingById(bookingId, bookingDetail, error);
        bookingDetail.observeForever(b -> { if (b != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void cancelBooking(String bookingId) {
        loading.setValue(true);
        repository.cancelBooking(bookingId, cancelSuccess, error);
        cancelSuccess.observeForever(s -> { if (s != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }

    public void submitReview(String bookingId, int rating, String comment) {
        loading.setValue(true);
        repository.submitReview(bookingId, rating, comment, reviewSuccess, error);
        reviewSuccess.observeForever(s -> { if (s != null) loading.setValue(false); });
        error.observeForever(e -> { if (e != null) loading.setValue(false); });
    }
}