package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class AppsListAdapter extends CursorRecyclerViewAdapter<AppsListAdapter.AppViewHolder> {

    private Context mContext;
    private DBManager dbManager;

    public AppsListAdapter(final Context _context, final Cursor _cursor) {
        super(_context, _cursor);
        this.mContext = _context;
    }

    public void setDbManager(final DBManager _dbManager) {
        this.dbManager = _dbManager;
    }
    //    public void setData(List<AppModel> _apps) {
//        mApps = _apps;
//    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_apps, viewGroup, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AppViewHolder appViewHolder, Cursor cursor) {
        final AppModel appModel = dbManager.getAppFromCursor(cursor);
        appViewHolder.tvAppName.setText(appModel.getAppName());
        appViewHolder.tvAppInfo.setText(getAppInfo(appModel));
        setStarColor(appViewHolder, appModel);
        setAppIcon(appViewHolder, appModel);
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAppName, tvAppInfo;
        private ImageView ivAppIcon, ivAppStar;

        public AppViewHolder(View itemView) {
            super(itemView);
            ivAppIcon = (ImageView) itemView.findViewById(R.id.ivAppIcon_LIA);
            ivAppStar = (ImageView) itemView.findViewById(R.id.ivAppStar_LIA);
            tvAppName = (TextView) itemView.findViewById(R.id.tvAppName_LIA);
            tvAppInfo = (TextView) itemView.findViewById(R.id.tvAppInfo_LIA);
        }
    }

    private String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %d x " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                _appModel.getAppUseTodayCount(), _appModel.getAppUseTotalCount());
    }

    private void setStarColor(final AppViewHolder _appViewHolder, final AppModel _appModel) {
        if (_appModel.isFavourite() == 1) {
            _appViewHolder.ivAppStar.setImageDrawable(mContext.getDrawable(R.drawable.ic_star_gold));
        } else {
            _appViewHolder.ivAppStar.setImageDrawable(mContext.getDrawable(R.drawable.ic_star_grey));
        }
    }

    private void setAppIcon(final AppViewHolder _appViewHolder, final AppModel _appModel) {
        Drawable icon;
        try {
            icon = mContext.getPackageManager().getApplicationIcon(_appModel.getAppPackageName());
            _appViewHolder.ivAppIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


}
