package com.watchappz.android.system.filters;

import android.widget.Filter;

import com.watchappz.android.system.adapters.DragDropFavoriteAppsListAdapter;
import com.watchappz.android.system.models.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * mRogach on 15.11.2015.
 */

public final class AppFilter extends Filter {

    private List<AppModel> appModels;
    private DragDropFavoriteAppsListAdapter mAdapter;

    public AppFilter(DragDropFavoriteAppsListAdapter adapter, List<AppModel> appModels) {
        this.mAdapter = adapter;
        this.appModels = appModels;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        constraint = constraint.toString().toLowerCase();
        FilterResults result = new Filter.FilterResults();

        if (constraint.toString().length() > 0) {
            List<AppModel> founded = new ArrayList<>();
            for (AppModel item : appModels) {
                if (item.getAppName().toLowerCase().contains(constraint)) {
                    founded.add(item);
                }
            }
            result.values = founded;
            result.count = founded.size();
        } else {
            result.values = appModels;
            result.count = appModels.size();
        }
        return result;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.clear();
        for (AppModel item : (List<AppModel>) results.values) {
            mAdapter.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }
}
