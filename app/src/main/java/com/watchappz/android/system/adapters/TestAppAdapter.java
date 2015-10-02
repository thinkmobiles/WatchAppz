package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.watchappz.android.R;
import com.watchappz.android.system.models.AppModel;

import java.util.List;

/**
 * Created by
 * mRogach on 22.09.2015.
 */

public final class TestAppAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppModel> mAppModels;

    public TestAppAdapter(final Context _context, final List<AppModel> _appModels) {
        mContext = _context;
        mAppModels = _appModels;
    }

    @Override
    public int getCount() {
        if (mAppModels != null)
            return mAppModels.size();
        else
            return 0;
    }

    @Override
    public AppModel getItem(int position) {
        if (mAppModels != null)
            return mAppModels.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AppViewHolder appViewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_apps, viewGroup, false);
            appViewHolder = new AppViewHolder();
            appViewHolder.ivAppIcon = (ImageView) view.findViewById(R.id.ivAppIcon_LIA);
            appViewHolder.btnAppStar = (Button) view.findViewById(R.id.btnAppStar_LIA);
            appViewHolder.tvAppName = (TextView) view.findViewById(R.id.tvAppName_LIA);
            appViewHolder.tvAppInfo = (TextView) view.findViewById(R.id.tvAppInfo_LIA);
            view.setTag(appViewHolder);
        } else {
            appViewHolder = (AppViewHolder) view.getTag();
        }
        final AppModel appModel = mAppModels.get(i);
        appViewHolder.tvAppName.setText(appModel.getAppName());
        appViewHolder.tvAppInfo.setText(getAppInfo(appModel));
        setStarColor(appViewHolder, appModel);
        setAppIcon(appViewHolder, appModel);
        return view;
    }


    private class AppViewHolder {

        private TextView tvAppName, tvAppInfo;
        private ImageView ivAppIcon;
        private Button btnAppStar;
    }

    private String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %d x " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                _appModel.getAppUseTodayCount(), _appModel.getAppUseTotalCount());
    }

    private void setStarColor(final AppViewHolder _appViewHolder, final AppModel _appModel) {
//        if (_appModel.isFavourite() == 1) {
        _appViewHolder.btnAppStar.setBackground(mContext.getResources().getDrawable(R.drawable.ic_star_gold, mContext.getTheme()));
//        } else {
//            _appViewHolder.ivAppStar.setImageResource(R.drawable.ic_star_grey);
//        }
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
