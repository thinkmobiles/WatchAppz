package com.watchappz.android.system.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.interfaces.IReloadFavoriteDragList;
import com.watchappz.android.interfaces.IReloadList;
import com.watchappz.android.loaders.FavoriteAppsLoader;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.system.models.CursorLoaderRestartEvent;
import com.watchappz.android.utils.SetPositionService;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class FavoriteAppsListFragment extends BaseAppsFragment implements AdapterView.OnItemClickListener,
        INewTextListener, IReloadList, IReloadFavoriteDragList, View.OnClickListener, LoaderManager.LoaderCallbacks<List<AppModel>> {

    private List<AppModel> favoriteApps;

    public static FavoriteAppsListFragment newInstance() {
        return new FavoriteAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.registerReceiver(clickFavoriteReceiver, mFavoriteFilter);
        mainActivity.setFloatingMenuVisibility(true);
        mainActivity.getSupportLoaderManager().restartLoader(1, null, this);
        setVisibleSortingLayout();
    }

    private void setListeners() {
        listView.setOnItemClickListener(this);
        llDefault.setOnClickListener(this);
        llDrag.setOnClickListener(this);
        mainActivity.addiReloadList(this);
        mainActivity.setReloadFavoriteList(this);
        mainActivity.setINewTextFavoriteListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewAccessibilityEvent) {
            mainActivity.getSupportLoaderManager().restartLoader(1, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        writeAppsPositionsToDB();
    }

    private void writeAppsPositionsToDB() {
        List<AppModel> appModels = new ArrayList<>();
        for (int i = 0; i < dragDropFavoriteAppsListAdapter.getCount(); i++) {
            appModels.add(dragDropFavoriteAppsListAdapter.getItem(i));
        }
        Intent intent = new Intent(Intent.ACTION_SYNC, null, mainActivity, SetPositionService.class);
        intent.putExtra("AppsPosition", (Serializable) appModels);
        mainActivity.startService(intent);
    }

    private void setVisibleSortingLayout() {
        llSortTabLayout.setVisibility(View.VISIBLE);
        llDefault.setVisibility(View.VISIBLE);
        llDrag.setVisibility(View.VISIBLE);
        llData.setVisibility(View.GONE);
        llTimeUsed.setVisibility(View.GONE);
        setPressedViews();
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
        mainActivity.getLoadingDialogController().showLoadingDialog(Constants.FAVORITE_RECEIVER);
        return new FavoriteAppsLoader(mainActivity, mainActivity.getDbManager(), mainActivity.getSortType());
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, final List<AppModel> _list) {
        favoriteApps = _list;
        mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.FAVORITE_RECEIVER);
        initFavoriteAdapter(_list);
        setEmptyView(R.string.app_favorites_empty_view);
        shareToFacebook(_list.size());
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

    private BroadcastReceiver clickFavoriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getSupportLoaderManager().restartLoader(1, null, FavoriteAppsListFragment.this);
        }
    };

    private BroadcastReceiver mSearchBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.FAVORITE_RECEIVER);
            mainActivity.getSupportLoaderManager().destroyLoader(1);
            List<AppModel> appModels = new ArrayList<>();
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(1, null, FavoriteAppsListFragment.this);
                } else {
                    appModels = mainActivity.getDbManager().getAppsList(mainActivity.getDbManager().searchFavoriteByInputText(query, mainActivity.getSortType()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initDragAndDropAdapter(appModels);
        }
    };

    @Override
    public void onNewText(String _newText) {
            dragDropFavoriteAppsListAdapter.getFilter().filter(_newText);
    }

    public void onEvent(CursorLoaderRestartEvent event) {
        isNewAccessibilityEvent = true;
    }

    public Bitmap loadBitmapFromView(View v) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setBackgroundColor(mainActivity.getResources().getColor(android.R.color.white));
        ((RelativeLayout) v).setGravity(Gravity.CENTER);
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    private void shareToFacebook(final int _size) {
        mainActivity.getFacebook().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_size >= 5) {
                    mainActivity.getFacebookShareManager().shareToFacebook(loadBitmapFromView(rlAppsContainer));
                } else {
                    Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.facebook_empty_favorite_apps), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void reloadList() {
        mainActivity.getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDefault_FVP:
                writeAppsPositionsToDB();
                if (mainActivity.getSortType() != Constants.SORT_TYPE_DEFAULT) {
                    mainActivity.setSortType(Constants.SORT_TYPE_DEFAULT);
                } else {
                    mainActivity.setSortType(Constants.SORT_TYPE_DEFAULT_DESC);
                    setUpArrow(llDefault, tvDefault, ivArrowDefault);
                }
                sortInTabLayoutListener.sortDefault();
                break;
            case R.id.llDrag_FVP:
                if (mainActivity.getSortType() != Constants.SORT_TYPE_DRAG_AND_DROP) {
                    mainActivity.setSortType(Constants.SORT_TYPE_DRAG_AND_DROP);
                    sortInTabLayoutListener.sortDrag();
                }
                break;
        }
        setPressedViews();
    }

    @Override
    public void reloadFavoriteToDragList() {
        mainActivity.getSupportLoaderManager().restartLoader(1, null, this);
    }
}
