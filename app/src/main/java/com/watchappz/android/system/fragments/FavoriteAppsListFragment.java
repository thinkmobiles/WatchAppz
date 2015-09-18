package com.watchappz.android.system.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.watchappz.android.R;
import com.watchappz.android.database.AllAppsCursorLoader;
import com.watchappz.android.database.FavoriteCursorLoader;
import com.watchappz.android.system.adapters.AppsListAdapter;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public class FavoriteAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor> {



    public static FavoriteAppsListFragment newInstance() {
        return new FavoriteAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvEmptyView.setText(mainActivity.getResources().getString(R.string.app_favorites_empty_view));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FavoriteCursorLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        appsListAdapter = new AppsListAdapter(mainActivity, cursor);
        listView.setAdapter(appsListAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
