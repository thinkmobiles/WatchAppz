package com.watchappz.android.system.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.watchappz.android.R;
import com.watchappz.android.global.Constants;
import com.watchappz.android.system.fragments.AppsListFragment;

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
                mContext.getResources().getString(R.string.tab_recent_gebruikt),
                mContext.getResources().getString(R.string.tab_alle_apps)};
    }

    @Override
    public Fragment getItem(final int _position) {
        switch (_position) {
            case 0:
                return AppsListFragment.newInstance();
            case 1:
                return AppsListFragment.newInstance();
            case 2:
                return AppsListFragment.newInstance();
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
