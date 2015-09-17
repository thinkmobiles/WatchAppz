package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.watchappz.android.R;
import com.watchappz.android.system.adapters.AppsListAdapter;

/**
 * Created by
 * mRogach on 17.09.2015.
 */
public abstract class BaseAppsFragment extends BaseFragment {

    protected ListView listView;
    protected AppsListAdapter appsListAdapter;
    protected View mInflatedView;
    protected FloatingActionButton fab;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_apps, container, false);
        findViews();
        return mInflatedView;
    }

    private void findViews() {
        listView = (ListView) mInflatedView.findViewById(R.id.lvApps_FA);
//        listView.setEmptyView();
        fab = (FloatingActionButton) mInflatedView.findViewById(R.id.fabAddApp_FA);
    }

}
