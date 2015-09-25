package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.watchappz.android.R;
import com.watchappz.android.system.adapters.AppsListFragmentsPagerAdapter;

/**
 * Created by
 * mRogach on 21.09.2015.
 */

public class AppViewPagerFragment extends BaseFragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public static AppViewPagerFragment newInstance() {
        return new AppViewPagerFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewPager();
        initTabLayout();
        initTollbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        findViews();
        return mInflatedView;
    }

    private void findViews() {
        mViewPager = (ViewPager) mInflatedView.findViewById(R.id.viewpager_AM);
        mTabLayout = (TabLayout) mInflatedView.findViewById(R.id.tlSliding_tabs_AM);
    }

    private void initViewPager() {
        AppsListFragmentsPagerAdapter mAppsListFragmentsPagerAdapter = new AppsListFragmentsPagerAdapter(
                getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mAppsListFragmentsPagerAdapter);
        mViewPager.setCurrentItem(0, true);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_favorieten)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_recently_used)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_all_apps)));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initTollbar() {
        if (!mainActivity.getToolbarManager().isVisibleToolbar()) {
            mainActivity.getToolbarManager().showToolbar();
        }
        mainActivity.getToolbarManager().hideBackButton();
        mainActivity.setTitle(getResources().getString(R.string.app_name));
    }
}
