package com.watchappz.android.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.watchappz.android.global.Variables;
import com.watchappz.android.system.activities.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by
 * mRogach on 06.10.2015.
 */

public final class FacebookShareManager {

    private MainActivity mainActivity;

    public FacebookShareManager(final Activity _activity) {
        mainActivity = (MainActivity) _activity;
    }

    public void shareToFacebook() {
        SharePhoto photo5 = new SharePhoto.Builder()
                .setBitmap(Variables.bitmap)
                .setCaption("Favorite apps")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo5)
                .build();

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Hello Facebook")
                .setContentDescription(
                        "The 'Hello Facebook' sample  showcases simple Facebook integration")
                .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                .build();
        ShareApi.share(linkContent, facebookCallback);

    }

    FacebookCallback<Sharer.Result> facebookCallback =  new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Toast.makeText(mainActivity, "Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Log.v("FACEBOOK_TEST", "share api cancel");
            Toast.makeText(mainActivity, "onCancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            Log.v("FACEBOOK_TEST", "share api error " + e);
            Toast.makeText(mainActivity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
