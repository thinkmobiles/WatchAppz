package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.SortInTabLayoutListener;
import com.watchappz.android.system.adapters.AppsListFragmentsPagerAdapter;

/**
 * Created by
 * mRogach on 21.09.2015.
 */

public class AppViewPagerFragment extends BaseFragment implements View.OnClickListener {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private LinearLayout llDefault, llData, llTimeUsed;
    private TextView tvDefault, tvData, tvTimeUsed;
    private ImageView ivArrowDefault, ivArrowData, ivArrowTimeUsed;
    private SortInTabLayoutListener sortInTabLayoutListener;

    public static AppViewPagerFragment newInstance() {
        return new AppViewPagerFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sortInTabLayoutListener = (SortInTabLayoutListener) getActivity();
        initViewPager();
        initTabLayout();
        setListeners();
        initTollbar();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mainActivity.setFloatingMenuVisibility(true);
                        break;
                    case 1:
                        mainActivity.setFloatingMenuVisibility(false);
                        break;
                    case 2:
                        mainActivity.setFloatingMenuVisibility(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        findViews();
        return mInflatedView;
    }

    private void findViews() {
        mViewPager           = (ViewPager) mInflatedView.findViewById(R.id.viewpager_AM);
        mTabLayout           = (TabLayout) mInflatedView.findViewById(R.id.tlSliding_tabs_AM);
        llDefault            = (LinearLayout) mInflatedView.findViewById(R.id.llDefault_FVP);
        llData               = (LinearLayout) mInflatedView.findViewById(R.id.llData_FVP);
        llTimeUsed           = (LinearLayout) mInflatedView.findViewById(R.id.llTime_FVP);
        tvDefault            = (TextView) mInflatedView.findViewById(R.id.tvDefault_FVP);
        tvData               = (TextView) mInflatedView.findViewById(R.id.tvData_FVP);
        tvTimeUsed           = (TextView) mInflatedView.findViewById(R.id.tvTime_FVP);
        ivArrowDefault       = (ImageView) mInflatedView.findViewById(R.id.ivArrowDefault_FVP);
        ivArrowData          = (ImageView) mInflatedView.findViewById(R.id.ivArrowData_FVP);
        ivArrowTimeUsed      = (ImageView) mInflatedView.findViewById(R.id.ivArrowTime_FVP);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initViewPager() {
        AppsListFragmentsPagerAdapter mAppsListFragmentsPagerAdapter = new AppsListFragmentsPagerAdapter(
                getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mAppsListFragmentsPagerAdapter);
        mViewPager.setCurrentItem(0, true);
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_favorieten)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_recently_used)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_all_apps)));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setListeners() {
        llDefault.setOnClickListener(this);
        llData.setOnClickListener(this);
        llTimeUsed.setOnClickListener(this);
    }

    private void initTollbar() {
        if (!mainActivity.getToolbarManager().isVisibleToolbar()) {
            mainActivity.getToolbarManager().showToolbar();
        }
        mainActivity.getToolbarManager().hideBackButton();
        mainActivity.setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDefault_FVP:
                sortInTabLayoutListener.sortDefault();
                break;
            case R.id.llData_FVP:
                sortInTabLayoutListener.sortData();
                break;
            case R.id.llTime_FVP:
                sortInTabLayoutListener.sortTimeUsed();
                break;
        }
        setPressedViews();
    }

    private void setPressedViews() {
        switch (mainActivity.getSortType()) {
            case Constants.SORT_TYPE_DEFAULT:
                llDefault.setPressed(true);
                tvDefault.setPressed(true);
                ivArrowDefault.setPressed(true);
                break;
            case Constants.SORT_TYPE_DATA_MENU:
                llData.setPressed(true);
                tvData.setPressed(true);
                ivArrowData.setPressed(true);
                break;
            case Constants.SORT_TYPE_TIME_USED:
                llTimeUsed.setPressed(true);
                tvTimeUsed.setPressed(true);
                ivArrowTimeUsed.setPressed(true);
                break;
            default:
                llDefault.setPressed(true);
                tvDefault.setPressed(true);
                ivArrowDefault.setPressed(true);
        }
    }
}
