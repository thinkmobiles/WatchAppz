package com.watchappz.android.system.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;

import java.util.List;

/**
 * Created by
 * mRogach on 17.11.2015.
 */

public final class RecentlyAppWidget extends AppWidgetProvider {

    private final String ACTION_ON_CLICK = "com.watchappz.android.system.widgets.itemonclick";
    private List<AppModel> appModels;
    private DBManager mDbManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mDbManager = new DBManager(context);
        mDbManager.open();
        appModels = mDbManager.getAppsList(mDbManager.getResentlyData(0));
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, appModels, id);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(Constants.ITEM_POSITION, -1);
//            int itemStarClickPos = intent.getIntExtra(Constants.STAR_LICK, -1);
            if (itemPos != -1) {
                if (mDbManager == null) {
                    mDbManager = new DBManager(context);
                    mDbManager.open();
                }
                if (appModels == null) {
                    appModels = mDbManager.getAppsList(mDbManager.getResentlyData(0));
                }
                AppModel appModel = appModels.get(itemPos);
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackageName());
                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                }
            }
        }
//            if (itemStarClickPos != -1) {
//                Toast.makeText(context, "Clicked on Star " + itemPos,
//                        Toast.LENGTH_SHORT).show();
//            }
//        } else if (intent.getAction().equalsIgnoreCase(Constants.STAR_LICK)) {
//            Toast.makeText(context, "Clicked on Star ",
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, List<AppModel> appModels, int widgetID) {

        if (appModels == null) return;
        RemoteViews remoteViews = updateWidgetListView(context, widgetID);
        setListClick(remoteViews, context, widgetID);
        appWidgetManager.updateAppWidget(widgetID, remoteViews);
            appWidgetManager.updateAppWidget(widgetID, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetID,
                    R.id.lvFavoriteApps_W);
        }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews( context.getPackageName(),R.layout.widget);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.lvFavoriteApps_W, svcIntent);
        remoteViews.setEmptyView(R.id.lvFavoriteApps_W, R.id.empty_view);
        return remoteViews;
    }

    void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, RecentlyAppWidget.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.lvFavoriteApps_W, listClickPIntent);
//        rv.setOnClickPendingIntent(R.id.lvFavoriteApps_W, listClickPIntent);
    }
}
