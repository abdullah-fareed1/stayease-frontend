package lk.grandhotel.stayease.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import lk.grandhotel.stayease.network.models.AdminResponse;
import lk.grandhotel.stayease.network.models.DashboardResponse;
import lk.grandhotel.stayease.repository.AdminRepository;

public class AdminViewModel extends AndroidViewModel {

    private final AdminRepository repository;
    public final MutableLiveData<AdminResponse> loginResult = new MutableLiveData<>();
    public final MutableLiveData<String> authError = new MutableLiveData<>();
    public final MutableLiveData<Boolean> forgotResult = new MutableLiveData<>();
    public final MutableLiveData<Boolean> resetResult = new MutableLiveData<>();
    public final MutableLiveData<DashboardResponse> dashboardResult = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        repository = new AdminRepository(application);
    }

    public void login(String email, String password) {
        repository.login(email, password, loginResult, authError);
    }

    public void forgotPassword(String email) {
        repository.forgotPassword(email, forgotResult, authError);
    }

    public void resetPassword(String otp, String email, String newPassword) {
        repository.resetPassword(otp, email, newPassword, resetResult, authError);
    }

    public void loadDashboard() {
        repository.getDashboard(dashboardResult, authError);
    }
}