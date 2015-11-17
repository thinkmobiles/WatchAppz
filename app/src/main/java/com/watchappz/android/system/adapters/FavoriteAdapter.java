package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.activities.MainActivity;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.utils.image_loader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by
 * mRogach on 06.11.2015.
 */

public final class FavoriteAdapter extends DragDropFavoriteAppsListAdapter {

    private MainActivity mActivity;

    public FavoriteAdapter(Context mContext, List<AppModel> _appModels, DBManager _dbManager) {
        super(mContext, _appModels, _dbManager);
        mActivity = (MainActivity) mContext;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        super.getView(_position, _convertView, _parent);
        AppViewHolder viewHolder;
        if (_convertView == null) {
            _convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_apps, _parent, false);
            viewHolder = new AppViewHolder();
            findViews(viewHolder, _convertView);
            _convertView.setTag(viewHolder);
        } else {
            viewHolder = (AppViewHolder) _convertView.getTag();
        }
        updateView(viewHolder, _position);
        return _convertView;
    }

    protected void updateView(final AppViewHolder appViewHolder, final int _position) {
        final AppModel appModel = getItem(_position);
        appViewHolder.tvAppName.setText(appModel.getAppName());
        appViewHolder.tvAppInfo.setText(getAppInfo(appModel));
        setStarColor(appViewHolder, appModel);
        setAppIcon(appViewHolder, appModel);
        appViewHolder.tvAppInfoSizeMinutes.setText(getAppInfoSizeTime(appModel));
        appViewHolder.btnAppStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(appViewHolder.btnAppStar, appModel.getAppPackageName());
            }
        });
        if (mActivity.getSortType() != Constants.SORT_TYPE_DEFAULT_DESC && mActivity.getSortType() != Constants.SORT_TYPE_DEFAULT) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    dbManager.updateAppPosition(appModel.getAppPackageName(), _position);
                }
            });
            t.start();
        }
    }
}
