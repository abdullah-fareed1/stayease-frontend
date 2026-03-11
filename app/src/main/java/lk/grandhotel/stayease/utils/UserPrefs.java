package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPrefs {

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME_USER, Context.MODE_PRIVATE);
    }

    public static void saveUser(Context context, String id, String name, String email, String phone) {
        getPrefs(context).edit()
                .putString(Constants.KEY_USER_ID, id)
                .putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_EMAIL, email)
                .putString(Constants.KEY_USER_PHONE, phone)
                .apply();
    }

    public static String getUserId(Context context) {
        return getPrefs(context).getString(Constants.KEY_USER_ID, null);
    }

    public static String getUserName(Context context) {
        return getPrefs(context).getString(Constants.KEY_USER_NAME, null);
    }

    public static String getUserEmail(Context context) {
        return getPrefs(context).getString(Constants.KEY_USER_EMAIL, null);
    }

    public static String getUserPhone(Context context) {
        return getPrefs(context).getString(Constants.KEY_USER_PHONE, null);
    }

    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}