package com.watchappz.android.system.models;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AppModel {

    private long id;
    private String mAppName;
    private long mAppUseTodayCount;
    private long mAppUseTotalCount;
    private int isFavourite;
    private String mAppPackageName;
    private long mDateUsege;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppPackageName() {
        return mAppPackageName;
    }

    public void setAppPackageName(final String _appPackageName) {
        this.mAppPackageName = _appPackageName;
    }

    public long getAppUseTotalCount() {
        return mAppUseTotalCount;
    }

    public void setAppUseTotalCount(final long _appUseTotalCount) {
        this.mAppUseTotalCount = _appUseTotalCount;
    }

    public int isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(final int _isFavourite) {
        this.isFavourite = _isFavourite;
    }

    public long getAppUseTodayCount() {
        return mAppUseTodayCount;
    }

    public void setAppUseTodayCount(final long _appUseTodayCount) {
        this.mAppUseTodayCount = _appUseTodayCount;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(final String _appName) {
        this.mAppName = _appName;
    }

    public long getDateUsege() {
        return mDateUsege;
    }

    public void setDateUsege(long mDateUsege) {
        this.mDateUsege = mDateUsege;
    }
}
