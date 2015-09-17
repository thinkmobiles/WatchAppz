package com.watchappz.android.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.watchappz.android.R;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class ToolbarManager {

    private Toolbar mToolbar;
    private AppCompatActivity mActivity;

    public ToolbarManager(final Activity _activity) {
        mActivity = (AppCompatActivity) _activity;
    }

    public void initToolbar() {
            mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            mActivity.setSupportActionBar(mToolbar);
    }

    public void showBackButton() {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
