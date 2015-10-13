package com.watchappz.android.system.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.watchappz.android.R;

/**
 * Created by
 * mRogach on 07.10.2015.
 */

public class AboutWatchAppzActivity extends AppCompatActivity {

    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);
        findViews();
        tvAppVersion.setText(String.format("%s %s", getResources().getString(R.string.app_version), getAppVersion()));
    }

    private void findViews() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
