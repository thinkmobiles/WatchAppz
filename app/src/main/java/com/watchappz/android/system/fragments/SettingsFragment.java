package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.watchappz.android.R;

/**
 * Created by
 * mRogach on 15.09.2015.
 */
public class SettingsFragment extends BaseFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setTitle(getResources().getString(R.string.action_settings));
        mainActivity.setSupportActionBar(mainActivity.getToolbar());
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mainActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mInflatedView = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        return mInflatedView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Toast.makeText(mainActivity, "Back", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
