package com.watchappz.android.system.fragments;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.adapters.AppsListAdapter;

import java.sql.SQLException;

/**
 * Created by
 * mRogach on 17.09.2015.
 */
public abstract class BaseAppsFragment extends BaseFragment {

    protected ListView listView;
    protected TextView tvEmptyView;
    protected AppsListAdapter appsListAdapter;
    protected IntentFilter mSearchFilter = new IntentFilter(Constants.QUERY);
    protected SearchManager searchManager;
    protected SearchView mSearchView;
    protected BroadcastReceiver mSearchBroadcastReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_apps, container, false);
        findViews();
        return mInflatedView;
    }

    private void findViews() {
        listView = (ListView) mInflatedView.findViewById(R.id.lvApps_FA);
        tvEmptyView = (TextView) mInflatedView.findViewById(R.id.tvEmptyAppsList_EV);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = mainActivity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) searchItem.getActionView();
    }

    protected void initAdapter(final Cursor _cursor) {
                appsListAdapter = new AppsListAdapter(mainActivity, _cursor, mainActivity.getDbManager());
                appsListAdapter.changeCursor(_cursor);
                listView.setAdapter(appsListAdapter);
                appsListAdapter.notifyDataSetChanged();
    }

    protected void setEmptyView(final int _id) {
        tvEmptyView.setText(mainActivity.getResources().getString(_id));
        listView.setEmptyView(tvEmptyView);
    }
}
