package com.watchappz.android.system.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.fragments.AllAppsListFragment;
import com.watchappz.android.system.fragments.FavoriteAppsListFragment;
import com.watchappz.android.system.fragments.RecentlyUsedAppsListFragment;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public final class AppsListFragmentsPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private String tabTitles[];

    public AppsListFragmentsPagerAdapter(final FragmentManager _fm, final Context _context) {
        super(_fm);
        mContext = _context;
        tabTitles = new String[] {mContext.getResources().getString(R.string.tab_favorieten),
                mContext.getResources().getString(R.string.tab_recently_used),
                mContext.getResources().getString(R.string.tab_all_apps)};
    }

    @Override
    public Fragment getItem(final int _position) {
        switch (_position) {
            case 0:
                return FavoriteAppsListFragment.newInstance();
            case 1:
                return RecentlyUsedAppsListFragment.newInstance();
            case 2:
                return AllAppsListFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return Constants.PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(final int _position) {
        return tabTitles[_position];
    }

}
