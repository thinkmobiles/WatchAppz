package com.watchappz.android.system.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.system.fragments.AboutWatchAppzFragment;
import com.watchappz.android.system.fragments.AppViewPagerFragment;
import com.watchappz.android.system.fragments.HelpFragment;
import com.watchappz.android.system.fragments.SettingsFragment;
import com.watchappz.android.utils.AccessibilityManager;
import com.watchappz.android.utils.FavoriteCountManager;
import com.watchappz.android.utils.LoadingDialogController;

public class MainActivity extends BaseActivity {


    private AccessibilityManager accessibilityManager;
    private DBManager dbManager;
    private FavoriteCountManager mFavoriteCountManager;
    private LoadingDialogController mLoadingDialogController;
    private SearchManager searchManager;
    protected SearchView mSearchView;
    private INewTextListener iNewTextAllAppsListener;
    private INewTextListener iNewTextFavoriteListener;
    private INewTextListener iNewTextRecentlyListener;

    public final void setINewTextListener(final INewTextListener _iNewTextListener) {
        iNewTextAllAppsListener = _iNewTextListener;
    }

    public final void setINewTextFavoriteListener(final INewTextListener _iNewTextListener) {
        iNewTextFavoriteListener = _iNewTextListener;
    }

    public final void setINewTextRecentlyListener(final INewTextListener _iNewTextListener) {
        iNewTextRecentlyListener = _iNewTextListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDBManager();
        initAccessibilityManager();
        initLoadingController();
        mFragmentNavigator.replaceFragment(AppViewPagerFragment.newInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFavoriteManager();
    }


    private void initAccessibilityManager() {
        accessibilityManager = new AccessibilityManager(this);
        accessibilityManager.startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        initSearchBar(menu);
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

    private void initFavoriteManager() {
        mFavoriteCountManager = new FavoriteCountManager(dbManager);
        mFavoriteCountManager.setFavorite(dbManager.getAllData());
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

    public void initSearchBar(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setActivated(true);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sendSearchBroadcastQuery("");
                return false;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    mSearchView.setIconified(false);
                    mSearchView.setQuery(query, false);
                    mSearchView.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                iNewTextAllAppsListener.onNewText(newText);
                iNewTextFavoriteListener.onNewText(newText);
                iNewTextRecentlyListener.onNewText(newText);
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            sendSearchBroadcastQuery(query);

        }
    }

    private void sendSearchBroadcastQuery(String query) {
        Intent broadcastIntent = new Intent(Constants.QUERY);
        broadcastIntent.putExtra(Constants.QUERY, query);
        sendBroadcast(broadcastIntent);
    }

    public SearchView getSearchView() {
        return mSearchView;
    }
}
