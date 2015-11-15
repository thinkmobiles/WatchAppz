package com.watchappz.android.system.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.interfaces.SortInTabLayoutListener;
import com.watchappz.android.system.adapters.AppsListAdapter;
import com.watchappz.android.system.adapters.DragDropFavoriteAppsListAdapter;
import com.watchappz.android.system.adapters.FavoriteAdapter;
import com.watchappz.android.system.models.AppModel;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by
 * mRogach on 17.09.2015.
 */
public class BaseAppsFragment extends BaseFragment {

    protected DynamicListView listView;
    protected TextView tvEmptyView;
    protected RelativeLayout rlAppsContainer;
    protected DragDropFavoriteAppsListAdapter dragDropFavoriteAppsListAdapter;
    protected FavoriteAdapter favoriteAdapter;
    protected IntentFilter mSearchFilter = new IntentFilter(Constants.QUERY);
    protected IntentFilter mFavoriteFilter = new IntentFilter(Constants.FAVORITE_CLICK);
    protected SearchManager searchManager;
    protected SearchView mSearchView;
    protected boolean isNewAccessibilityEvent = false;
    protected LinearLayout llDefault, llData, llTimeUsed, llSortTabLayout;
    protected TextView tvDefault, tvData, tvTimeUsed;
    protected ImageView ivArrowDefault, ivArrowData, ivArrowTimeUsed;
    protected SortInTabLayoutListener sortInTabLayoutListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_apps, container, false);
        findViews();
        return mInflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sortInTabLayoutListener = (SortInTabLayoutListener) getActivity();
    }

    private void findViews() {
        listView = (DynamicListView) mInflatedView.findViewById(R.id.lvFavoriteApps_FA);
        tvEmptyView = (TextView) mInflatedView.findViewById(R.id.tvEmptyAppsList_EV);
        rlAppsContainer = (RelativeLayout) mInflatedView.findViewById(R.id.rlAppsContainer_FA);
        llSortTabLayout = (LinearLayout) mInflatedView.findViewById(R.id.llSortTabLayout_FVP);
        llDefault = (LinearLayout) mInflatedView.findViewById(R.id.llDefault_FVP);
        llData = (LinearLayout) mInflatedView.findViewById(R.id.llData_FVP);
        llTimeUsed = (LinearLayout) mInflatedView.findViewById(R.id.llTime_FVP);
        tvDefault = (TextView) mInflatedView.findViewById(R.id.tvDefault_FVP);
        tvData = (TextView) mInflatedView.findViewById(R.id.tvData_FVP);
        tvTimeUsed = (TextView) mInflatedView.findViewById(R.id.tvTime_FVP);
        ivArrowDefault = (ImageView) mInflatedView.findViewById(R.id.ivArrowDefault_FVP);
        ivArrowData = (ImageView) mInflatedView.findViewById(R.id.ivArrowData_FVP);
        ivArrowTimeUsed = (ImageView) mInflatedView.findViewById(R.id.ivArrowTime_FVP);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = mainActivity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) searchItem.getActionView();
    }

    protected void initDragAndDropAdapter(final List<AppModel> _appModels) {
        dragDropFavoriteAppsListAdapter = new DragDropFavoriteAppsListAdapter(mainActivity, _appModels, mainActivity.getDbManager());
        listView.setAdapter(dragDropFavoriteAppsListAdapter);
        listView.enableDragAndDrop();
        listView.setTextFilterEnabled(true);
        listView.setOnItemMovedListener(new MyOnItemMovedListener(dragDropFavoriteAppsListAdapter));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));
    }

    protected void initFavoriteAdapter(final List<AppModel> _appModels) {
        favoriteAdapter = new FavoriteAdapter(mainActivity, _appModels, mainActivity.getDbManager());
        listView.setAdapter(favoriteAdapter);
        listView.enableDragAndDrop();
        listView.setTextFilterEnabled(true);
        listView.setOnItemMovedListener(new MyOnItemMovedListener(favoriteAdapter));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));
    }

    protected void setEmptyView(final int _id) {
        tvEmptyView.setText(mainActivity.getResources().getString(_id));
        listView.setEmptyView(tvEmptyView);
    }

    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView favoriteListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            favoriteListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (favoriteListView != null) {
                favoriteListView.startDragging(position - favoriteListView.getHeaderViewsCount());
            }
            return true;
        }
    }

    private class MyOnItemMovedListener implements OnItemMovedListener {

        private final DragDropFavoriteAppsListAdapter mAdapter;

        MyOnItemMovedListener(final DragDropFavoriteAppsListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int originalPosition, final int newPosition) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    mainActivity.getDbManager().updateAppPosition(mAdapter.getItem(newPosition).getAppPackageName(), newPosition);
                }
            });
            t.start();
//            mAdapter.getItem(newPosition).setAppPosition(newPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    protected void setUpArrow(final LinearLayout _layout, final TextView _textView, final ImageView _arrow) {
        _layout.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
        _textView.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
        _arrow.setImageResource(R.drawable.ic_arrow_black_up);
    }

    protected void setDefaultColors(final LinearLayout _layout, final TextView _textView, final ImageView _arrow) {
        _layout.setBackgroundColor(getColor(R.color.sort_tab_layout_background));
        _textView.setTextColor(getColor(R.color.sort_tab_title_text_color));
        _arrow.setImageResource(R.drawable.ic_arrow_grey_down);
    }

    protected void setCheckedColors(final LinearLayout _layout, final TextView _textView, final ImageView _arrow) {
        _layout.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
        _textView.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
        _arrow.setImageResource(R.drawable.ic_arrow_black_down);
    }

    protected void setPressedViews() {
        switch (mainActivity.getSortType()) {
            case Constants.SORT_TYPE_DEFAULT:
                setCheckedColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_DEFAULT_DESC:
                llDefault.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
                tvDefault.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
                ivArrowDefault.setImageResource(R.drawable.ic_arrow_black_up);
                setDefaultColors(llData, tvData, ivArrowData);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_DATA_MENU:
                setCheckedColors(llData, tvData, ivArrowData);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_DATA_MENU_ASC:
                llData.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
                tvData.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
                ivArrowData.setImageResource(R.drawable.ic_arrow_black_up);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                break;
            case Constants.SORT_TYPE_TIME_USED:
                setCheckedColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                break;
            case Constants.SORT_TYPE_TIME_USED_ASC:
                llTimeUsed.setBackgroundColor(getColor(R.color.sort_tab_layout_background_pressed));
                tvTimeUsed.setTextColor(getColor(R.color.sort_tab_title_text_color_pressed));
                ivArrowTimeUsed.setImageResource(R.drawable.ic_arrow_black_up);
                setDefaultColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                break;
            default:
                setCheckedColors(llDefault, tvDefault, ivArrowDefault);
                setDefaultColors(llData, tvData, ivArrowData);
                setDefaultColors(llTimeUsed, tvTimeUsed, ivArrowTimeUsed);
        }
    }
}
