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
public final class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvHelp, tvAbout;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTollbar();
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
    }

    private void setListeners() {
        tvHelp.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
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

}
