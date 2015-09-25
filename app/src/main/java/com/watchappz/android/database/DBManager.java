package com.watchappz.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.utils.DateManager;

import java.io.Serializable;
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
        values.put(KEY_TODAY_COUNT, _app.getAppUseTodayCount() + 1);
        values.put(KEY_TOTAL_COUNT, _app.getAppUseTotalCount() + 1);
        values.put(KEY_IS_FAVOURITE, _app.isFavourite());
        values.put(KEY_PACKAGE_NAME, _app.getAppPackageName());
        values.put(KEY_DATE_USEGE, _app.getDateUsege());

        AppModel existApp = getAppModelIfExistsInDB(_app.getAppPackageName());
        if (existApp != null) {
            mDB.update(TABLE_APPS, values, KEY_ID + " = ?", new String[] {String.valueOf(_app.getId())});
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
                new String[]{ _appPackge });
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
                        KEY_NAME, KEY_TODAY_COUNT, KEY_TOTAL_COUNT, KEY_IS_FAVOURITE, KEY_PACKAGE_NAME, KEY_DATE_USEGE}, KEY_PACKAGE_NAME + "=?",
                new String[]{ _fieldValue }, null, null, null, null);
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
        return mDB.query(TABLE_APPS, null, null, null, null, null, null);
    }

    public Cursor getFavoriteData() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE " + KEY_TODAY_COUNT + " > " + 10;
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(selectQuery, null);
        } catch (Exception Exp) {
            return null;
        }
        return cursor;
    }

    public Cursor getResentlyData() {
        mDB = mDBHelper.getWritableDatabase();
        if (mDB == null) {
            return null;
        }
        String selectQuery = "SELECT  * FROM " + TABLE_APPS + " WHERE "
                + KEY_DATE_USEGE + " BETWEEN " + DateManager.startOfDay()
                + " AND " + DateManager.currentTime();
        Cursor cursor = null;
        try {
            cursor = mDB.rawQuery(selectQuery, null);
        } catch (Exception Exp) {
            return null;
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
            cursor = mDB.rawQuery(selectQuery, new String[] { _fieldValue });
        } catch (Exception Exp) {
            return null;
        }
        if( cursor != null && cursor.moveToFirst() ) {
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
        return app;
    }


}
