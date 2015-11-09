package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.watchappz.android.R;
import com.watchappz.android.system.adapters.DragDropFavoriteAppsListAdapter;
import com.watchappz.android.system.models.AppModel;
import java.util.List;

/**
 * Created by
 * mRogach on 06.11.2015.
 */

public final class DragAndDropFavoriteListFragment extends BaseFragment {

    private DragDropFavoriteAppsListAdapter adapter;
    private List<AppModel> appModels;

    public static DragAndDropFavoriteListFragment newInstance() {
        return new DragAndDropFavoriteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_drag_and_drop, container, false);
        return mInflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DynamicListView listView = (DynamicListView) mainActivity.findViewById(R.id.lvFavoriteApps_FDAD);
        appModels = mainActivity.getDbManager().getFavoriteList();
        adapter = new DragDropFavoriteAppsListAdapter(mainActivity, appModels, mainActivity.getDbManager());
        listView.setAdapter(adapter);
        listView.enableDragAndDrop();
        listView.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));
    }

    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mListView != null) {
                mListView.startDragging(position - mListView.getHeaderViewsCount());
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

        }
    }

}
