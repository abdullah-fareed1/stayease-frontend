package lk.grandhotel.stayease;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.appcompat.app.AppCompatDelegate;

import lk.grandhotel.stayease.utils.AppPrefs;
import lk.grandhotel.stayease.utils.Constants;

public class StayEaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        applyDarkMode();
        createNotificationChannel();
    }

    private void applyDarkMode() {
        if (AppPrefs.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Hotel booking and payment notifications");
        channel.enableLights(true);
        channel.enableVibration(true);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}