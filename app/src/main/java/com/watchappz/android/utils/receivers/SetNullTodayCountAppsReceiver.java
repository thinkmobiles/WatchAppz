package com.watchappz.android.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.watchappz.android.database.DBManager;

/**
 * Created by
 * mRogach on 24.09.2015.
 */

public class SetNullTodayCountAppsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        DBManager dbManager = new DBManager(context);
        dbManager.open();
        dbManager.updateTodayCount();
    }
}
