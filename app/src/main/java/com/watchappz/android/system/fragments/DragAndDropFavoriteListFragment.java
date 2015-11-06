package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.watchappz.android.R;
import com.watchappz.android.system.adapters.DragDropFavoriteAppsListAdapter;
import com.watchappz.android.system.models.AppModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by
 * mRogach on 06.11.2015.
 */

public final class DragAndDropFavoriteListFragment extends BaseFragment {

    private static final int INITIAL_DELAY_MILLIS = 300;
    private DragDropFavoriteAppsListAdapter adapter;
    private int mNewItemCount;
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
        adapter = new DragDropFavoriteAppsListAdapter(mainActivity, appModels);
//        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(adapter, mainActivity, new MyOnDismissCallback(adapter));
        AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(animAdapter);

        /* Enable drag and drop functionality */
        listView.enableDragAndDrop();
//        listView.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_touchview));
        listView.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));

        /* Enable swipe to dismiss */
//        listView.enableSimpleSwipeUndo();

        /* Add new items on item click */
        listView.setOnItemClickListener(new MyOnItemClickListener(listView));
    }

    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mListView != null) {
                mListView.startDragging(position);
            }
            return true;
        }
    }

    private class MyOnDismissCallback implements OnDismissCallback {

        private final DragDropFavoriteAppsListAdapter mAdapter;

        @Nullable
        private Toast mToast;

        MyOnDismissCallback(final DragDropFavoriteAppsListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                mAdapter.remove(position);
            }

            if (mToast != null) {
                mToast.cancel();
            }
//            mToast = Toast.makeText(mainActivity, getString(R.string.removed_positions, Arrays.toString(reverseSortedPositions)),
//                    Toast.LENGTH_LONG
//            );
//            mToast.show();
        }
    }

    private class MyOnItemMovedListener implements OnItemMovedListener {

        private final DragDropFavoriteAppsListAdapter mAdapter;

        private Toast mToast;

        MyOnItemMovedListener(final DragDropFavoriteAppsListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int originalPosition, final int newPosition) {
            if (mToast != null) {
                mToast.cancel();
            }

//            mToast = Toast.makeText(mainActivity, getString(R.string.moved, mAdapter.getItem(newPosition), newPosition), Toast.LENGTH_SHORT);
//            mToast.show();
        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        private final DynamicListView mListView;

        MyOnItemClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mListView.insert(position, adapter.getItem(position));
            mNewItemCount++;
        }
    }
}
