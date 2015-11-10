package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        setPressedViews();
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
        mViewPager = (ViewPager) mInflatedView.findViewById(R.id.viewpager_AM);
        mTabLayout = (TabLayout) mInflatedView.findViewById(R.id.tlSliding_tabs_AM);
        llDefault = (LinearLayout) mInflatedView.findViewById(R.id.llDefault_FVP);
        llData = (LinearLayout) mInflatedView.findViewById(R.id.llData_FVP);
        llTimeUsed = (LinearLayout) mInflatedView.findViewById(R.id.llTime_FVP);
        tvDefault = (TextView) mInflatedView.findViewById(R.id.tvDefault_FVP);
        tvData = (TextView) mInflatedView.findViewById(R.id.tvData_FVP);
        tvTimeUsed = (TextView) mInflatedView.findViewById(R.id.tvTime_FVP);
        ivArrowDefault = (ImageView) mInflatedView.findViewById(R.id.ivArrowDefault_FVP);
        ivArrowData = (ImageView) mInflatedView.findViewById(R.id.ivArrowData_FVP);
        ivArrowTimeUsed = (ImageView) mInflatedView.findViewById(R.id.ivArrowTime_FVP);
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

    private void setPressedViews() {
        switch (mainActivity.getSortType()) {
            case Constants.SORT_TYPE_DEFAULT:
                setCheckedColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_DEFAULT_DESC:
                llDefault.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
                tvDefault.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
                ivArrowDefault.setImageResource(R.drawable.ic_arrow_black_up);
                setDefaultColors(llData, tvData, ivArrowData);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_DATA_MENU:
                setCheckedColors(llData, tvData, ivArrowData);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_DATA_MENU_ASC:
                llData.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
                tvData.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
                ivArrowData.setImageResource(R.drawable.ic_arrow_black_up);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_TIME_USED:
                setCheckedColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                break;
            case Constants.SORT_TYPE_TIME_USED_ASC:
                llTimeUsed.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
                tvTimeUsed.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
                ivArrowTimeUsed.setImageResource(R.drawable.ic_arrow_black_up);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                break;
            default:
                setCheckedColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
        }
    }

    private void setDefaultColors(final LinearLayout _layout, final TextView _textView, final ImageView _arrow) {
        _layout.setBackgroundColor(getColor(R.color.sort_tab_layout_background));
        _textView.setTextColor(getColor(R.color.sort_tab_title_text_color));
        _arrow.setImageResource(R.drawable.ic_arrow_grey_down);
    }

    private void setCheckedColors(final LinearLayout _layout, final TextView _textView, final ImageView _arrow) {
        _layout.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
        _textView.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
        _arrow.setImageResource(R.drawable.ic_arrow_black_down);
    }

    private void setUpArrow(final LinearLayout _layout, final TextView _textView, final ImageView _arrow) {
        _layout.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
        _textView.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
        _arrow.setImageResource(R.drawable.ic_arrow_black_up);
    }
}
