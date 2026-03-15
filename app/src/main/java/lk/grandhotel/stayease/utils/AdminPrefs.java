package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AdminPrefs {

    private static final String PREF_NAME = "admin_prefs";
    private static final String KEY_ID = "admin_id";
    private static final String KEY_NAME = "admin_name";
    private static final String KEY_EMAIL = "admin_email";
    private static final String KEY_ROLE = "admin_role";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveAdmin(Context context, String id, String name, String email, String role) {
        getPrefs(context).edit()
                .putString(KEY_ID, id)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public static String getAdminName(Context context) {
        return getPrefs(context).getString(KEY_NAME, null);
    }

    public static String getAdminEmail(Context context) {
        return getPrefs(context).getString(KEY_EMAIL, null);
    }

    public static String getAdminRole(Context context) {
        return getPrefs(context).getString(KEY_ROLE, null);
    }

    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}