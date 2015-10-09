package com.watchappz.android.system.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.watchappz.android.R;

/**
 * Created by
 * mRogach on 18.09.2015.
 */

public final class AboutWatchAppzFragment extends BaseFragment {

    private ImageView ivLogo;
    private TextView tvAppVersion;

    public static AboutWatchAppzFragment newInstance() {
        return new AboutWatchAppzFragment();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvAppVersion.setText(mainActivity.getResources().getString(R.string.app_version) + " " + getAppVersion());
        mainActivity.getToolbarManager().hideToolbar();
        mainActivity.setFloatingMenuVisibility(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.fragment_about, container, false);
        findViews();
        return mInflatedView;
    }

    private void findViews() {
        ivLogo = (ImageView) mInflatedView.findViewById(R.id.ivLogo_FA);
        tvAppVersion = (TextView) mInflatedView.findViewById(R.id.tvAppVersion_FA);
    }

    private String getAppVersion() {
        PackageInfo pInfo;
        String version = null;
        try {
            pInfo = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
