package com.watchappz.android;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by
 * mRogach on 15.09.2015.
 */
public class WatchAppzApplication extends Application{

    private static Context mContext;

    public void onCreate(){
        super.onCreate();
        WatchAppzApplication.mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return WatchAppzApplication.mContext;
    }
}
