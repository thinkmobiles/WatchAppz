package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.watchappz.android.R;

import org.w3c.dom.Text;

/**
 * Created by
 * mRogach on 15.09.2015.
 */
public final class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvHelp, tvAbout;
    private RadioButton rbSortMostUsed, rbSortDataUsed, rbSortLowestUsed;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTollbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
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

}
