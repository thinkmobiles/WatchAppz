package com.watchappz.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by
 * mRogach on 23.09.2015.
 */

public class AndroidManager {

    private Context mContext;

    public AndroidManager(final Context _context) {
        mContext = _context;
    }

    public boolean isAppInstalled(String packageName) {
        try {
            mContext.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
