package com.watchappz.android.system.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.interfaces.ISendSortTypeListener;
import com.watchappz.android.system.fragments.AboutWatchAppzFragment;
import com.watchappz.android.system.fragments.AppViewPagerFragment;
import com.watchappz.android.system.fragments.HelpFragment;
import com.watchappz.android.system.fragments.SettingsFragment;
import com.watchappz.android.utils.AccessibilityManager;
import com.watchappz.android.utils.FacebookShareManager;
import com.watchappz.android.utils.FavoriteCountManager;
import com.watchappz.android.utils.LoadingDialogController;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ISendSortTypeListener {

    private AccessibilityManager accessibilityManager;
    private DBManager dbManager;
    private FavoriteCountManager mFavoriteCountManager;
    private LoadingDialogController mLoadingDialogController;
    private SearchManager searchManager;
    protected SearchView mSearchView;
    private INewTextListener iNewTextAllAppsListener;
    private INewTextListener iNewTextFavoriteListener;
    private INewTextListener iNewTextRecentlyListener;
    private int sortType;
    private FacebookShareManager facebookShareManager;
    private FloatingActionButton facebook;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

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
        initFloatingButton();
        callbackManager = CallbackManager.Factory.create();
        initFacebook();
        mFragmentNavigator.replaceFragment(AppViewPagerFragment.newInstance());
        facebookButtonClick();
    }

    @Override
         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                mFragmentNavigator.clearBackStackToFragmentOrShow(SettingsFragment.newInstance(this));
                break;
            case R.id.action_help:
                mFragmentNavigator.clearBackStackToFragmentOrShow(HelpFragment.newInstance());
                break;
            case R.id.action_over_watchappz:
                mFragmentNavigator.clearBackStackToFragmentOrShow(AboutWatchAppzFragment.newInstance());
//                Intent intent = new Intent(this, AboutWatchAppzActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_out, R.anim.trans_left_in);
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

    @Override
    public void getSortType(int _type) {
        sortType = _type;
    }

    public int getSortType() {
        return sortType;
    }

    private void initFloatingButton() {
        facebook = new FloatingActionButton(getBaseContext());
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            facebook.setIconDrawable(getResources().getDrawable(R.drawable.ic_fb_icon, this.getTheme()));
        } else {
            facebook.setIconDrawable(getResources().getDrawable(R.drawable.ic_fb_icon));
        }
        faMenu.addButton(facebook);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookShareManager = new FacebookShareManager(this);
    }

    private void facebookButtonClick() {
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager = LoginManager.getInstance();
                loginManager.logInWithPublishPermissions(MainActivity.this, Arrays.asList("publish_actions"));
                loginManager.registerCallback(callbackManager, facebookCallback);
            }
        });
    }
    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            facebookShareManager.shareToFacebook();
            Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
        }
    };


}
