package com.watchappz.android.utils;

import android.app.IntentService;
import android.content.Intent;

import com.watchappz.android.system.models.AppModel;

import java.util.List;

/**
 * Created by
 * mRogach on 18.11.2015.
 */

public final class SetPositionService extends IntentService {

    private List<AppModel> appModels;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */



    public SetPositionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        appModels = (List<AppModel>) intent.getSerializableExtra("AppsPosition");
    }
}
