package lk.grandhotel.stayease.utils;

import lk.grandhotel.stayease.BuildConfig;

public class Constants {
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final int CACHE_EXPIRY_MS = 30 * 60 * 1000;
    public static final String PREF_NAME_USER = "user_prefs";
    public static final String PREF_NAME_AUTH = "auth_prefs";
    public static final String PREF_NAME_SETTINGS = "settings_prefs";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_ONBOARDING_DONE = "onboarding_done";
    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String NOTIFICATION_CHANNEL_ID = "stayease_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "StayEase Notifications";
    public static final double HOTEL_LAT = 6.92726420169972;
    public static final double HOTEL_LNG = 79.84496133515718;
}