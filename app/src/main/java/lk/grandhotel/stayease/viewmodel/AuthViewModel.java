package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.AuthResponse;
import lk.grandhotel.stayease.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    public final MutableLiveData<AuthResponse> authResult = new MutableLiveData<>();
    public final MutableLiveData<String> authError = new MutableLiveData<>();
    public final MutableLiveData<Boolean> forgotResult = new MutableLiveData<>();
    public final MutableLiveData<Boolean> resetResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public void login(String email, String password, String fcmToken) {
        repository.login(email, password, fcmToken, authResult, authError);
    }

    public void register(String name, String email, String password, String phone) {
        repository.register(name, email, password, phone, authResult, authError);
    }

    public void forgotPassword(String email) {
        repository.forgotPassword(email, forgotResult, authError);
    }

    public void resetPassword(String otp, String email, String newPassword) {
        repository.resetPassword(otp, email, newPassword, resetResult, authError);
    }

    public void clearAuthResult() {
        authResult.setValue(null);
        authError.setValue(null);
    }
}