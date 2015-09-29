package com.watchappz.android.utils;

import android.database.Cursor;

import com.watchappz.android.database.DBManager;

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
        if (_cursor != null) {
            while (_cursor.moveToNext()) {
                allAppsUsegeValues.put(_cursor.getString(5), getUsegeValueInPersent(_cursor, _cursor.getLong(3)));
            }
        }
        List<Map.Entry<String, Float>> list = sortAppsUsegeValues(allAppsUsegeValues);
        Map.Entry<String, Float> maxEntry = list.get(0);
        mDbManager.addToFavorite(maxEntry.getKey());
        list.remove(0);
        for (Map.Entry<String, Float> entry : list) {
            if (isMoreThenEightyPersent(maxEntry, entry)) {
                mDbManager.addToFavorite(entry.getKey());
            }
        }
    }

    private boolean isMoreThenEightyPersent(final Map.Entry<String, Float> _maxEntry, final Map.Entry<String, Float> _entry) {
        if ((_entry.getValue() / _maxEntry.getValue() * 100) >= 80) {
            return true;
        }
        return false;
    }

    private long getTotalCountFromAllApps(final Cursor _cursor) {
        long totalCountAllAppsUsed = 0;
        if (_cursor != null) {
            while (_cursor.moveToNext()) {
                totalCountAllAppsUsed += _cursor.getLong(3);
            }
        }
        return totalCountAllAppsUsed;
    }

    private float getUsegeValueInPersent(final Cursor _cursor, final long _totalCount) {
        if (_totalCount != 0) {
            return _totalCount / getTotalCountFromAllApps(_cursor) * 100;
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
