package com.watchappz.android.system.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.watchappz.android.R;
import com.watchappz.android.utils.FragmentNavigator;
import com.watchappz.android.utils.ToolbarManager;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public class BaseActivity extends AppCompatActivity{

    protected ToolbarManager mToolbarManager;
    protected FragmentNavigator mFragmentNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initFragmentNavigator();
    }

    private void initToolbar() {
        mToolbarManager = new ToolbarManager(this);
        mToolbarManager.initToolbar();
    }

    public ToolbarManager getToolbarManager() {
        return mToolbarManager;
    }

    private void initFragmentNavigator() {
        mFragmentNavigator = new FragmentNavigator();
        mFragmentNavigator.register(getSupportFragmentManager(), R.id.flContainer_AM);
    }

    public FragmentNavigator getFragmentNavigator() {
        return mFragmentNavigator;
    }
}
