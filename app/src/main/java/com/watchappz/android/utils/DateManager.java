package com.watchappz.android.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.watchappz.android.utils.receivers.SetNullTodayCountAppsReceiver;

import java.util.Calendar;

/**
 * Created by
 * mRogach on 18.09.2015.
 */

public final class DateManager {

    private Context mContext;

    public DateManager(final Context _context) {
        mContext = _context;
    }

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

    public void startAlarmManager() {
        Intent todayCountIntent = new Intent(mContext, SetNullTodayCountAppsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, todayCountIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getEndOfDay(), AlarmManager.INTERVAL_DAY,  pendingIntent);
    }

    private long getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTimeInMillis();
    }
}
