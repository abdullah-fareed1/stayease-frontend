package lk.grandhotel.stayease.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import lk.grandhotel.stayease.services.SyncWorker;
import lk.grandhotel.stayease.utils.NetworkUtils;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean online = NetworkUtils.isOnline(context);
        NetworkUtils.postOnlineStatus(online);

        if (online) {
            OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                    .setConstraints(new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build())
                    .build();
            WorkManager.getInstance(context).enqueue(syncRequest);
        }
    }
}