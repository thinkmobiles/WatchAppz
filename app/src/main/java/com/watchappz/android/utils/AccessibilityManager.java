package com.watchappz.android.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.watchappz.android.global.Constants;

import java.util.List;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class AccessibilityManager {

    private Activity mActivity;

    public AccessibilityManager(final Activity _activity) {
        mActivity = _activity;
    }

    public void startService() {
        if (!isAccessibilityEnabled(mActivity, "com.watchappz.android/.utils.AppInfoService")) {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            mActivity.startActivityForResult(intent, Constants.REQUEST_ENABLE);
        }
    }



    private boolean isAccessibilityEnabled(Context context, String id) {

        android.view.accessibility.AccessibilityManager am = (android.view.accessibility.AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }
}
