package com.watchappz.android.system.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.watchappz.android.R;
import com.watchappz.android.utils.FragmentNavigator;
import com.watchappz.android.utils.ToolbarManager;

/**
 * Created by
 * mRogach on 17.09.2015.
 */

public class BaseActivity extends AppCompatActivity {

    protected ToolbarManager mToolbarManager;
    protected FragmentNavigator mFragmentNavigator;
    protected FloatingActionsMenu faMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initFragmentNavigator();
        faMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu_AM);
    }

    private void initToolbar() {
        mToolbarManager = new ToolbarManager(this);
        mToolbarManager.initToolbar();
    }

    public ToolbarManager getToolbarManager() {
        return mToolbarManager;
    }

    private void initFragmentNavigator() {
        mFragmentNavigator = new FragmentNavigator();
        mFragmentNavigator.register(getSupportFragmentManager(), R.id.flContainer_AM);
    }

    public FragmentNavigator getFragmentNavigator() {
        return mFragmentNavigator;
    }

    protected void setDrawable(final View _view, final int _idView, final int _idDrawable) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            _view.findViewById(_idView).setBackground(getResources()
                    .getDrawable(_idDrawable, getTheme()));
        } else {
            _view.findViewById(_idView).setBackground(getResources().getDrawable(_idDrawable));
        }
    }
}
