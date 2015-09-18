package com.watchappz.android.utils;

import java.util.Calendar;

/**
 * Created by
 * mRogach on 18.09.2015.
 */

public final class DateManager {

    public static long startOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long currentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
