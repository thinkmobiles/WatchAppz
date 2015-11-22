package com.watchappz.android.utils;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.watchappz.android.R;
import com.watchappz.android.WatchAppzApplication;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.system.activities.MainActivity;
import com.watchappz.android.system.models.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * mRogach on 22.11.2015.
 */

public final class AppContextualActionModeListener implements ListView.MultiChoiceModeListener {

    private MainActivity mainActivity;
    private List<AppModel> favoriteApps;
    private List<AppModel> selectedApps;
    private DBManager mDbManager;
    private ActionMode actionMode;

    public AppContextualActionModeListener(final MainActivity _mainActivity, final List<AppModel> _favoriteApps) {
        mainActivity = _mainActivity;
        favoriteApps = _favoriteApps;
        selectedApps = new ArrayList<>();
        mDbManager = WatchAppzApplication.getDbManager();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (position < favoriteApps.size()) {
            if (checked) selectedApps.add(favoriteApps.get(position));
            else selectedApps.remove(favoriteApps.get(position));
            actionMode = mode;
            mode.setTitle(selectedApps.size() + " " + mainActivity.getString(R.string.selected_apps));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDialog();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectedApps.clear();
    }

    protected void showDialog() {
        new MaterialDialog.Builder(mainActivity)
                .backgroundColorRes(android.R.color.white)
                .title(mainActivity.getResources().getString(R.string.hide_apps))
                .titleColorRes(android.R.color.black)
                .content(mainActivity.getResources().getString(R.string.hide_apps_dialog_content))
                .contentColorRes(android.R.color.black)
                .positiveText(mainActivity.getResources().getString(R.string.hide_apps_yes))
                .negativeText(mainActivity.getResources().getString(R.string.hide_apps_no))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (!selectedApps.isEmpty()) {
                            for (AppModel appModel : selectedApps) {
                                mDbManager.hideOrShowApp(appModel.getAppPackageName(), true);
                            }
                            mainActivity.reloadLists();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .show();
    }

    public void hideActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }
}
