package lk.grandhotel.stayease.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean isDarkMode(Context context) {
        return getPrefs(context).getBoolean(Constants.KEY_DARK_MODE, false);
    }

    public static void setDarkMode(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(Constants.KEY_DARK_MODE, enabled).apply();
    }

    public static boolean isOnboardingDone(Context context) {
        return getPrefs(context).getBoolean(Constants.KEY_ONBOARDING_DONE, false);
    }

    public static void setOnboardingDone(Context context) {
        getPrefs(context).edit().putBoolean(Constants.KEY_ONBOARDING_DONE, true).apply();
    }
}