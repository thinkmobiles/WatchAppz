package com.watchappz.android.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by
 * mRogach on 24.09.2015.
 */

public class SetNullTodayCountAppsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Delete today values", Toast.LENGTH_LONG).show();
        Log.v("SetNullTodayCountAppsReceiver", "Delete");

    }
}
