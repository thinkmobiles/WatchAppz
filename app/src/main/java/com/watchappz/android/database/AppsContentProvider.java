package com.watchappz.android.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import static com.watchappz.android.database.DBConstants.*;

/**
 * Created by
 * mRogach on 01.10.2015.
 */

public final class AppsContentProvider extends ContentProvider {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    // // Uri
    // authority
    static final String AUTHORITY = "com.watchappz.android.provider";

    // path
    static final String APP_PATH = "apps";

    // Общий Uri
    public static final Uri APP_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + APP_PATH);

    // Типы данных
    // набор строк
    static final String APP_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + APP_PATH;

    // одна строка
    static final String APP_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + APP_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_APPS = 1;

    // Uri с указанным ID
    static final int URI_APPS_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, APP_PATH, URI_APPS);
        uriMatcher.addURI(AUTHORITY, APP_PATH + "/#", URI_APPS_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case URI_APPS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = KEY_NAME + " ASC";
                }
                break;
            case URI_APPS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_APPS, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                APP_CONTENT_URI);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_APPS:
                return APP_CONTENT_TYPE;
            case URI_APPS_ID:
                return APP_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_APPS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(TABLE_APPS, null, values);
        Uri resultUri = ContentUris.withAppendedId(APP_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_APPS:
                break;
            case URI_APPS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(TABLE_APPS, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_APPS:
                break;
            case URI_APPS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(TABLE_APPS, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
}
