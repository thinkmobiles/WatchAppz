package com.watchappz.android.system.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.IReloadFavoriteDragList;
import com.watchappz.android.interfaces.IReloadList;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.interfaces.ISendSortTypeListener;
import com.watchappz.android.interfaces.SortInTabLayoutListener;
import com.watchappz.android.system.fragments.AppViewPagerFragment;
import com.watchappz.android.system.fragments.HelpFragment;
import com.watchappz.android.system.fragments.SettingsFragment;
import com.watchappz.android.utils.AccessibilityManager;
import com.watchappz.android.utils.FacebookShareManager;
import com.watchappz.android.utils.FavoriteCountManager;
import com.watchappz.android.utils.LoadingDialogController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ISendSortTypeListener, SortInTabLayoutListener {

    private DBManager dbManager;
    private LoadingDialogController mLoadingDialogController;
    protected SearchView mSearchView;
    private INewTextListener iNewTextAllAppsListener;
    private INewTextListener iNewTextFavoriteListener;
    private INewTextListener iNewTextRecentlyListener;
    private int sortType;
    private FacebookShareManager facebookShareManager;
    protected FloatingActionButton facebook;
    private List<IReloadList> reloadLists;
    private IReloadFavoriteDragList reloadFavoriteList;
//    private SharedPrefManager sharedPrefManager;

    public final void setINewTextListener(final INewTextListener _iNewTextListener) {
        iNewTextAllAppsListener = _iNewTextListener;
    }

    public final void setINewTextFavoriteListener(final INewTextListener _iNewTextListener) {
        iNewTextFavoriteListener = _iNewTextListener;
    }

    public final void setINewTextRecentlyListener(final INewTextListener _iNewTextListener) {
        iNewTextRecentlyListener = _iNewTextListener;
    }

    public void setReloadFavoriteList(IReloadFavoriteDragList reloadFavoriteList) {
        this.reloadFavoriteList = reloadFavoriteList;
    }

    public void addiReloadList(final IReloadList _iReloadList) {
        reloadLists.add(_iReloadList);
    }

    public FloatingActionButton getFacebook() {
        return facebook;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
//        sharedPrefManager = SharedPrefManager.getInstance(this);
        initDBManager();
        initAccessibilityManager();
        initLoadingController();
        initFloatingButton();
        initFacebook();
        if (savedInstanceState == null)
            mFragmentNavigator.replaceFragment(AppViewPagerFragment.newInstance());
        reloadLists = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFavoriteManager();
    }


    private void initAccessibilityManager() {
        AccessibilityManager accessibilityManager = new AccessibilityManager(this);
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
                startAboutActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDBManager() {
        dbManager = new DBManager(this);
        dbManager.open();
    }

    private void initFavoriteManager() {
        FavoriteCountManager mFavoriteCountManager = new FavoriteCountManager(dbManager);
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

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setActivated(true);

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.setGroupVisible(R.id.groupOptions, false);
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sendSearchBroadcastQuery("");
                menu.setGroupVisible(R.id.groupOptions, true);
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

    @Override
    public void getSortType(int _type) {
        sortType = _type;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    private void initFloatingButton() {
        facebook = new FloatingActionButton(getBaseContext());
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            icon = getResources().getDrawable(R.drawable.ic_fb_icon, this.getTheme());
        } else {
            icon = getResources().getDrawable(R.drawable.ic_fb_icon);
        }
        if (icon != null) {
            facebook.setIconDrawable(icon);
        }
        faMenu.addButton(facebook);
    }

    private void initFacebook() {
        facebookShareManager = new FacebookShareManager(this);
    }

    public void setFloatingMenuVisibility(final boolean _visible) {
        faMenu.setVisibility(_visible ? View.VISIBLE : View.GONE);
    }

    public FacebookShareManager getFacebookShareManager() {
        return facebookShareManager;
    }

    private void reloadLists() {
        for (IReloadList iReloadList : reloadLists) {
            iReloadList.reloadList();
        }
    }


    public void startAboutActivity() {
        Intent intent = new Intent(this, AboutWatchAppzActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    public void sortDefault() {
        reloadLists();
    }

    @Override
    public void sortDrag() {
        reloadFavoriteList.reloadFavoriteToDragList();
    }

    @Override
    public void sortData() {
        reloadLists();
    }

    @Override
    public void sortTimeUsed() {
        reloadLists();
    }
}
