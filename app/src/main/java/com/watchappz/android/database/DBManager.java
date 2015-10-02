package com.watchappz.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
                if (cursor.getString(5).equals(_packageName) && cursor.getLong(8) >= 10) {
                    mDB.update(TABLE_APPS, values, KEY_PACKAGE_NAME + " = ?", new String[]{_packageName});
                }
            }
        }
        mDB.close();
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

    public void updateTodayCount() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return;
        }
        Cursor cursor = getAllData();
        Log.v("SetNullTodayCountAppsReceiver", "cursor");
        ContentValues values = new ContentValues();
        values.put(KEY_TODAY_COUNT, 0);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mDB.update(TABLE_APPS, values, KEY_ID + " =?", new String[]{String.valueOf(cursor.getLong(0))});
                Log.v("SetNullTodayCountAppsReceiver", String.valueOf(cursor.getLong(0)));
            }
        }
        mDB.close();
    }

    public AppModel getApp(final String _fieldValue) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        Cursor cursor = mDB.query(TABLE_APPS, new String[]{KEY_ID,
                        KEY_NAME, KEY_TODAY_COUNT, KEY_TOTAL_COUNT, KEY_IS_FAVOURITE, KEY_PACKAGE_NAME,
                        KEY_DATE_USEGE, KEY_IS_ABLE_TO_FAVORITE, KEY_FAVORITE_COUNT, KEY_APP_SIZE}, KEY_PACKAGE_NAME + "=?",
                new String[]{_fieldValue}, null, null, null, null);
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
            app.setDateUsege(cursor.getInt(6));
            app.setIsAbleToFavorite(cursor.getInt(7));
            app.setFavoriteCount(cursor.getLong(8));
            app.setAppSize(cursor.getLong(9));
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
        Cursor cursor;
        switch (_sortType) {
            case 1:
                cursor = mDB.query(TABLE_APPS, null, null, null, null, null, KEY_TOTAL_COUNT + " DESC");
                break;
            case 2:
                cursor = mDB.query(TABLE_APPS, null, null, null, null, null, KEY_APP_SIZE + " DESC");
                break;
            case 3:
                cursor = mDB.query(TABLE_APPS, null, null, null, null, null, KEY_TOTAL_COUNT + " ASC");
                break;
            default:
                cursor = mDB.query(TABLE_APPS, null, null, null, null, null, KEY_TOTAL_COUNT + " DESC");
        }
        return cursor;
    }

    public Cursor getFavoriteData(final int _sortType) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = KEY_IS_FAVOURITE + " == " + 1
                + " OR " + KEY_IS_ABLE_TO_FAVORITE + " == " + 1;
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
            default:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " DESC");
        }
        return cursor;
    }

    public Cursor getResentlyData(final int _sortType) {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = KEY_DATE_USEGE + " BETWEEN " + DateManager.startOfDay()
                + " AND " + DateManager.currentTime();
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
            default:
                cursor = mDB.query(TABLE_APPS, null, selectQuery, null, null, null, KEY_TOTAL_COUNT + " DESC");
        }
        return cursor;
    }


    public AppModel getAppModelIfExistsInDB(final String _fieldValue) {
        AppModel appModel = null;
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE " + KEY_PACKAGE_NAME + " =? ";
        Cursor cursor = null;
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
        app.setDateUsege(_cursor.getInt(6));
        app.setIsAbleToFavorite(_cursor.getInt(7));
        app.setFavoriteCount(_cursor.getLong(8));
        app.setAppSize(_cursor.getLong(9));
        return app;
    }

    public Cursor searchFavoriteByInputText(String inputText) throws SQLException {
        Cursor cursor;
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE " + KEY_NAME + " LIKE ?" + " AND " + KEY_IS_FAVOURITE + " == " + 1;
        cursor = mDB.rawQuery(selectQuery, new String[]{"%" + inputText + "%"});
        return cursor;
    }

    public Cursor searchRecentlyByInputText(String inputText) throws SQLException {
        Cursor cursor;
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE " + KEY_NAME + " LIKE ?" + " AND " + KEY_TODAY_COUNT + " > " + 0;
        cursor = mDB.rawQuery(selectQuery, new String[]{"%" + inputText + "%"});
        return cursor;
    }

    public Cursor searchAllByInputText(String inputText) throws SQLException {
        Cursor cursor;
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE " + KEY_NAME + " LIKE ?";
        cursor = mDB.rawQuery(selectQuery, new String[]{"%" + inputText + "%"});
        return cursor;
    }

}
