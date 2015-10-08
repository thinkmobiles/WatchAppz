package com.watchappz.android.system.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.watchappz.android.R;

/**
 * Created by
 * mRogach on 07.10.2015.
 */

public class AboutWatchAppzActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);
        findViews();
        tvAppVersion.setText(getResources().getString(R.string.app_version) + " " + getAppVersion());
    }

    private void findViews() {
        ivLogo = (ImageView) findViewById(R.id.ivLogo_FA);
        tvAppVersion = (TextView) findViewById(R.id.tvAppVersion_FA);
    }

    private String getAppVersion() {
        PackageInfo pInfo;
        String version = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
