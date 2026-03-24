package lk.grandhotel.stayease.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.network.models.HotelConfigResponse;
import lk.grandhotel.stayease.repository.BookingRepository;
import lk.grandhotel.stayease.repository.HotelConfigRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final BookingRepository bookingRepository;
    private final HotelConfigRepository configRepository;

    public final MutableLiveData<Integer> bookingCount = new MutableLiveData<>();
    public final MutableLiveData<HotelConfigResponse> hotelConfig = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        bookingRepository = new BookingRepository(application);
        configRepository = new HotelConfigRepository(application);
    }

    public void loadBookingCount() {
        MutableLiveData<List<BookingModel>> result = new MutableLiveData<>();
        MutableLiveData<String> err = new MutableLiveData<>();
        bookingRepository.getMyBookings(null, result, err);
        result.observeForever(bookings -> {
            if (bookings == null) return;
            int count = 0;
            for (BookingModel b : bookings) {
                if (!"CANCELLED".equals(b.status)) count++;
            }
            bookingCount.postValue(count);
        });
        err.observeForever(msg -> {
            if (msg != null) bookingCount.postValue(0);
        });
    }

    public void loadHotelConfig() {
        configRepository.getHotelConfig(hotelConfig, error);
    }
}