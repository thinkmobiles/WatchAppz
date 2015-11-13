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
import com.watchappz.android.interfaces.IReloadList;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.loaders.AllAppsLoader;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.system.models.CursorLoaderRestartEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AllAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<List<AppModel>>,
        AdapterView.OnItemClickListener, INewTextListener, IReloadList, View.OnClickListener {

    public static AllAppsListFragment newInstance() {
        return new AllAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.registerReceiver(clickFavoriteReceiver, mFavoriteFilter);
        mainActivity.getSupportLoaderManager().initLoader(3, null, this);
        llSortTabLayout.setVisibility(View.VISIBLE);
        llDefault.setVisibility(View.VISIBLE);
        llData.setVisibility(View.VISIBLE);
        llTimeUsed.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        listView.setOnItemClickListener(this);
        mainActivity.setINewTextListener(this);
        mainActivity.addiReloadList(this);
        llDefault.setOnClickListener(this);
        llData.setOnClickListener(this);
        llTimeUsed.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewAccessibilityEvent) {
            mainActivity.getSupportLoaderManager().restartLoader(3, null, this);
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
        mainActivity.getLoadingDialogController().showLoadingDialog(Constants.All_APPS_RECEIVER);
        return new AllAppsLoader(mainActivity, mainActivity.getDbManager(), mainActivity.getSortType());
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> _list) {
        mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.All_APPS_RECEIVER);
        initDragAndDropAdapter(_list);
        setEmptyView(R.string.app_all_empty_view);
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
            mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.All_APPS_RECEIVER);
            mainActivity.getSupportLoaderManager().destroyLoader(3);
            List<AppModel> appModels = new ArrayList<>();
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(3, null, AllAppsListFragment.this);
                } else {
                    appModels = mainActivity.getDbManager().getAppsList(mainActivity.getDbManager().searchAllByInputText(query, mainActivity.getSortType()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initDragAndDropAdapter(appModels);
        }
    };

    private BroadcastReceiver clickFavoriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getSupportLoaderManager().restartLoader(3, null, AllAppsListFragment.this);
        }
    };


    @Override
    public void onNewText(String _newText) {
        dragDropFavoriteAppsListAdapter.getFilter().filter(_newText);
    }

    @Override
    public void reloadList() {
        mainActivity.getSupportLoaderManager().restartLoader(3, null, this);
    }

    public void onEvent(CursorLoaderRestartEvent event) {
        isNewAccessibilityEvent = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDefault_FVP:
                if (mainActivity.getSortType() != Constants.SORT_TYPE_DEFAULT) {
                    mainActivity.setSortType(Constants.SORT_TYPE_DEFAULT);
                } else {
                    mainActivity.setSortType(Constants.SORT_TYPE_DEFAULT_DESC);
                    setUpArrow(llDefault, tvDefault, ivArrowDefault);
                }
                sortInTabLayoutListener.sortDefault();
                break;
            case R.id.llData_FVP:
                if (mainActivity.getSortType() != Constants.SORT_TYPE_DATA_MENU) {
                    mainActivity.setSortType(Constants.SORT_TYPE_DATA_MENU);
                    setUpArrow(llData, tvData, ivArrowData);
                } else {
                    mainActivity.setSortType(Constants.SORT_TYPE_DATA_MENU_ASC);
                    setUpArrow(llData, tvData, ivArrowData);
                }
                sortInTabLayoutListener.sortData();
                break;
            case R.id.llTime_FVP:
                if (mainActivity.getSortType() != Constants.SORT_TYPE_TIME_USED) {
                    mainActivity.setSortType(Constants.SORT_TYPE_TIME_USED);
                    setUpArrow(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                } else {
                    mainActivity.setSortType(Constants.SORT_TYPE_TIME_USED_ASC);
                    setUpArrow(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                }
                sortInTabLayoutListener.sortTimeUsed();
                break;
        }
        setPressedViews();
    }
}
