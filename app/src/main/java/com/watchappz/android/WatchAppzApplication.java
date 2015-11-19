package com.watchappz.android;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.watchappz.android.database.DBManager;

/**
 * Created by
 * mRogach on 15.09.2015.
 */
public class WatchAppzApplication extends Application {

    private static Context mContext;
    private static DBManager dbManager;

    public void onCreate(){
        super.onCreate();
        WatchAppzApplication.mContext = getApplicationContext();
        dbManager = new DBManager(mContext);
        dbManager.open();
    }

    public static Context getAppContext() {
        return WatchAppzApplication.mContext;
    }

    public static DBManager getDbManager() {
        return dbManager;
    }
}
