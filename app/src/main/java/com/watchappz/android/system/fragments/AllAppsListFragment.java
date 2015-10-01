package com.watchappz.android.system.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.loaders.AllAppsCursorLoader;
import com.watchappz.android.system.models.AppModel;

import java.sql.SQLException;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AllAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, INewTextListener {

    public static AllAppsListFragment newInstance() {
        return new AllAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(this);
        mainActivity.setINewTextListener(this);
//        setFilterQueryProvider();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.getSupportLoaderManager().restartLoader(3, null, this);
        Log.v("AllApps", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("AllApps", "onPause");
        mainActivity.getSupportLoaderManager().destroyLoader(3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("lifecicle", "onDestroy");
        mainActivity.unregisterReceiver(mSearchBroadcastReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("lifecicle", "onCreateLoader");
//        mainActivity.getLoadingDialogController().showLoadingDialog("all");
        return new AllAppsCursorLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v("lifecicle", "onLoadFinished");
//        mainActivity.getLoadingDialogController().hideLoadingDialog("all");
        initAdapter(cursor);
        setFilterQueryProvider();
        setEmptyView(R.string.app_all_empty_view);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppModel appModel = appsListAdapter.getItem(i);
        Intent launchIntent = mainActivity.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    BroadcastReceiver mSearchBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getLoadingDialogController().hideLoadingDialog("all");
            mainActivity.getSupportLoaderManager().destroyLoader(3);
            Log.v("lifecicle", "onReceive");
            Cursor cursor = null;
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(3, null, AllAppsListFragment.this);
                } else {
                    cursor = mainActivity.getDbManager().searchAllByInputText(query);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initAdapter(cursor);
//            setFilterQueryProvider();
        }
    };

    private void setFilterQueryProvider() {
        appsListAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                try {
                    return mainActivity.getDbManager().searchAllByInputText(constraint.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public void onNewText(String _newText) {
        appsListAdapter.getFilter().filter(_newText);
    }
}
