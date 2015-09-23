package com.watchappz.android.system.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;

import com.watchappz.android.R;
import com.watchappz.android.loaders.FavoriteCursorLoader;
import com.watchappz.android.system.adapters.AppsListAdapter;
import com.watchappz.android.system.models.AppModel;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class FavoriteAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {



    public static FavoriteAppsListFragment newInstance() {
        return new FavoriteAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.getSupportLoaderManager().initLoader(1, null, this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mainActivity.getLoadingDialogController().showLoadingDialog("favorite");
        return new FavoriteCursorLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        appsListAdapter = new AppsListAdapter(mainActivity, cursor);
        appsListAdapter.setDbManager(mainActivity.getDbManager());
        listView.setAdapter(appsListAdapter);
        tvEmptyView.setText(mainActivity.getResources().getString(R.string.app_favorites_empty_view));
        mainActivity.getLoadingDialogController().hideLoadingDialog("favorite");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppModel appModel = appsListAdapter.getItem(i);
        Intent launchIntent = mainActivity.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackageName());
        startActivity(launchIntent);
    }
}
