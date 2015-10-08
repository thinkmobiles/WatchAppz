package com.watchappz.android.system.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.ISendSortTypeListener;
import com.watchappz.android.system.activities.AboutWatchAppzActivity;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * Created by
 * mRogach on 15.09.2015.
 */
public final class SettingsFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tvHelp, tvAbout;
    private RadioButton rbSortMostUsed, rbSortDataUsed, rbSortLowestUsed;
    private ISendSortTypeListener iSendSortTypeListener;

    public static SettingsFragment newInstance(ISendSortTypeListener iSendSortTypeListener) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("listener", iSendSortTypeListener);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTollbar();
        setCheckedRadioButton(mainActivity.getSortType());
    }

    public void getISendSortTypeListener(final ISendSortTypeListener _iSendSortTypeListener) {
        this.iSendSortTypeListener = _iSendSortTypeListener;
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
        getISendSortTypeListener((ISendSortTypeListener) getArguments().getSerializable("listener"));
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
        menu.findItem(R.id.action_search).setVisible(false);
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
                mainActivity.getFragmentNavigator().clearBackStackToFragmentOrShow(AboutWatchAppzFragment.newInstance());
//                Intent intent = new Intent(mainActivity, AboutWatchAppzActivity.class);
//                startActivity(intent);
//                mainActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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
            default:
                rbSortMostUsed.setChecked(true);
                rbSortDataUsed.setChecked(false);
                rbSortLowestUsed.setChecked(false);
        }
    }
}
