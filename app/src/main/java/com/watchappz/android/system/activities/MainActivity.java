package com.watchappz.android.system.activities;

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
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.adapters.AppsListFragmentsPagerAdapter;
import com.watchappz.android.system.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private AppsListFragmentsPagerAdapter mAppsListFragmentsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViewPager();
        initTabLayout();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
