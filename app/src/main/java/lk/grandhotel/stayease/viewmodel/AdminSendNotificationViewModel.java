package lk.grandhotel.stayease.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import lk.grandhotel.stayease.repository.AdminSendNotificationRepository;

public class AdminSendNotificationViewModel extends AndroidViewModel {

    private final AdminSendNotificationRepository repository;

    public final MutableLiveData<String> sendResult = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public AdminSendNotificationViewModel(@NonNull Application application) {
        super(application);
        repository = new AdminSendNotificationRepository(application);
    }

    public void sendNotification(String title, String body, String targetType, String targetUserId) {
        loading.setValue(true);
        repository.sendNotification(title, body, targetType, targetUserId, sendResult, error);
        
        sendResult.observeForever(result -> {
            if (result != null) loading.setValue(false);
        });
        
        error.observeForever(err -> {
            if (err != null) loading.setValue(false);
        });
    }
}
