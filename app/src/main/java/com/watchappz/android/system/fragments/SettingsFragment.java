package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.ISendSortTypeListener;
import com.watchappz.android.system.models.CursorLoaderRestartEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by
 * mRogach on 15.09.2015.
 */
public final class SettingsFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tvHelp, tvAbout;
    private RadioButton rbSortMostUsed, rbSortDataUsed, rbSortLowestUsed;
    private ISendSortTypeListener iSendSortTypeListener;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iSendSortTypeListener = (ISendSortTypeListener) getActivity();
        initTollbar();
        setCheckedRadioButton(mainActivity.getSortType());
        mainActivity.setFloatingMenuVisibility(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_settings, container, false);
        findViews();
        setListeners();
        return mInflatedView;
    }

    private void findViews() {
        tvHelp                  = (TextView) mInflatedView.findViewById(R.id.tvHelp_FS);
        tvAbout                 = (TextView) mInflatedView.findViewById(R.id.tvOverWatchAppz_FS);
        rbSortMostUsed          = (RadioButton) mInflatedView.findViewById(R.id.rbSortMostUsed_FS);
        rbSortDataUsed          = (RadioButton) mInflatedView.findViewById(R.id.rbSortingData_FS);
        rbSortLowestUsed        = (RadioButton) mInflatedView.findViewById(R.id.rbSortingLowestUsege_FS);
    }

    private void setListeners() {
        tvHelp.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        rbSortMostUsed.setOnCheckedChangeListener(this);
        rbSortDataUsed.setOnCheckedChangeListener(this);
        rbSortLowestUsed.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.groupMenu, false);
        menu.setGroupVisible(R.id.groupOptions, false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mainActivity.getFragmentNavigator().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvHelp_FS:
                mainActivity.getFragmentNavigator().clearBackStackToFragmentOrShow(HelpFragment.newInstance());
                break;
            case R.id.tvOverWatchAppz_FS:
                mainActivity.startAboutActivity();
                break;

        }
    }

    private void initTollbar() {
        if (!mainActivity.getToolbarManager().isVisibleToolbar()) {
            mainActivity.getToolbarManager().showToolbar();
        }
        mainActivity.getToolbarManager().showBackButton();
        mainActivity.setTitle(getResources().getString(R.string.action_settings));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rbSortMostUsed_FS:
                    rbSortDataUsed.setChecked(false);
                    rbSortLowestUsed.setChecked(false);
                    iSendSortTypeListener.getSortType(Constants.SORT_TYPE_MOST);
                    break;
                case R.id.rbSortingData_FS:
                    rbSortMostUsed.setChecked(false);
                    rbSortLowestUsed.setChecked(false);
                    iSendSortTypeListener.getSortType(Constants.SORT_TYPE_DATA);
                    break;
                case R.id.rbSortingLowestUsege_FS:
                    rbSortMostUsed.setChecked(false);
                    rbSortDataUsed.setChecked(false);
                    iSendSortTypeListener.getSortType(Constants.SORT_TYPE_LOWEST);
                    break;
            }
            EventBus.getDefault().post(new CursorLoaderRestartEvent());
        }
    }

    private void setCheckedRadioButton(final int _sortType) {
        switch (_sortType) {
            case Constants.SORT_TYPE_MOST:
                rbSortMostUsed.setChecked(true);
                rbSortDataUsed.setChecked(false);
                rbSortLowestUsed.setChecked(false);
                break;
            case Constants.SORT_TYPE_DATA:
                rbSortDataUsed.setChecked(true);
                rbSortMostUsed.setChecked(false);
                rbSortLowestUsed.setChecked(false);
                break;
            case Constants.SORT_TYPE_LOWEST:
                rbSortLowestUsed.setChecked(true);
                rbSortMostUsed.setChecked(false);
                rbSortDataUsed.setChecked(false);
                break;
            case Constants.SORT_TYPE_DATA_MENU:
                rbSortDataUsed.setChecked(true);
                rbSortMostUsed.setChecked(false);
                rbSortLowestUsed.setChecked(false);
                break;
            case Constants.SORT_TYPE_MOST_MENU:
                rbSortMostUsed.setChecked(true);
                rbSortDataUsed.setChecked(false);
                rbSortLowestUsed.setChecked(false);
                break;
            default:
                rbSortMostUsed.setChecked(true);
                rbSortDataUsed.setChecked(false);
                rbSortLowestUsed.setChecked(false);
        }
    }
}
