package com.watchappz.android.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.watchappz.android.database.DBManager;


/**
 * Created by
 * mRogach on 24.09.2015.
 */

public final class PackageRemoveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DBManager mDbManager = new DBManager(context);
        mDbManager.open();
        Log.v("uninstalled", intent.getDataString());
        if (intent.getDataString() != null && !intent.getDataString().equals("package:com.watchappz.android")) {
            Log.v("uninstalled", intent.getDataString().split(":")[1]);
            mDbManager.deleteAppByPackage(intent.getDataString().split(":")[1]);
        }
    }
}
