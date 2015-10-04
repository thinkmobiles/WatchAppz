package com.watchappz.android.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.watchappz.android.utils.AppInfoService;

/**
 * Created by
 * mRogach on 04.10.2015.
 */

public class PackageInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("AppPackage", "installed: " + intent.getDataString());
//        context.startService(new Intent(context, AppInfoService.class));
    }
}
