package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.filters.AppFilter;
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

public class DragDropFavoriteAppsListAdapter extends ArrayAdapter<AppModel> implements Filterable {

    protected Context mContext;
    protected DBManager dbManager;
    protected ImageLoader imageLoader;
    protected List<AppModel> appModels;
    protected AppFilter mFilter;
    private boolean isDragIconVisible;
    private boolean isHideAppsAdapter;

    public void setIsDragIconVisible(boolean isDragIconVisible) {
        this.isDragIconVisible = isDragIconVisible;
    }

    public void setIsHideAppsAdapter(boolean isHideAppsAdapter) {
        this.isHideAppsAdapter = isHideAppsAdapter;
    }

    public DragDropFavoriteAppsListAdapter(Context mContext, final List<AppModel> _appModels, final DBManager _dbManager) {
        this.mContext = mContext;
        dbManager = _dbManager;
        imageLoader = new ImageLoader(mContext);
        appModels = _appModels;
        addAll(appModels);
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

    protected static class AppViewHolder {

        protected TextView tvAppName, tvAppInfo, tvAppInfoSizeMinutes;
        protected ImageView ivAppIcon;
        protected ImageView btnAppStar;
        protected ImageView ivDrag;
        protected CheckBox chAppUnhide;

    }

    protected void findViews(final AppViewHolder appViewHolder, final View _convertView) {
        appViewHolder.ivAppIcon = (ImageView) _convertView.findViewById(R.id.ivAppIcon_LIA);
        appViewHolder.btnAppStar = (ImageView) _convertView.findViewById(R.id.btnAppStar_LIA);
        appViewHolder.chAppUnhide = (CheckBox) _convertView.findViewById(R.id.chAppUnhide_LIA);
        appViewHolder.ivDrag = (ImageView) _convertView.findViewById(R.id.ivDrag_LIA);
        appViewHolder.tvAppName = (TextView) _convertView.findViewById(R.id.tvAppName_LIA);
        appViewHolder.tvAppInfo = (TextView) _convertView.findViewById(R.id.tvAppInfo_LIA);
        appViewHolder.tvAppInfoSizeMinutes = (TextView) _convertView.findViewById(R.id.tvAppInfoSizeMinutes_LIA);
    }

    private void updateView(final AppViewHolder appViewHolder, final int _position) {
        final AppModel appModel = getItem(_position);
        appViewHolder.tvAppName.setText(appModel.getAppName());
        appViewHolder.tvAppInfo.setText(getAppInfo(appModel));
        setStarColor(appViewHolder, appModel);
        setAppIcon(appViewHolder, appModel);
        appViewHolder.tvAppInfoSizeMinutes.setText(getAppInfoSizeTime(appModel));
        if (!isHideAppsAdapter) {
            appViewHolder.btnAppStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(appViewHolder.btnAppStar, appModel);
                }
            });
        } else {
            appViewHolder.chAppUnhide.setVisibility(View.VISIBLE);
            appViewHolder.btnAppStar.setVisibility(View.GONE);
            appViewHolder.chAppUnhide.setChecked(appModel.isChecked());
        }
        if (isDragIconVisible) {
            appViewHolder.ivDrag.setVisibility(View.VISIBLE);
        } else {
            appViewHolder.ivDrag.setVisibility(View.GONE);
        }
    }

    protected String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %s " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                getDate(_appModel.getDateUsege(), "hh:mm"), _appModel.getAppUseTotalCount());
    }

    protected String getAppInfoSizeTime(final AppModel _appModel) {
        long mills;
        mills = _appModel.getAppTimeSpent();
        return getAppSize(_appModel.getAppSize(), true) + " | " + getTimeLastUsage(mills);
    }

    protected String getTimeLastUsage(final long mills) {
        String time = "";
        double minutes = (double) mills / 60000;
        if (minutes < 1) {
            time = String.format("%.0f " + mContext.getResources().getString(R.string.app_time_used_seconds), (double) mills / 1000);
        }
        double hours = minutes / 60;
        if (hours < 1 && minutes > 1) {
            time = String.format("%.0f " + mContext.getResources().getString(R.string.app_time_used_minutes), (double) minutes);
        } else if (hours > 1 && hours < 24) {
            time = String.format("%.0f " + mContext.getResources().getString(R.string.app_time_used_hours), hours);
        }
        double days = hours / 24;
        if (days > 1 && days < 365) {
            time = String.format("%.0f " + mContext.getResources().getString(R.string.app_time_used_days), days);
        } else if (days > 365) {
            time = String.format("%.0f " + mContext.getResources().getString(R.string.app_time_used_years), days);
        }
        return time;
    }

    protected void setStarColor(final AppViewHolder appViewHolder, final AppModel _appModel) {
        if (_appModel.isFavourite() == 1) {
            setDrawable(appViewHolder.btnAppStar, R.drawable.ic_star_gold);
        } else {
            setDrawable(appViewHolder.btnAppStar, R.drawable.ic_star_grey);
        }
    }

    protected void setAppIcon(final AppViewHolder appViewHolder, final AppModel _appModel) {
        imageLoader.displayImage(_appModel.getAppPackageName(), appViewHolder.ivAppIcon);
    }

    protected void showDialog(final ImageView v, final AppModel _appModel) {
        new MaterialDialog.Builder(mContext)
                .backgroundColorRes(android.R.color.white)
                .title(mContext.getResources().getString(R.string.tab_favorieten))
                .titleColorRes(android.R.color.black)
                .content(mContext.getResources().getString(R.string.app_favorite_add) + " / "
                        + mContext.getResources().getString(R.string.app_favorite_remove))
                .contentColorRes(android.R.color.black)
                .positiveText(mContext.getResources().getString(R.string.app_favorite_add))
                .negativeText(mContext.getResources().getString(R.string.app_favorite_remove))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (_appModel != null && _appModel.isFavourite() == 0) {
                            dbManager.addToFavoriteByTap(_appModel.getAppPackageName());
                            setDrawable(v, R.drawable.ic_star_gold);
                            sendSearchBroadcastFavorute();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        if (_appModel != null && _appModel.isFavourite() == 1) {
                            dbManager.removeFromFavorite(_appModel.getAppPackageName());
                            setDrawable(v, R.drawable.ic_star_grey);
                            sendSearchBroadcastFavorute();
                        }
                    }
                })
                .show();
    }

    protected void setDrawable(final ImageView _view, final int _idDrawable) {
        _view.setImageResource(_idDrawable);
    }

    protected void sendSearchBroadcastFavorute() {
        Intent broadcastIntent = new Intent(Constants.FAVORITE_CLICK);
        mContext.sendBroadcast(broadcastIntent);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new AppFilter(this, appModels);
        }
        return mFilter;
    }

    protected String getAppSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    protected String getDate(long milliSeconds, String dateFormat) {
        if (milliSeconds > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }
        return mContext.getResources().getString(R.string.app_time_last_used_not);
    }
}
