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
import com.watchappz.android.loaders.FavoriteCursorLoader;
import com.watchappz.android.system.models.AppModel;

import java.sql.SQLException;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class FavoriteAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, INewTextListener {



    public static FavoriteAppsListFragment newInstance() {
        return new FavoriteAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(this);
        mainActivity.setINewTextFavoriteListener(this);
//        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
//        setFilterQueryProvider();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.getSupportLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("lifecicle", "onDestroy");
        mainActivity.unregisterReceiver(mSearchBroadcastReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        mainActivity.getLoadingDialogController().showLoadingDialog("favorite");
        return new FavoriteCursorLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        mainActivity.getLoadingDialogController().hideLoadingDialog("favorite");
        initAdapter(cursor);
        setFilterQueryProvider();
        setEmptyView(R.string.app_favorites_empty_view);
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
            mainActivity.getLoadingDialogController().hideLoadingDialog("favorite");
            mainActivity.getSupportLoaderManager().destroyLoader(1);
//            setFilterQueryProvider();
            Cursor cursor = null;
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(1, null, FavoriteAppsListFragment.this);
                } else {
                    cursor = mainActivity.getDbManager().searchAllByInputText(query);
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
                    return mainActivity.getDbManager().searchFavoriteByInputText(constraint.toString());
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
