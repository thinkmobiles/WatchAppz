package com.watchappz.android.utils;

import android.database.Cursor;
import android.util.Log;

import com.watchappz.android.database.DBManager;
import com.watchappz.android.system.models.AppModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by
 * mRogach on 28.09.2015.
 */

public final class FavoriteCountManager {

    private DBManager mDbManager;

    public FavoriteCountManager(final DBManager _dbManager) {
        mDbManager = _dbManager;
    }

    public void setFavorite(final Cursor _cursor) {

        Map<String, Float> allAppsUsegeValues = new HashMap<>();
            Log.v("Apps", String.valueOf(_cursor.getCount()));
        ArrayList<AppModel> apps = (ArrayList<AppModel>) mDbManager.getAllApps();
            for (AppModel appModel : apps) {
                allAppsUsegeValues.put(appModel.getAppPackageName(), getUsegeValueInPersent(apps, appModel.getAppUseTotalCount()));
            }
        if (allAppsUsegeValues.isEmpty()) {
            return;
        }
        List<Map.Entry<String, Float>> list = sortAppsUsegeValues(allAppsUsegeValues);
        Log.v("Apps", String.valueOf(allAppsUsegeValues.size()));
        Map.Entry<String, Float> maxEntry = list.get(0);
        if (mDbManager.setToFavotiteIfMaxValueMoreThenTen(maxEntry.getKey())) {
            mDbManager.addToFavorite(maxEntry.getKey());
            list.remove(0);
            for (Map.Entry<String, Float> entry : list) {
                if (isMoreThenEightyPersent(maxEntry, entry)) {
                    mDbManager.addToFavorite(entry.getKey());
                } else if (!mDbManager.isAbleToFavorite(entry.getKey())) {
                    mDbManager.removeFavoriteIfLessPersent(entry.getKey());
                }
            }
        }
    }

    private boolean isMoreThenEightyPersent(final Map.Entry<String, Float> _maxEntry, final Map.Entry<String, Float> _entry) {
        if ((_entry.getValue() / _maxEntry.getValue() * 100) >= 80) {
            return true;
        }
        return false;
    }

    private long getTotalCountFromAllApps(final ArrayList<AppModel> _list) {
        long totalCountAllAppsUsed = 0;
        for (AppModel appModel : _list) {
            totalCountAllAppsUsed += appModel.getAppUseTotalCount();
        }
        return totalCountAllAppsUsed;
    }

    private float getUsegeValueInPersent(final ArrayList<AppModel> _list, final long _totalCount) {
        long totalCountFromAllApps = getTotalCountFromAllApps(_list);
        if (_totalCount != 0 && totalCountFromAllApps != 0) {
            return (float)_totalCount / (float)totalCountFromAllApps * 100;
        }
        return 0;
    }

    public List<Map.Entry<String, Float>> sortAppsUsegeValues(Map<String, Float> _appsUsegeValues) {

        List<Map.Entry<String, Float>> list = new LinkedList<>(_appsUsegeValues.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        return list;
    }


}
