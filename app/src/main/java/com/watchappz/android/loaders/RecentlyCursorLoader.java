package com.watchappz.android.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.watchappz.android.database.DBManager;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class RecentlyCursorLoader extends CursorLoader {

    private DBManager mDbManager;
    private int mSortType;

    public RecentlyCursorLoader(Context _context, DBManager _dbManager, int _sortType) {
        super(_context);
        this.mDbManager = _dbManager;
        mSortType =_sortType;
    }

    @Override
    public Cursor loadInBackground() {
        return mDbManager.getResentlyData(mSortType);
    }

}