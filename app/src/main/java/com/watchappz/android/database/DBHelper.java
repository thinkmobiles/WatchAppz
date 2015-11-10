package com.watchappz.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

import static com.watchappz.android.database.DBConstants.*;

/**
 * Created by
 * mRogach on 16.09.2015.
 */

public final class DBHelper extends SQLiteOpenHelper implements Serializable {

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_APPS + "( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_TODAY_COUNT + " INTEGER," + KEY_TOTAL_COUNT + " INTEGER," +
                KEY_IS_FAVOURITE + " INTEGER," + KEY_PACKAGE_NAME + " TEXT," + KEY_DATE_USEGE + " INTEGER, "  +
                KEY_IS_ABLE_TO_FAVORITE + " INTEGER,"  + KEY_FAVORITE_COUNT + " INTEGER," +
                KEY_APP_SIZE + " INTEGER," + KEY_APP_LAST_USAGE + " INTEGER," + KEY_TIME_SPENT + " INTEGER" + " )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
        onCreate(db);
    }
}