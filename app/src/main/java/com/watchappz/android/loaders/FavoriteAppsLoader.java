package com.watchappz.android.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.watchappz.android.database.DBManager;
import com.watchappz.android.system.models.AppModel;

import java.util.List;

/**
 * Created by
 * mRogach on 13.11.2015.
 */

public final class FavoriteAppsLoader extends AsyncTaskLoader<List<AppModel>> {

    private DBManager mDbManager;
    private int mSortType;

    public FavoriteAppsLoader(Context _context, DBManager _dbManager, int _sortType) {
        super(_context);
        this.mDbManager = _dbManager;
        mSortType = _sortType;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<AppModel> loadInBackground() {
        return mDbManager.getAppsList(mDbManager.getFavoriteData(mSortType));
    }
}
