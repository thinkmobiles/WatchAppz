package com.watchappz.android.system.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.watchappz.android.R;
import com.watchappz.android.WatchAppzApplication;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.adapters.AppsListFragmentsPagerAdapter;
import com.watchappz.android.system.fragments.SettingsFragment;
import com.watchappz.android.utils.AccessibilityManager;
import com.watchappz.android.utils.AppInfoService;

public class MainActivity extends BaseActivity {


    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private AppsListFragmentsPagerAdapter mAppsListFragmentsPagerAdapter;
    private AccessibilityManager accessibilityManager;
    private DBManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewPager();
        initTabLayout();
        initDBManager();
        initAccessibilityManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_AM);
        mAppsListFragmentsPagerAdapter = new AppsListFragmentsPagerAdapter(getSupportFragmentManager(),
                WatchAppzApplication.getAppContext());
        mViewPager.setAdapter(mAppsListFragmentsPagerAdapter);
        mViewPager.setCurrentItem(0, true);
    }

    private void initTabLayout() {
        mTabLayout = (TabLayout) findViewById(R.id.tlSliding_tabs_AM);
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_favorieten)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_recent_gebruikt)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.tab_alle_apps)));
        mTabLayout.setupWithViewPager(mViewPager);
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
                mTabLayout.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.flContainer_AM, SettingsFragment.newInstance()).commit();
                break;
            case R.id.action_help:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                break;
            case R.id.action_over_watchappz:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDBManager() {
        dbManager = new DBManager(this);
        dbManager.open();
    }

    public DBManager getDbManager() {
        return dbManager;
    }
}
