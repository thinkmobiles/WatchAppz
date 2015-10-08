package com.watchappz.android.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.watchappz.android.database.DBConstants.KEY_TODAY_COUNT;
import static com.watchappz.android.database.DBConstants.KEY_TOTAL_COUNT;

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
            if (accessibilityEvent.getPackageName()!= null && !accessibilityEvent.getPackageName().toString().equals(mPackageName)) {
                mPackageName = accessibilityEvent.getPackageName().toString();
                    dbManager.addApp(getAppToWriteInDB(accessibilityEvent));
//                    Toast.makeText(this, accessibilityEvent.getPackageName(), Toast.LENGTH_LONG).show();
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
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageInfo.processName);
            if (launchIntent != null && !packageInfo.processName.contains(Constants.SYSTEM_PACKAGE) &&
                    !packageInfo.processName.contains(Constants.LAUNCHER_PACKAGE)) {
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

}
