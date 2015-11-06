package com.watchappz.android.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.watchappz.android.system.activities.MainActivity;

/**
 * Created by
 * mRogach on 06.10.2015.
 */

public final class FacebookShareManager {

    private MainActivity mainActivity;
    private ShareDialog shareDialog;

    public FacebookShareManager(final Activity _activity) {
        mainActivity = (MainActivity) _activity;
        shareDialog = new ShareDialog(mainActivity);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, facebookCallback);
    }

    public void shareToFacebook(final Bitmap _bitmap) {
        SharePhoto photo5 = new SharePhoto.Builder()
                .setBitmap(_bitmap)
                .setCaption("Favorite apps")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo5)
                .build();

        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(content);
        } else {
            Toast.makeText(mainActivity, "Cant share", Toast.LENGTH_SHORT).show();
        }

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
