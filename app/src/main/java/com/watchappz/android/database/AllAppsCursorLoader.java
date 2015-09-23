package com.watchappz.android.database;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import java.util.concurrent.TimeUnit;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public class AllAppsCursorLoader extends CursorLoader {

    private DBManager mDbManager;

    public AllAppsCursorLoader(Context _context, DBManager _dbManager) {
        super(_context);
        this.mDbManager = _dbManager;
    }



    @Override
    public Cursor loadInBackground() {
        Cursor cursor = mDbManager.getAllData();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cursor;
    }

}