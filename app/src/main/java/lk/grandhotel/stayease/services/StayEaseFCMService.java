package lk.grandhotel.stayease.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.activities.BookingDetailActivity;
import lk.grandhotel.stayease.activities.MainActivity;
import lk.grandhotel.stayease.activities.PaymentActivity;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.utils.Constants;
import lk.grandhotel.stayease.utils.TokenPrefs;
import lk.grandhotel.stayease.utils.UserPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StayEaseFCMService extends FirebaseMessagingService {

    private static final AtomicInteger notificationIdCounter = new AtomicInteger(1000);

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Save the token locally for use on next login
        TokenPrefs.saveFcmToken(this, token);

        // If user is already logged in, push the new token to the server immediately
        String userId = UserPrefs.getUserId(this);
        if (userId != null) {
            pushFcmTokenToServer(token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        String title = null;
        String body  = null;

        // Prefer notification payload, fall back to data payload
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body  = message.getNotification().getBody();
        }

        Map<String, String> data = message.getData();

        if (title == null) title = data.getOrDefault("title", getString(R.string.app_name));
        if (body  == null) body  = data.getOrDefault("body",  "You have a new notification.");

        PendingIntent pendingIntent = buildPendingIntent(data);
        showNotification(title, body, pendingIntent);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void pushFcmTokenToServer(String token) {
        Map<String, String> body = new HashMap<>();
        body.put("fcmToken", token);
        ApiClient.getService(this).updateFcmToken(body).enqueue(new Callback<ApiResponse>() {
            @Override public void onResponse(Call<ApiResponse> call, Response<ApiResponse> r) {}
            @Override public void onFailure(Call<ApiResponse> call, Throwable t) {}
        });
    }

    private PendingIntent buildPendingIntent(Map<String, String> data) {
        String type      = data.getOrDefault("type", "");
        String bookingId = data.getOrDefault("bookingId", null);

        Intent intent;

        switch (type) {
            case "booking":
                if (bookingId != null) {
                    intent = new Intent(this, BookingDetailActivity.class);
                    intent.putExtra("bookingId", bookingId);
                } else {
                    intent = buildMainIntent(R.id.nav_bookings);
                }
                break;

            case "payment":
                if (bookingId != null) {
                    // Route to the bookings tab so the user can see the updated booking
                    intent = buildMainIntent(R.id.nav_bookings);
                } else {
                    intent = buildMainIntent(R.id.nav_bookings);
                }
                break;

            default:
                intent = buildMainIntent(R.id.nav_home);
                break;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(
                this,
                notificationIdCounter.getAndIncrement(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private Intent buildMainIntent(int navTab) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigate_to", navTab);
        return intent;
    }

    private void showNotification(String title, String body, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nav_bookings)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationIdCounter.getAndIncrement(), builder.build());
    }
}