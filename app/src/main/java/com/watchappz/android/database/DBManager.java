package com.watchappz.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.utils.DateManager;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.watchappz.android.database.DBConstants.*;

/**
 * Created by
 * mRogach on 16.09.2015.
 */

public final class DBManager implements Serializable {

    private Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DBManager(final Context _context) {
        this.mContext = _context;
    }

    public void open() {
        mDBHelper = new DBHelper(mContext);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    public void addApp(final AppModel _app) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, _app.getAppName());
        values.put(KEY_TODAY_COUNT, _app.getAppUseTodayCount());
        values.put(KEY_TOTAL_COUNT, _app.getAppUseTotalCount());
        values.put(KEY_IS_FAVOURITE, _app.isFavourite());
        values.put(KEY_PACKAGE_NAME, _app.getAppPackageName());
        values.put(KEY_DATE_USEGE, _app.getDateUsege());
        values.put(KEY_IS_ABLE_TO_FAVORITE, _app.getIsAbleToFavorite());
        values.put(KEY_FAVORITE_COUNT, _app.getFavoriteCount());
        values.put(KEY_APP_SIZE, _app.getAppSize());
        values.put(KEY_APP_LAST_USAGE, _app.getAppLastUsege());
        values.put(KEY_TIME_SPENT, _app.getAppTimeSpent());

