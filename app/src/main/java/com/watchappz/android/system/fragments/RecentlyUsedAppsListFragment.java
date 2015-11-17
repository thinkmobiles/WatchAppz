package com.watchappz.android.system.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.interfaces.IReloadList;
import com.watchappz.android.loaders.RecentlyAppsLoader;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.system.models.CursorLoaderRestartEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class RecentlyUsedAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<List<AppModel>>, AdapterView.OnItemClickListener, INewTextListener, IReloadList {


    public static RecentlyUsedAppsListFragment newInstance() {
        return new RecentlyUsedAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(this);
        mainActivity.setINewTextRecentlyListener(this);
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.registerReceiver(clickFavoriteReceiver, mFavoriteFilter);
        mainActivity.getSupportLoaderManager().initLoader(2, null, this);
        mainActivity.addiReloadList(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewAccessibilityEvent) {
            mainActivity.getSupportLoaderManager().restartLoader(2, null, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainActivity != null) {
            mainActivity.unregisterReceiver(mSearchBroadcastReceiver);
            mainActivity.unregisterReceiver(clickFavoriteReceiver);
        }
    }

    @Override
    public Loader<List<AppModel>> onCreateLoader(int id, Bundle args) {
        mainActivity.getLoadingDialogController().showLoadingDialog(Constants.RECENTLY_RECEIVER);
        return new RecentlyAppsLoader(mainActivity, mainActivity.getDbManager(), mainActivity.getSortType());
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> _list) {
        mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.RECENTLY_RECEIVER);
        initRecentlyAdapter(_list);
        setEmptyView(R.string.app_resently_used_empty_view);
    }

    @Override
    public void onLoaderReset(Loader<List<AppModel>> loader) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppModel appModel = dragDropFavoriteAppsListAdapter.getItem(i);
        Intent launchIntent = mainActivity.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    private BroadcastReceiver mSearchBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.RECENTLY_RECEIVER);
            mainActivity.getSupportLoaderManager().destroyLoader(2);
            List<AppModel> appModels = new ArrayList<>();
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(2, null, RecentlyUsedAppsListFragment.this);
                } else {
                    appModels = mainActivity.getDbManager().getAppsList(mainActivity.getDbManager().searchRecentlyByInputText(query, mainActivity.getSortType()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initRecentlyAdapter(appModels);
        }
    };

    private BroadcastReceiver clickFavoriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getSupportLoaderManager().restartLoader(2, null, RecentlyUsedAppsListFragment.this);
        }
    };


    @Override
    public void onNewText(String _newText) {
        dragDropFavoriteAppsListAdapter.getFilter().filter(_newText);
    }

    @Override
    public void reloadList() {
        mainActivity.getSupportLoaderManager().restartLoader(2, null, this);
    }

    public void onEvent(CursorLoaderRestartEvent event) {
        isNewAccessibilityEvent = true;
    }
}