package com.watchappz.android.system.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.watchappz.android.database.AllAppsCursorLoader;
import com.watchappz.android.system.adapters.AppsListAdapter;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public class AllAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor> {



    public static AllAppsListFragment newInstance() {
        return new AllAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AllAppsCursorLoader(mainActivity, mainActivity.getDbManager());
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
