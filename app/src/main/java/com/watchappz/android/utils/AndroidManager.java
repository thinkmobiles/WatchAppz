package com.watchappz.android.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

/**
 * Created by
 * mRogach on 23.09.2015.
 */

public final class AndroidManager {

    private Context mContext;

    public AndroidManager(final Context _context) {
        mContext = _context;
    }

    public boolean isAppInstalled(String packageName) {
        try {
            mContext.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean isSystemPackage(final ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isNamedProcessRunning(final String _processName) {
        if (_processName == null)
            return false;

        ActivityManager manager =
                (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (_processName.equals(process.processName)) {
                return true;
            }
        }
        return false;
    }

    public String getAppNameByPackage(final String _packageName) {
        String appName = "";
        try {
            appName = (String) mContext.getPackageManager().getApplicationLabel(mContext.getPackageManager().getApplicationInfo(_packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    public boolean isAppSystem(final String _packageName) {
        List<ApplicationInfo> installedApps = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ai: installedApps) {
                if (ai.processName.equals(_packageName)) {
                    if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        return true;
                    } else {
                       return false;
                    }
                }
        }
        return false;
    }
}
