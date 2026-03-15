package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class AdminTokenPrefs {

    private static final String PREF_NAME = "admin_auth_prefs";
    private static final String KEY_ACCESS = "admin_access_token";
    private static final String KEY_REFRESH = "admin_refresh_token";

    private static SharedPreferences getPrefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context, PREF_NAME, masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveTokens(Context context, String accessToken, String refreshToken) {
        getPrefs(context).edit()
                .putString(KEY_ACCESS, accessToken)
                .putString(KEY_REFRESH, refreshToken)
                .apply();
    }

    public static String getAccessToken(Context context) {
        return getPrefs(context).getString(KEY_ACCESS, null);
    }

    public static String getRefreshToken(Context context) {
        return getPrefs(context).getString(KEY_REFRESH, null);
    }

    public static void clearTokens(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    public static boolean hasTokens(Context context) {
        return getAccessToken(context) != null;
    }
}