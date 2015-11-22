package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.loaders.HiddenAppsLoader;
import com.watchappz.android.system.adapters.DragDropFavoriteAppsListAdapter;
import com.watchappz.android.system.models.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * mRogach on 22.11.2015.
 */

public final class HiddenAppsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<AppModel>>,
        View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private ListView mListView;
    private TextView tvUnhide, tvEmptyView;
    private CheckBox chUnhideApps;
    private DragDropFavoriteAppsListAdapter mAdapter;
    private List<AppModel> hiddenApps;
    private List<AppModel> appsToUnhide;

    public static HiddenAppsFragment newInstance() {
        return new HiddenAppsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_hidden_apps, container, false);
        findViews();
        return mInflatedView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.groupMenu, false);
        menu.setGroupVisible(R.id.groupOptions, false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mainActivity.getFragmentNavigator().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findViews() {
        mListView = (ListView) mInflatedView.findViewById(R.id.lvHideApps_FHA);
        tvUnhide = (TextView) mInflatedView.findViewById(R.id.tvUnhide_FHA);
        tvEmptyView = (TextView) mInflatedView.findViewById(R.id.tvEmptyAppsList_FHA);
        chUnhideApps = (CheckBox) mInflatedView.findViewById(R.id.chUnhideApps_FHA);
    }

    private void setListeners() {
        chUnhideApps.setOnCheckedChangeListener(this);
        tvUnhide.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTollbar();
        setListeners();
        mainActivity.getSupportLoaderManager().restartLoader(4, null, this);
        appsToUnhide = new ArrayList<>();
    }

    @Override
    public Loader<List<AppModel>> onCreateLoader(int id, Bundle args) {
        mainActivity.getLoadingDialogController().showLoadingDialog(Constants.FAVORITE_RECEIVER);
        return new HiddenAppsLoader(mainActivity, mainActivity.getDbManager());
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> data) {
        mainActivity.getLoadingDialogController().hideLoadingDialog(Constants.FAVORITE_RECEIVER);
        hiddenApps = data;
        initList();
        chUnhideApps.setChecked(false);
        checkValidateUnhideButton();
    }

    @Override
    public void onLoaderReset(Loader<List<AppModel>> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvUnhide_FHA:
                unHideApps();
                appsToUnhide.clear();
                mainActivity.getSupportLoaderManager().restartLoader(4, null, this);
                mainActivity.reloadLists();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chAppUnhide_LIA);
        if (checkBox.getVisibility() == View.VISIBLE) {
            checkBox.toggle();
        }
        getAppsToUnhide(position);
    }

    private void getAppsToUnhide(final int _position) {
        if (!hiddenApps.get(_position).isChecked()) {
            hiddenApps.get(_position).setIsChecked(true);
            appsToUnhide.add(hiddenApps.get(_position));
        } else if (hiddenApps.get(_position).isChecked()) {
            hiddenApps.get(_position).setIsChecked(false);
            if (!appsToUnhide.isEmpty() && appsToUnhide.contains(hiddenApps.get(_position))) {
                appsToUnhide.remove(hiddenApps.get(_position));
            }
        }
        checkValidateUnhideButton();
    }

    private void checkValidateUnhideButton() {
        if (!appsToUnhide.isEmpty()) {
            tvUnhide.setEnabled(true);
        } else
            tvUnhide.setEnabled(false);
    }

    private void initTollbar() {
        if (!mainActivity.getToolbarManager().isVisibleToolbar()) {
            mainActivity.getToolbarManager().showToolbar();
        }
        mainActivity.getToolbarManager().showBackButton();
        mainActivity.setTitle(getResources().getString(R.string.hidden_apps));
    }

    private void initList() {
        mAdapter = new DragDropFavoriteAppsListAdapter(mainActivity, hiddenApps, mainActivity.getDbManager());
        mAdapter.setIsHideAppsAdapter(true);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        tvEmptyView.setText(mainActivity.getResources().getString(R.string.app_hidden_empty_view));
        mListView.setEmptyView(tvEmptyView);
    }

    private void unHideApps() {
        if (!appsToUnhide.isEmpty()) {
            for (AppModel appModel : appsToUnhide) {
                if (appModel.isChecked()) {
                    mainActivity.getDbManager().hideOrShowApp(appModel.getAppPackageName(), false);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (AppModel appModel : hiddenApps) {
            if (isChecked) {
                if (!appModel.isChecked()) {
                    appModel.setIsChecked(true);
                    appsToUnhide.add(appModel);
                }
            } else {
                if (appModel.isChecked()) {
                    appModel.setIsChecked(false);
                    if (!appsToUnhide.isEmpty() && appsToUnhide.contains(appModel)) {
                        appsToUnhide.remove(appModel);
                    }
                }
            }
        }
        checkValidateUnhideButton();
        mAdapter.notifyDataSetChanged();
    }
}
