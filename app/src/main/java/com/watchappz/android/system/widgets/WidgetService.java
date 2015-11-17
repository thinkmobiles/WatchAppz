package com.watchappz.android.system.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.watchappz.android.database.DBManager;

/**
 * Created by
 * mRogach on 17.11.2015.
 */

public final class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListProvider(this.getApplicationContext(), intent, new DBManager(getApplicationContext()));
    }
}
