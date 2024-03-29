package com.watchappz.android.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.system.models.CursorLoaderRestartEvent;
import com.watchappz.android.system.models.MessageEvent;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by
 * mRogach on 16.09.2015.
 */

public final class AppInfoService extends AccessibilityService {

    private DBManager dbManager;
    private AndroidManager mAndroidManager;
    private String mLastPackageName, mNewPackageName;
    private long getEventTime, getNewEventTime, spentTime;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!TextUtils.isEmpty(getEventText(accessibilityEvent))) {
            if (accessibilityEvent.getPackageName() != null && !accessibilityEvent.getPackageName().toString().equals(mNewPackageName)) {
                    mNewPackageName = accessibilityEvent.getPackageName().toString();
                    getNewEventTime = Calendar.getInstance().getTimeInMillis();
                    if (getEventTime > 0) {
                        spentTime = Calendar.getInstance().getTimeInMillis() - getEventTime;
                        if (!TextUtils.isEmpty(mLastPackageName)) {
                            dbManager.setAppSpentTimeInDB(mLastPackageName, spentTime);
                        }
                    }
                    mLastPackageName = mNewPackageName;
                    getEventTime = getNewEventTime;
                    if (!accessibilityEvent.getPackageName().toString().equals(Constants.WATCH_APPZ_PAKAGE) &&
                            !accessibilityEvent.getPackageName().toString().equals(Constants.LAUNCHER_PACKAGE) &&
                            !accessibilityEvent.getPackageName().toString().equals(Constants.SYSTEM_PACKAGE)) {
                        dbManager.addApp(getAppToWriteInDB(accessibilityEvent));
                        EventBus.getDefault().post(new CursorLoaderRestartEvent());
                    }
            }
        }
    }


    @Override
    public void onInterrupt() {
        EventBus.getDefault().unregister(this);
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
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageInfo.processName);
//            if (launchIntent != null && !packageInfo.processName.contains(Constants.SYSTEM_PACKAGE) &&
//                    !packageInfo.processName.contains(Constants.LAUNCHER_PACKAGE) && !packageInfo.processName.contains(Constants.WATCH_APPZ_PAKAGE)) {
            if (launchIntent != null) {
                appName[count] = packageInfo.processName;
                count++;
            }
        }
        return appName;
    }

    private AppModel getAppToWriteInDB(final AccessibilityEvent _accessibilityEvent) {
        Calendar calendar = Calendar.getInstance();
        AppModel appModel = dbManager.getAppModelIfExistsInDB(_accessibilityEvent.getPackageName().toString());
        if (appModel != null) {
            appModel.setDateUsege(calendar.getTimeInMillis());
            appModel.setAppLastUsege(calendar.getTimeInMillis());
            appModel.setAppUseTodayCount(appModel.getAppUseTodayCount() + 1);
            appModel.setAppUseTotalCount(appModel.getAppUseTotalCount() + 1);
            appModel.setFavoriteCount(appModel.getFavoriteCount() + 1);
//            if (appModel.getIsAbleToFavorite() == 1 || appModel.getAppUseTotalCount() >= 10 && ) {
//                appModel.setIsFavourite(1);
//            }
        } else {
            appModel = new AppModel();
            appModel.setAppName(mAndroidManager.getAppNameByPackage(_accessibilityEvent.getPackageName().toString()));
            appModel.setAppPackageName(_accessibilityEvent.getPackageName().toString());
            appModel.setDateUsege(calendar.getTimeInMillis());
            appModel.setAppLastUsege(calendar.getTimeInMillis());
            appModel.setIsFavourite(0);
            appModel.setIsAbleToFavorite(0);
        }
        return appModel;
    }

    private AppModel getAppToWriteInDB(final String _packageName) {
        AppModel appModel = new AppModel();
        appModel.setAppName(mAndroidManager.getAppNameByPackage(_packageName));
        appModel.setAppPackageName(_packageName);
        appModel.setIsFavourite(0);
        appModel.setIsAbleToFavorite(0);
        appModel.setAppSize(getAppSize(_packageName));
        return appModel;
    }

    private void initObjects() {
        EventBus.getDefault().register(this);
        mAndroidManager = new AndroidManager(this);
        dbManager = new DBManager(getApplicationContext());
        dbManager.open();
        DateManager dateManager = new DateManager(this);
        dateManager.startAlarmManager();
    }

    private AccessibilityServiceInfo getAccessibilityServiceInfo() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = getPack();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        appAllAppsToBD(info.packageNames);
        return info;
    }

    private void appAllAppsToBD(final String[] _allApps) {
        AppModel appModel;
        for (String _allApp : _allApps) {
            appModel = getAppToWriteInDB(_allApp);
            if (!TextUtils.isEmpty(appModel.getAppName())) {
                dbManager.addApp(appModel);
            }
        }
    }

    private long getAppSize(final String _packageName) {
        ApplicationInfo applicationInfo;
        long size = 0;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(_packageName, 0);
            File file = new File(applicationInfo.publicSourceDir);
            size = file.length();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return size;
    }

    public void onEvent(MessageEvent event) {
        setServiceInfo(getAccessibilityServiceInfo());
        Log.v("setServiceInfo", "reload pps");
    }

}
