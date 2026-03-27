package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import androidx.lifecycle.MutableLiveData;

public class NetworkUtils {

    private static final MutableLiveData<Boolean> isOnlineLiveData = new MutableLiveData<>(true);

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
        android.net.Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }
}