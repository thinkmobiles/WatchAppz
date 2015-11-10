package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.utils.image_loader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AppsListAdapter extends CursorAdapter {

    private Context mContext;
    private DBManager dbManager;
    private ImageLoader imageLoader;

    public AppsListAdapter(Context context, Cursor cursor, final DBManager _dbManager) {
        super(context, cursor, 0);
        mContext = context;
        this.dbManager = _dbManager;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public AppModel getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return dbManager.getAppFromCursor(cursor);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_apps, viewGroup, false);
        AppViewHolder appViewHolder = new AppViewHolder();
        appViewHolder.ivAppIcon = (ImageView) view.findViewById(R.id.ivAppIcon_LIA);
        appViewHolder.btnAppStar = (ImageView) view.findViewById(R.id.btnAppStar_LIA);
        appViewHolder.tvAppName = (TextView) view.findViewById(R.id.tvAppName_LIA);
        appViewHolder.tvAppInfo = (TextView) view.findViewById(R.id.tvAppInfo_LIA);
        appViewHolder.tvAppInfoSizeMinutes = (TextView) view.findViewById(R.id.tvAppInfoSizeMinutes_LIA);
        view.setTag(appViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final AppViewHolder appViewHolder = (AppViewHolder) view.getTag();
        final AppModel appModel = dbManager.getAppFromCursor(cursor);
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
    }

    private static class AppViewHolder {

        private TextView tvAppName, tvAppInfo, tvAppInfoSizeMinutes;
        private ImageView ivAppIcon;
        private ImageView btnAppStar;

    }

    private String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %s " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                getDate(_appModel.getDateUsege(), "hh:mm"), _appModel.getAppUseTotalCount());
    }

    private String getAppInfoSizeTime(final AppModel _appModel) {
        long mills;
        mills = _appModel.getAppTimeSpent();
        return getAppSize(_appModel.getAppSize(), true) + " | " + getTimeLastUsage(mills);
    }

    private String getTimeLastUsage(final long mills) {
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

    private void setStarColor(final AppViewHolder appViewHolder, final AppModel _appModel) {
        if (_appModel.isFavourite() == 1) {
            setDrawable(appViewHolder.btnAppStar, R.drawable.ic_star_gold);
        } else {
            setDrawable(appViewHolder.btnAppStar, R.drawable.ic_star_grey);
        }
    }

    private void setAppIcon(final AppViewHolder appViewHolder, final AppModel _appModel) {
        imageLoader.displayImage(_appModel.getAppPackageName(), appViewHolder.ivAppIcon);
    }

    private void showDialog(final ImageView v, final String _packageName) {
        final AppModel appModel = dbManager.getApp(_packageName);
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
                        if (appModel != null && appModel.isFavourite() == 0) {
                            dbManager.addToFavoriteByTap(_packageName);
                            setDrawable(v, R.drawable.ic_star_gold);
                            sendSearchBroadcastFavorute();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        if (appModel != null && appModel.isFavourite() == 1) {
                            dbManager.removeFromFavorite(_packageName);
                            setDrawable(v, R.drawable.ic_star_grey);
                            sendSearchBroadcastFavorute();
                        }
                    }
                })
                .show();
    }

    private void setDrawable(final ImageView _view, final int _idDrawable) {
        _view.setImageResource(_idDrawable);
    }

    private void sendSearchBroadcastFavorute() {
        Intent broadcastIntent = new Intent(Constants.FAVORITE_CLICK);
        mContext.sendBroadcast(broadcastIntent);
    }

    private String getAppSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private String getDate(long milliSeconds, String dateFormat) {
        if (milliSeconds > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }
        return mContext.getResources().getString(R.string.app_time_last_used_not);
    }

}
