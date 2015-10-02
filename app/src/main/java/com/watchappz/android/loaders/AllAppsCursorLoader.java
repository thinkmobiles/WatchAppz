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

public final class AllAppsCursorLoader extends CursorLoader {

    private DBManager mDbManager;
    private int mSortType;

    public AllAppsCursorLoader(Context _context, DBManager _dbManager, int _sortType) {
        super(_context);
        this.mDbManager = _dbManager;
        mSortType = _sortType;
    }



    @Override
    public Cursor loadInBackground() {
        return mDbManager.getAllData(mSortType);
    }

}