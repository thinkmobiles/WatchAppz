package com.watchappz.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by
 * mRogach on 09.11.2015.
 */

public final class SharedPrefManager {

    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor mSPEditor;

    private static volatile SharedPrefManager instance;

    public static SharedPrefManager getInstance(final Context _context) {
        if (instance == null) {
            instance = new SharedPrefManager(_context);
        }
        return instance;
    }

    private SharedPrefManager(final Context _context) {
        setSharedPreferences(_context);
    }

    public static void setSharedPreferences(final Context _context) {
        mSharedPref = _context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        mSPEditor = mSharedPref.edit();
    }

    public void putSharedSortType(String key, int value) {
        mSPEditor.putInt(key, value);
        mSPEditor.commit();
    }

    public static SharedPreferences getSharedPref() {
        return mSharedPref;
    }

    public static SharedPreferences.Editor getSPEditor() {
        return mSPEditor;
    }
}
