package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class TokenPrefs {

    private static SharedPreferences getPrefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    Constants.PREF_NAME_AUTH,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            return context.getSharedPreferences(Constants.PREF_NAME_AUTH, Context.MODE_PRIVATE);
        }
    }

    public static void saveTokens(Context context, String accessToken, String refreshToken) {
        getPrefs(context).edit()
                .putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                .putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public static String getAccessToken(Context context) {
        return getPrefs(context).getString(Constants.KEY_ACCESS_TOKEN, null);
    }

    public static String getRefreshToken(Context context) {
        return getPrefs(context).getString(Constants.KEY_REFRESH_TOKEN, null);
    }

    public static void clearTokens(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    public static boolean hasTokens(Context context) {
        return getAccessToken(context) != null;
    }

    public static void saveFcmToken(Context context, String fcmToken) {
        getPrefs(context).edit()
                .putString(Constants.KEY_FCM_TOKEN, fcmToken)
                .apply();
    }

    public static String getFcmToken(Context context) {
        return getPrefs(context).getString(Constants.KEY_FCM_TOKEN, null);
    }
}