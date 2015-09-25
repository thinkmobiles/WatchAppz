package com.watchappz.android.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.watchappz.android.database.DBManager;
import com.watchappz.android.system.models.AppModel;

import java.util.Calendar;
import java.util.List;

/**
 * Created by
 * mRogach on 16.09.2015.
 */

public final class AppInfoService extends AccessibilityService {

    private DBManager dbManager;
    private AndroidManager mAndroidManager;
    private DateManager dateManager;
    private String mPackageName;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!TextUtils.isEmpty(getEventText(accessibilityEvent))) {
            if (!accessibilityEvent.getPackageName().toString().equals(mPackageName)) {
                mPackageName = accessibilityEvent.getPackageName().toString();
                if (!mAndroidManager.isAppSystem(mPackageName)) {
                    dbManager.addApp(getAppToWriteInDB(accessibilityEvent));
//                    Toast.makeText(this, accessibilityEvent.getPackageName(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onInterrupt() {

    }

    private String getEventText(AccessibilityEvent accessibilityEvent) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : accessibilityEvent.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        initObjects();
        setServiceInfo(getAccessibilityServiceInfo());
    }

    protected String[] getPack() {
        int count = 0;
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);
        String[] appName = new String[packages.size()];
        for (ApplicationInfo packageInfo : packages) {
//            if (!mAndroidManager.isSystemPackage(packageInfo)) {
            appName[count] = packageInfo.processName;
            count++;
//            }
        }
        return appName;
    }

    private AppModel getAppToWriteInDB(final AccessibilityEvent _accessibilityEvent) {
        Calendar calendar = Calendar.getInstance();
        AppModel appModel = dbManager.getAppModelIfExistsInDB(_accessibilityEvent.getPackageName().toString());
        if (appModel != null) {
            appModel.setDateUsege(calendar.getTimeInMillis());
        } else {
            appModel = new AppModel();
            appModel.setAppName(mAndroidManager.getAppNameByPackage(_accessibilityEvent.getPackageName().toString()));
            appModel.setAppPackageName(_accessibilityEvent.getPackageName().toString());
            appModel.setDateUsege(calendar.getTimeInMillis());
        }
        return appModel;
    }

    private void initObjects() {
        mAndroidManager = new AndroidManager(this);
        dbManager = new DBManager(getApplicationContext());
        dbManager.open();
        dateManager = new DateManager(this);
        dateManager.startAlarmManager();
    }

    private AccessibilityServiceInfo getAccessibilityServiceInfo() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = getPack();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        return info;
    }

}
