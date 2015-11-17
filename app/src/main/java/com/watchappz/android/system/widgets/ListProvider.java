package com.watchappz.android.system.widgets;

import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by
 * mRogach on 17.11.2015.
 */

public final class ListProvider implements RemoteViewsFactory {

    private List<AppModel> listItemList;
    private Context mContext = null;
    private int appWidgetId;
    private List<Drawable> icons;
    private DBManager mDbManager;

    public ListProvider(Context context, Intent intent, DBManager dbManager) {
        this.mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mDbManager = dbManager;
    }

    @Override
    public void onCreate() {
        listItemList = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        listItemList.clear();
        mDbManager.open();
        listItemList = mDbManager.getAppsList(mDbManager.getResentlyData(0));
        if (listItemList != null) {
            icons = getIcons(listItemList);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
//        return data.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.list_item_apps);
        AppModel appModel = listItemList.get(position);
        remoteView.setTextViewText(R.id.tvAppName_LIA, appModel.getAppName());
        remoteView.setTextViewText(R.id.tvAppInfo_LIA, getAppInfo(appModel));
        remoteView.setTextViewText(R.id.tvAppInfoSizeMinutes_LIA, getAppInfoSizeTime(appModel));
        remoteView.setImageViewBitmap(R.id.ivAppIcon_LIA, drawableToBitmap(icons.get(position)));
        remoteView.setImageViewBitmap(R.id.btnAppStar_LIA, getBitmapFromDrawableRes(setStarColor(appModel)));
        Intent clickIntent = new Intent();
        clickIntent.putExtra(Constants.ITEM_POSITION, position);
        remoteView.setOnClickFillInIntent(R.id.rlItem_LIA, clickIntent);
//        PendingIntent pendingClickIntent = PendingIntent.getBroadcast(mContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Intent clickStarIntent = new Intent();
//        clickStarIntent.putExtra(Constants.STAR_LICK, position);
//        clickStarIntent.setAction(Constants.STAR_LICK);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, clickStarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        clickStarIntent.putExtra(Constants.STAR_LICK, position);
//        remoteView.setOnClickPendingIntent(R.id.rlItem_LIA, pendingIntent);
//        clickIntent.putExtra(Constants.STAR_LICK, position);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteView.setOnClickPendingIntent(R.id.btnAppStar_LIA, pendingIntent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private List<Drawable> getIcons(List<AppModel> models) {
        List<Drawable> drawables = new ArrayList<>();
        for (AppModel appModel : models) {
            try {
                drawables.add(mContext.getPackageManager().getApplicationIcon(appModel.getAppPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return drawables;
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    protected String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %s " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                getDate(_appModel.getDateUsege(), "hh:mm"), _appModel.getAppUseTotalCount());
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
    protected String getAppSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    protected int setStarColor(final AppModel _appModel) {
        if (_appModel.isFavourite() == 1) {
            return R.drawable.ic_star_gold;
        } else {
            return R.drawable.ic_star_grey;
        }
    }

    private Bitmap getBitmapFromDrawableRes(final int _res) {
        return BitmapFactory.decodeResource(mContext.getResources(), _res);
    }

}
