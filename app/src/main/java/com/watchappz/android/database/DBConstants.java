package com.watchappz.android.database;

/**
 * Created by
 * mRogach on 16.09.2015.
 */

public abstract class DBConstants {

    public static final int DATABASE_VERSION                   = 1;
    public static final String DATABASE_NAME                   = "appsManager";
    public static final String TABLE_APPS                      = "apps";
    public static final String KEY_ID                          = "_id";
    public static final String KEY_NAME                        = "nameApp";
    public static final String KEY_TODAY_COUNT                 = "todayCount";
    public static final String KEY_TOTAL_COUNT                 = "totalCount";
    public static final String KEY_IS_FAVOURITE                = "isFavourite";
    public static final String KEY_PACKAGE_NAME                = "packageName";
    public static final String KEY_DATE_USEGE                  = "dateUsege";
    public static final String KEY_IS_ABLE_TO_FAVORITE         = "isAbale";
    public static final String KEY_FAVORITE_COUNT              = "favoriteCount";
    public static final String KEY_APP_SIZE                    = "appSize";
    public static final String KEY_APP_LAST_USAGE              = "appLastUsage";
}
