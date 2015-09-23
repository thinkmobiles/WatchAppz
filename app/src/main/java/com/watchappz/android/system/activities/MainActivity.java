package com.watchappz.android.system.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.system.fragments.AboutWatchAppzFragment;
import com.watchappz.android.system.fragments.AppViewPagerFragment;
import com.watchappz.android.system.fragments.BaseAppsFragment;
import com.watchappz.android.system.fragments.HelpFragment;
import com.watchappz.android.system.fragments.SettingsFragment;
import com.watchappz.android.utils.AccessibilityManager;
import com.watchappz.android.utils.LoadingDialogController;

public class MainActivity extends BaseActivity {



    private AccessibilityManager accessibilityManager;
    private DBManager dbManager;
    private LoadingDialogController mLoadingDialogController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDBManager();
        initAccessibilityManager();
        initLoadingController();
        mFragmentNavigator.addFragment(AppViewPagerFragment.newInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void initAccessibilityManager() {
        accessibilityManager = new AccessibilityManager(this);
        accessibilityManager.startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mFragmentNavigator.clearBackStackToFragmentOrShow(SettingsFragment.newInstance());
                break;
            case R.id.action_help:
                mFragmentNavigator.clearBackStackToFragmentOrShow(HelpFragment.newInstance());
                break;
            case R.id.action_over_watchappz:
                mFragmentNavigator.clearBackStackToFragmentOrShow(AboutWatchAppzFragment.newInstance());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDBManager() {
        dbManager = new DBManager(this);
        dbManager.open();
    }

    private void initLoadingController() {
        mLoadingDialogController = new LoadingDialogController();
        mLoadingDialogController.register(this);
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public LoadingDialogController getLoadingDialogController() {
        return mLoadingDialogController;
    }
}
