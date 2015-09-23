package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.system.models.AppModel;

import java.util.List;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AppsListAdapter extends CursorAdapter {

    private Context mContext;
    private DBManager dbManager;

    public AppsListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_apps, viewGroup, false);
        AppViewHolder appViewHolder = new AppViewHolder();
        appViewHolder.ivAppIcon = (ImageView) view.findViewById(R.id.ivAppIcon_LIA);
        appViewHolder.ivAppStar = (ImageView) view.findViewById(R.id.ivAppStar_LIA);
        appViewHolder.tvAppName = (TextView) view.findViewById(R.id.tvAppName_LIA);
        appViewHolder.tvAppInfo = (TextView) view.findViewById(R.id.tvAppInfo_LIA);
        view.setTag(appViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        AppViewHolder appViewHolder = (AppViewHolder) view.getTag();
        final AppModel appModel = dbManager.getAppFromCursor(cursor);
        appViewHolder.tvAppName.setText(appModel.getAppName());
        appViewHolder.tvAppInfo.setText(getAppInfo(appModel));
            setStarColor(appViewHolder, appModel);
            setAppIcon(appViewHolder, appModel);
    }

    public void setDbManager(final DBManager _dbManager) {
        this.dbManager = _dbManager;
    }


    private static class AppViewHolder {

        private TextView tvAppName, tvAppInfo;
        private ImageView ivAppIcon, ivAppStar;

    }

    private String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %d x " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                _appModel.getAppUseTodayCount(), _appModel.getAppUseTotalCount());
    }

    private void setStarColor(final AppViewHolder appViewHolder, final AppModel _appModel) {
        if (_appModel.isFavourite() == 1) {
            appViewHolder.ivAppStar.setImageResource(R.drawable.ic_star_gold);
        } else {
            appViewHolder.ivAppStar.setImageResource(R.drawable.ic_star_grey);
        }
    }

    private void setAppIcon(final AppViewHolder appViewHolder, final AppModel _appModel) {
        Drawable icon;
        try {
            icon = mContext.getPackageManager().getApplicationIcon(_appModel.getAppPackageName());
            appViewHolder.ivAppIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


}