        AppModel existApp = getAppModelIfExistsInDB(_app.getAppPackageName());
        if (existApp != null) {
            mDB.update(TABLE_APPS, values, KEY_ID + " = ?", new String[]{String.valueOf(_app.getId())});
        } else {
            mDB.insert(TABLE_APPS, null, values);
        }
        mDB.close();
    }

    public void deleteApp(final AppModel _app) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        mDB.delete(TABLE_APPS, KEY_ID + " =?",
                new String[]{String.valueOf(_app.getId())});
        mDB.close();
    }

    public void deleteAppByPackage(final String _appPackge) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        mDB.delete(TABLE_APPS, KEY_PACKAGE_NAME + " =?",
                new String[]{_appPackge});
        mDB.close();
    }

    public void addToFavorite(final String _packageName) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        Cursor cursor = getAllData();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_FAVOURITE, 1);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(5).equals(_packageName)) {
                    mDB.update(TABLE_APPS, values, KEY_PACKAGE_NAME + " = ?", new String[]{_packageName});
                }
            }
        }
        mDB.close();
    }

    public boolean setToFavotiteIfMaxValueMoreThenTen(final String _packageName) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return false;
        }
        Cursor cursor = getAllData();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(5).equals(_packageName) && cursor.getLong(3) > 10) {
                    return true;
                }
            }
        }
        mDB.close();
        return false;
    }

    public boolean isAbleToFavorite(final String _packageName) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            cursor = getAllData();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(5).equals(_packageName) && cursor.getInt(7) == 1) {
                        return true;
                    }
                }
            }
        }finally {
            if(cursor != null)
                cursor.close();
        }
        mDB.close();
        return false;
    }


    public void addToFavoriteByTap(final String _packageName) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        Cursor cursor = getAllData();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_FAVOURITE, 1);
        values.put(KEY_IS_ABLE_TO_FAVORITE, 1);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(5).equals(_packageName)) {
                    mDB.update(TABLE_APPS, values, KEY_PACKAGE_NAME + " = ?", new String[]{_packageName});
                }
            }
        }
        mDB.close();
    }

    public void setAppSpentTimeInDB(final String _packageName, final long _spentTime) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        Cursor cursor = getAllData();
        ContentValues values = new ContentValues();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(5).equals(_packageName)) {
                    values.put(KEY_TIME_SPENT, cursor.getLong(11) + _spentTime);
                    mDB.update(TABLE_APPS, values, KEY_PACKAGE_NAME + " = ?", new String[]{_packageName});
                }
            }
        }
        mDB.close();
    }

    public void removeFromFavorite(final String _packageName) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_IS_FAVOURITE, 0);
        values.put(KEY_FAVORITE_COUNT, 0);
        values.put(KEY_IS_ABLE_TO_FAVORITE, 0);
        mDB.update(TABLE_APPS, values, KEY_PACKAGE_NAME + " = ?", new String[]{_packageName});
        mDB.close();
    }

    public void removeFavoriteIfLessPersent(final String _packageName) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_IS_FAVOURITE, 0);
        mDB.update(TABLE_APPS, values, KEY_PACKAGE_NAME + " = ?", new String[]{_packageName});
        mDB.close();
    }

    public void updateTodayCount() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        Cursor cursor = getAllData();
        ContentValues values = new ContentValues();
        values.put(KEY_TODAY_COUNT, 0);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mDB.update(TABLE_APPS, values, KEY_ID + " =?", new String[]{String.valueOf(cursor.getLong(0))});
            }
        }
        mDB.close();
    }

    public AppModel getApp(final String _fieldValue) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        Cursor cursor = mDB.query(TABLE_APPS, null, KEY_PACKAGE_NAME + "=?", new String[]{_fieldValue}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AppModel app = new AppModel();
        if (cursor != null) {
            app.setId(cursor.getLong(0));
            app.setAppName(cursor.getString(1));
            app.setAppUseTodayCount(cursor.getLong(2));
            app.setAppUseTotalCount(cursor.getLong(3));
            app.setIsFavourite(cursor.getInt(4));
            app.setAppPackageName(cursor.getString(5));
            app.setDateUsege(cursor.getLong(6));
            app.setIsAbleToFavorite(cursor.getInt(7));
            app.setFavoriteCount(cursor.getLong(8));
            app.setAppSize(cursor.getLong(9));
            app.setAppLastUsege(cursor.getLong(10));
            app.setAppTimeSpent(cursor.getLong(11));
        }
        return app;
    }

    public AppModel getAppFromCursor(final Cursor _cursor) {
        return fillAppModelFromCursor(_cursor);
    }

    public List<AppModel> getAllApps() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        List<AppModel> appList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_APPS;

        Cursor cursor = mDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                appList.add(fillAppModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return appList;
    }

    public Cursor getAllData() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        return mDB.query(TABLE_APPS, null, null, null, null, null, KEY_TOTAL_COUNT + " DESC");
    }

    public Cursor getAllData(final int _sortType) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryForAllAppsIfPackageRemoved(getPackageRemoved());
        return getCursorBySortType(selectQuery, _sortType);
    }

    public Cursor getFavoriteData(final int _sortType) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryIfPackageRemoved(getPackageRemoved()) + KEY_IS_FAVOURITE + " == " + 1
                + " OR " + KEY_IS_ABLE_TO_FAVORITE + " == " + 1;
        return getCursorBySortType(selectQuery, _sortType);
    }

    private Cursor getFavorites() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryIfPackageRemoved(getPackageRemoved()) + KEY_IS_FAVOURITE + " == " + 1
                + " OR " + KEY_IS_ABLE_TO_FAVORITE + " == " + 1;
        return getFavorite(selectQuery);
    }

    public List<AppModel> getFavoriteList() {
        List<AppModel> list = new ArrayList<>();
        Cursor cursor = getFavorites();
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                list.add(fillAppModelFromCursor(cursor));
            }
        }
        return list;
    }

    public List<AppModel> getAppsFromCursor(final Cursor _cursor) {
        List<AppModel> list = new ArrayList<>();
        if (_cursor != null) {
            for (_cursor.moveToFirst(); !_cursor.isAfterLast(); _cursor.moveToNext()) {
                list.add(fillAppModelFromCursor(_cursor));
            }
        }
        return list;
    }

    public Cursor getResentlyData(final int _sortType) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryIfPackageRemoved(getPackageRemoved()) + KEY_DATE_USEGE + " BETWEEN " + DateManager.startOfDay()
                + " AND " + DateManager.currentTime();
        return getRecentlyCursorBySortType(selectQuery, _sortType);
    }


    public AppModel getAppModelIfExistsInDB(final String _fieldValue) {
        AppModel appModel = null;
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE " + KEY_PACKAGE_NAME + " =? ";
        Cursor cursor;
        try {
            cursor = mDB.rawQuery(selectQuery, new String[]{_fieldValue});
        } catch (Exception Exp) {
            return null;
        }
        if (cursor != null && cursor.moveToFirst()) {
            appModel = fillAppModelFromCursor(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        return appModel;
    }

    private AppModel fillAppModelFromCursor(final Cursor _cursor) {
        AppModel app = new AppModel();
        app.setId(_cursor.getLong(0));
        app.setAppName(_cursor.getString(1));
        app.setAppUseTodayCount(_cursor.getLong(2));
        app.setAppUseTotalCount(_cursor.getLong(3));
        app.setIsFavourite(_cursor.getInt(4));
        app.setAppPackageName(_cursor.getString(5));
        app.setDateUsege(_cursor.getLong(6));
        app.setIsAbleToFavorite(_cursor.getInt(7));
        app.setFavoriteCount(_cursor.getLong(8));
        app.setAppSize(_cursor.getLong(9));
        app.setAppLastUsege(_cursor.getLong(10));
        app.setAppTimeSpent(_cursor.getLong(11));
        return app;
    }

    public Cursor searchFavoriteByInputText(String inputText, final int _sortType) throws SQLException {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryIfPackageRemoved(getPackageRemoved()) + KEY_NAME + " LIKE ?" + " AND " + KEY_IS_FAVOURITE + " == " + 1;
        return getCursorBySortType(selectQuery, inputText, _sortType);
    }

    public Cursor searchRecentlyByInputText(String inputText, final int _sortType) throws SQLException {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryIfPackageRemoved(getPackageRemoved()) + KEY_NAME + " LIKE ?" + " AND " + KEY_TODAY_COUNT + " > " + 0;
        return getRecentlyCursorBySortType(selectQuery, inputText, _sortType);
    }

    public Cursor searchAllByInputText(String inputText, final int _sortType) throws SQLException {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = getQueryIfPackageRemoved(getPackageRemoved()) + KEY_NAME + " LIKE ?";
        return getCursorBySortType(selectQuery, inputText, _sortType);
    }

    private Cursor getCursorBySortType(final String selectQuery, final String inputText, final int _sortType) {
        Cursor cursor;
        switch (_sortType) {
            case 1:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 2:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 3:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TOTAL_COUNT + " ASC");
                break;
            case 4:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 5:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 6:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_NAME + " ASC");
                break;
            case 7:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TIME_SPENT + " DESC");
                break;
            case 8:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_NAME + " DESC");
                break;
            case 9:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_APP_SIZE + " ASC");
                break;
            case 10:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TIME_SPENT + " ASC");
                break;
            default:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_NAME + " ASC");
        }
        return cursor;
    }

    private Cursor getRecentlyCursorBySortType(final String selectQuery, final String inputText, final int _sortType) {
        Cursor cursor;
        switch (_sortType) {
            case 1:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 2:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 3:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TOTAL_COUNT + " ASC");
                break;
            case 4:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 5:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 6:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_DATE_USEGE + " DESC");
                break;
            case 7:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_TIME_SPENT + " DESC");
                break;
            default:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, new String[]{"%" + inputText + "%"}, null, null, KEY_DATE_USEGE + " DESC");
        }
        return cursor;
    }

    private Cursor getCursorBySortType(final String selectQuery,  final int _sortType) {
        Cursor cursor;
        switch (_sortType) {
            case 1:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 2:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 3:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " ASC");
                break;
            case 4:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 5:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 6:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_NAME + " ASC");
                break;
            case 7:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TIME_SPENT + " DESC");
                break;
            case 8:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_NAME + " DESC");
                break;
            case 9:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_APP_SIZE + " ASC");
                break;
            case 10:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TIME_SPENT + " ASC");
                break;
            default:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_NAME + " ASC");
        }
        return cursor;
    }

    private Cursor getFavorite(final String selectQuery) {
        return mDB.query(TABLE_APPS, null, selectQuery, null, null, null, null);
    }

    private Cursor getRecentlyCursorBySortType(final String selectQuery,  final int _sortType) {
        Cursor cursor;
        switch (_sortType) {
            case 1:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 2:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 3:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " ASC");
                break;
            case 4:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 5:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 6:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_DATE_USEGE + " DESC");
                break;
            case 7:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TIME_SPENT + " DESC");
                break;
            case 8:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_NAME + " DESC");
                break;
            case 9:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_APP_SIZE + " ASC");
                break;
            case 10:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TIME_SPENT + " ASC");
                break;
            default:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null,  KEY_DATE_USEGE + " DESC");
        }
        return cursor;
    }

    private String getQueryIfPackageRemoved(final ArrayList<String> _removedPackges) {
        String query = "";
        if (!_removedPackges.isEmpty()) {
            for (String packageName : _removedPackges) {
                query += KEY_PACKAGE_NAME + " NOT LIKE '" + packageName + "' AND ";
            }
            return  query;
        } else
            return "";
    }

    private String getQueryForAllAppsIfPackageRemoved(final ArrayList<String> _removedPackges) {
        String query = "";
        if (!_removedPackges.isEmpty()) {
            for (String packageName : _removedPackges) {
                if (_removedPackges.size() > 1 && !packageName.equals(_removedPackges.get(_removedPackges.size() - 1))) {
                    query += KEY_PACKAGE_NAME + " NOT LIKE '" + packageName + "' AND ";
                } else {
                    query += KEY_PACKAGE_NAME + " NOT LIKE '" + packageName + "'";
                }
            }
            return query;
        } else
            return "";
    }

    private ArrayList<String> getPackageRemoved() {
        ArrayList<String> installed = new ArrayList<>(getPackages());
        ArrayList<String> packagesInDB = new ArrayList<>(getAllAppsPackagesFromDB());
        for (String packageName : installed) {
            if (!packagesInDB.isEmpty() && packagesInDB.contains(packageName)) {
                packagesInDB.remove(packageName);
            }
        }
        return packagesInDB;
    }

    private ArrayList<String> getPackages() {
        final PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> appName = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageInfo.processName);
            if (launchIntent != null && !packageInfo.processName.contains(Constants.SYSTEM_PACKAGE) &&
                    !packageInfo.processName.contains(Constants.LAUNCHER_PACKAGE) && !packageInfo.processName.contains(Constants.WATCH_APPZ_PAKAGE)) {
                appName.add(packageInfo.processName);
            }
        }
        return appName;
    }

    private ArrayList<String> getAllAppsPackagesFromDB() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS;
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        ArrayList<String> appList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                appList.add(cursor.getString(5));
            } while (cursor.moveToNext());
        }

        return appList;
    }
}
