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
import com.watchappz.android.loaders.RecentlyCursorLoader;
import com.watchappz.android.system.models.AppModel;

import java.sql.SQLException;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class RecentlyUsedAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, INewTextListener {



    public static RecentlyUsedAppsListFragment newInstance() {
        return new RecentlyUsedAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(this);
        mainActivity.setINewTextRecentlyListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.getSupportLoaderManager().restartLoader(2, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("lifecicle", "onDestroy");
        mainActivity.unregisterReceiver(mSearchBroadcastReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mainActivity.getLoadingDialogController().showLoadingDialog("recently");
        return new RecentlyCursorLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mainActivity.getLoadingDialogController().hideLoadingDialog("recently");
        initAdapter(cursor);
        setFilterQueryProvider();
        setEmptyView(R.string.app_resently_used_empty_view);
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
            mainActivity.getLoadingDialogController().hideLoadingDialog("recently");
            mainActivity.getSupportLoaderManager().destroyLoader(2);
            Cursor cursor = null;
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(2, null, RecentlyUsedAppsListFragment.this);
                } else {
                    cursor = mainActivity.getDbManager().searchRecentlyByInputText(query);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initAdapter(cursor);
        }
    };

    private void setFilterQueryProvider()  {
        appsListAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                try {
                    return mainActivity.getDbManager().searchRecentlyByInputText(constraint.toString());
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