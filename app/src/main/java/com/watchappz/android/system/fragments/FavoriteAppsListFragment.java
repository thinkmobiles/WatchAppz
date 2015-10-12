package com.watchappz.android.system.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.global.Variables;
import com.watchappz.android.interfaces.INewTextListener;
import com.watchappz.android.loaders.FavoriteCursorLoader;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.system.models.CursorLoaderRestartEvent;
import com.watchappz.android.system.models.MessageEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public final class FavoriteAppsListFragment extends BaseAppsFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, INewTextListener {



    public static FavoriteAppsListFragment newInstance() {
        return new FavoriteAppsListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(this);
        mainActivity.setINewTextFavoriteListener(this);
        mainActivity.registerReceiver(mSearchBroadcastReceiver, mSearchFilter);
        mainActivity.registerReceiver(clickFavoriteReceiver, mFavoriteFilter);
        mainActivity.setFloatingMenuVisibility(true);
        mainActivity.getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewAccessibilityEvent) {
            mainActivity.getSupportLoaderManager().restartLoader(1, null, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("lifecicle", "onDestroy");
        mainActivity.unregisterReceiver(mSearchBroadcastReceiver);
        mainActivity.unregisterReceiver(clickFavoriteReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mainActivity.getLoadingDialogController().showLoadingDialog("favorite");
        return new FavoriteCursorLoader(mainActivity, mainActivity.getDbManager(), mainActivity.getSortType());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        mainActivity.getLoadingDialogController().hideLoadingDialog("favorite");
        initAdapter(cursor);
        setFilterQueryProvider();
        setEmptyView(R.string.app_favorites_empty_view);
        Variables.bitmap = loadBitmapFromView(rlAppsContainer);
        shareToFacebook(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        appsListAdapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppModel appModel = appsListAdapter.getItem(i);
        Intent launchIntent = mainActivity.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    BroadcastReceiver clickFavoriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getSupportLoaderManager().restartLoader(1, null, FavoriteAppsListFragment.this);
        }
    };

    BroadcastReceiver mSearchBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.getLoadingDialogController().hideLoadingDialog("favorite");
            mainActivity.getSupportLoaderManager().destroyLoader(1);
            Cursor cursor = null;
            String query = intent.getStringExtra(Constants.QUERY);
            try {
                if (query.isEmpty()) {
                    mainActivity.getSupportLoaderManager().restartLoader(1, null, FavoriteAppsListFragment.this);
                } else {
                    cursor = mainActivity.getDbManager().searchAllByInputText(query, mainActivity.getSortType());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initAdapter(cursor);
        }
    };

    private void setFilterQueryProvider()  {
        appsListAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                try {
                    return mainActivity.getDbManager().searchFavoriteByInputText(constraint.toString(), mainActivity.getSortType());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public void onNewText(String _newText) {
        appsListAdapter.getFilter().filter(_newText);
    }

    public Bitmap loadBitmapFromView(View v) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setBackgroundColor(mainActivity.getResources().getColor(android.R.color.white));
        ((RelativeLayout) v).setGravity(Gravity.CENTER);
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    private void shareToFacebook(final Cursor _cursor) {
        mainActivity.getFacebook().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_cursor.getCount() >= 5) {
                    mainActivity.getFacebookShareManager().shareToFacebook();
                } else {
                    Toast.makeText(mainActivity, "Not enough favorite apps to share", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onEvent(CursorLoaderRestartEvent event) {
        isNewAccessibilityEvent = true;
    }
}
