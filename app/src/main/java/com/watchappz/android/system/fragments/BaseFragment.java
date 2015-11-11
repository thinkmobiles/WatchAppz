package com.watchappz.android.system.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.watchappz.android.R;
import com.watchappz.android.system.activities.MainActivity;

/**
 * Created by
 * mRogach on 15.09.2015.
 */

public class BaseFragment extends Fragment {

    protected MainActivity mainActivity;
    protected View mInflatedView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    protected int getColor(int _idRes) {
        return ContextCompat.getColor(mainActivity, _idRes);
    }
}
