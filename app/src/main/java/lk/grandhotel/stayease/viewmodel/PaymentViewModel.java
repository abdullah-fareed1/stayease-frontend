package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.PaymentInitiateResponse;
import lk.grandhotel.stayease.repository.PaymentRepository;

public class PaymentViewModel extends AndroidViewModel {

    private final PaymentRepository repository;

    public final MutableLiveData<PaymentInitiateResponse> paymentInitResult = new MutableLiveData<>();
    public final MutableLiveData<String> paymentError = new MutableLiveData<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        repository = new PaymentRepository(application);
    }

    public void initiatePayment(String bookingId, String paymentType) {
        repository.initiatePayment(bookingId, paymentType, paymentInitResult, paymentError);
    }
}