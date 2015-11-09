package com.watchappz.android.system.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.watchappz.android.R;
import com.watchappz.android.database.DBManager;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.models.AppModel;
import com.watchappz.android.utils.image_loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by
 * mRogach on 06.11.2015.
 */

public final class DragDropFavoriteAppsListAdapter extends ArrayAdapter<AppModel> {

    private Context mContext;
    private DBManager dbManager;
    private ImageLoader imageLoader;
    private List<AppModel> appModels;

    public DragDropFavoriteAppsListAdapter(Context mContext, final List<AppModel> _appModels, final DBManager _dbManager) {
        this.mContext = mContext;
        dbManager = _dbManager;
        imageLoader = new ImageLoader(mContext);
        appModels = _appModels;
        addAll(appModels);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        AppViewHolder viewHolder;
        if (_convertView == null) {
            _convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_apps, _parent, false);
            viewHolder = new AppViewHolder();
            findViews(viewHolder, _convertView);
            _convertView.setTag(viewHolder);
        } else {
            viewHolder = (AppViewHolder) _convertView.getTag();
        }
        updateView(viewHolder, _position);
        return _convertView;
    }

    private static class AppViewHolder {

        private TextView tvAppName, tvAppInfo;
        private ImageView ivAppIcon;
        private ImageView btnAppStar;

    }

    private void findViews(final AppViewHolder appViewHolder, final View _convertView) {
        appViewHolder.ivAppIcon = (ImageView) _convertView.findViewById(R.id.ivAppIcon_LIA);
        appViewHolder.btnAppStar = (ImageView) _convertView.findViewById(R.id.btnAppStar_LIA);
        appViewHolder.tvAppName = (TextView) _convertView.findViewById(R.id.tvAppName_LIA);
        appViewHolder.tvAppInfo = (TextView) _convertView.findViewById(R.id.tvAppInfo_LIA);
    }

    private void updateView(final AppViewHolder appViewHolder, final int _position) {
        final AppModel appModel = getItem(_position);
        appViewHolder.tvAppName.setText(appModel.getAppName());
        appViewHolder.tvAppInfo.setText(getAppInfo(appModel));
        setStarColor(appViewHolder, appModel);
        setAppIcon(appViewHolder, appModel);

        appViewHolder.btnAppStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(appViewHolder.btnAppStar, appModel.getAppPackageName());
            }
        });
    }

    private String getAppInfo(final AppModel _appModel) {
        return String.format(mContext.getResources().getString(R.string.app_used) +
                        ": %d x " + mContext.getResources().getString(R.string.app_use_today) +
                        " | %d x " + mContext.getResources().getString(R.string.app_use_total),
                _appModel.getAppUseTodayCount(), _appModel.getAppUseTotalCount());
    }

    private void setStarColor(final AppViewHolder appViewHolder, final AppModel _appModel) {
        if (_appModel.isFavourite() == 1) {
            setDrawable(appViewHolder.btnAppStar, R.drawable.ic_star_gold);
        } else {
            setDrawable(appViewHolder.btnAppStar, R.drawable.ic_star_grey);
        }
    }

    private void setAppIcon(final AppViewHolder appViewHolder, final AppModel _appModel) {
        imageLoader.displayImage(_appModel.getAppPackageName(), appViewHolder.ivAppIcon);
    }

    private void showDialog(final ImageView v, final String _packageName) {
        final AppModel appModel = dbManager.getApp(_packageName);
        new MaterialDialog.Builder(mContext)
                .backgroundColorRes(android.R.color.white)
                .title(mContext.getResources().getString(R.string.tab_favorieten))
                .titleColorRes(android.R.color.black)
                .content(mContext.getResources().getString(R.string.app_favorite_add) + " / "
                        + mContext.getResources().getString(R.string.app_favorite_remove))
                .contentColorRes(android.R.color.black)
                .positiveText(mContext.getResources().getString(R.string.app_favorite_add))
                .negativeText(mContext.getResources().getString(R.string.app_favorite_remove))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (appModel != null && appModel.isFavourite() == 0) {
                            dbManager.addToFavoriteByTap(_packageName);
                            setDrawable(v, R.drawable.ic_star_gold);
                            sendSearchBroadcastFavorute();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        if (appModel != null && appModel.isFavourite() == 1) {
                            dbManager.removeFromFavorite(_packageName);
                            setDrawable(v, R.drawable.ic_star_grey);
                            sendSearchBroadcastFavorute();
                        }
                    }
                })
                .show();
    }

    private void setDrawable(final ImageView _view, final int _idDrawable) {
        _view.setImageResource(_idDrawable);
    }

    private void sendSearchBroadcastFavorute() {
        Intent broadcastIntent = new Intent(Constants.FAVORITE_CLICK);
        mContext.sendBroadcast(broadcastIntent);
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter(){
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                constraint = constraint.toString().toLowerCase();
//                FilterResults result = new Filter.FilterResults();
//
//                if (constraint != null && constraint.toString().length() > 0) {
//                    List<AppModel> founded = new ArrayList<>();
//                    for(AppModel item: appModels){
//                        if(item.toString().toLowerCase().contains(constraint)){
//                            founded.add(item);
//                        }
//                    }
//
//                    result.values = founded;
//                    result.count = founded.size();
//                }else {
//                    result.values = appModels;
//                    result.count = appModels.size();
//                }
//                return result;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                clear();
//                for (AppModel item : (List<AppModel>) results.values) {
//                    add(item);
//                }
//                notifyDataSetChanged();
//
//            }
//
//        };
//    }
}
