package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class NetworkUtils {

    private static final MutableLiveData<Boolean> isOnlineLiveData = new MutableLiveData<>(true);
    private static ConnectivityManager.NetworkCallback networkCallback;

    public static MutableLiveData<Boolean> getIsOnlineLiveData() {
        return isOnlineLiveData;
    }

    public static void postOnlineStatus(boolean online) {
        isOnlineLiveData.postValue(online);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }

    public static void registerNetworkCallback(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        // Post the current state immediately on registration
        isOnlineLiveData.postValue(isOnline(context));

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isOnlineLiveData.postValue(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                // Check if there's still another active network
                isOnlineLiveData.postValue(isOnline(context));
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network,
                                              @NonNull NetworkCapabilities caps) {
                boolean hasInternet = caps.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        caps.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                isOnlineLiveData.postValue(hasInternet);
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        cm.registerNetworkCallback(request, networkCallback);
    }

    public static void unregisterNetworkCallback(Context context) {
        if (networkCallback == null) return;
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            try {
                cm.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) {}
        }
        networkCallback = null;
    }
}