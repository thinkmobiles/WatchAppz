package com.watchappz.android.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.watchappz.android.database.DBManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class RecentlyCursorLoader extends CursorLoader {

    private DBManager mDbManager;

    public RecentlyCursorLoader(Context _context, DBManager _dbManager) {
        super(_context);
        this.mDbManager = _dbManager;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = mDbManager.getResentlyData();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cursor;
    }

}