package com.watchappz.android.system.models;

import java.io.Serializable;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AppModel implements Serializable {

    private long id;
    private String mAppName;
    private long mAppUseTodayCount;
    private long mAppUseTotalCount;
    private int isFavourite;
    private String mAppPackageName;
    private long mDateUsege;
    private long mAppLastUsege;
    private int isAbleToFavorite;
    private long mFavoriteCount;
    private long mAppSize;
    private int mAppDeleted;
    private long mAppTimeSpent;
    private int mAppPosition;

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

    public int getIsAbleToFavorite() {
        return isAbleToFavorite;
    }

    public void setIsAbleToFavorite(int isAbleToFavorite) {
        this.isAbleToFavorite = isAbleToFavorite;
    }

    public long getFavoriteCount() {
        return mFavoriteCount;
    }

    public void setFavoriteCount(long mFavoriteCount) {
        this.mFavoriteCount = mFavoriteCount;
    }

    public long getAppSize() {
        return mAppSize;
    }

    public void setAppSize(long mAppSize) {
        this.mAppSize = mAppSize;
    }

    public int getAppDeleted() {
        return mAppDeleted;
    }

    public void setAppDeleted(int mAppDeleted) {
        this.mAppDeleted = mAppDeleted;
    }

    public long getAppLastUsege() {
        return mAppLastUsege;
    }

    public void setAppLastUsege(long mAppLastUsege) {
        this.mAppLastUsege = mAppLastUsege;
    }

    public long getAppTimeSpent() {
        return mAppTimeSpent;
    }

    public void setAppTimeSpent(long mAppTimeSpent) {
        this.mAppTimeSpent = mAppTimeSpent;
    }

    public int getAppPosition() {
        return mAppPosition;
    }

    public void setAppPosition(int mAppPosition) {
        this.mAppPosition = mAppPosition;
    }
}
