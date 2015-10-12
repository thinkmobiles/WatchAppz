package com.watchappz.android.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.MessageEvent;
import com.watchappz.android.utils.AppInfoService;

import de.greenrobot.event.EventBus;

/**
 * Created by
 * mRogach on 04.10.2015.
 */

public class PackageInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("AppPackage", "installed: " + intent.getDataString());
        EventBus.getDefault().post(new MessageEvent());
        Log.v("setServiceInfo", "onReceive");
    }
}
