package com.watchappz.android.system.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.watchappz.android.database.AllAppsCursorLoader;
import com.watchappz.android.system.adapters.AppsListAdapter;
import com.watchappz.android.system.adapters.TestAppAdapter;
import com.watchappz.android.system.models.AppModel;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public class AllAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {



    public static AllAppsListFragment newInstance() {
        return new AllAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        tvEmptyView.setText(mainActivity.getResources().getString(R.string.app_all_empty_view));
        Log.v("onActivityCreated", "AllAppsListFragment");
//        appsListAdapter = new AppsListAdapter(mainActivity, mainActivity.getDbManager().getAllData());
//        appsListAdapter = new TestAppAdapter(mainActivity, mainActivity.getDbManager().getAllApps());
//        listView.setAdapter(appsListAdapter);
//        listView.setOnItemClickListener(this);
        mainActivity.getSupportLoaderManager().initLoader(3, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mainActivity.getLoadingDialogController().showLoadingDialog("all");
        return new AllAppsCursorLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mainActivity.getLoadingDialogController().hideLoadingDialog("all");
        appsListAdapter = new AppsListAdapter(mainActivity, cursor);
        appsListAdapter.setDbManager(mainActivity.getDbManager());
        appsListAdapter.changeCursor(cursor);
        listView.setAdapter(appsListAdapter);
        appsListAdapter.notifyDataSetChanged();
//        listView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        AppModel appModel = appsListAdapter.getItem(i);
//        Intent launchIntent = mainActivity.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackageName());
//        startActivity(launchIntent);
    }

}
