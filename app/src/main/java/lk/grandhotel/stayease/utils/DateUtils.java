package lk.grandhotel.stayease.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public static String toIsoString(long ms) {
        return ISO_FORMAT.format(new Date(ms));
    }

    public static String toDisplayString(long ms) {
        return DISPLAY_FORMAT.format(new Date(ms));
    }

    public static String toDisplayString(String isoDate) {
        if (isoDate == null) return "";
        try {
            Date d = ISO_FORMAT.parse(isoDate);
            return d != null ? DISPLAY_FORMAT.format(d) : isoDate;
        } catch (Exception e) {
            if (isoDate.length() >= 10) return isoDate.substring(0, 10);
            return isoDate;
        }
    }

    public static int calculateNights(long checkInMs, long checkOutMs) {
        long diff = checkOutMs - checkInMs;
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }
}