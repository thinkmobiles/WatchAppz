package com.watchappz.android.utils.custom_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

/**
 * Created by
 * mRogach on 19.11.2015.
 */

public final class CustomDynamicListView extends DynamicListView {

    private String TAG = "CustomDynamicListView";

    public CustomDynamicListView(Context context) {
        super(context);
    }

    public CustomDynamicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDynamicListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev)
    {
        try
        {
            return super.dispatchTouchEvent(ev);
        }
        catch (IllegalStateException e)
        {
            Log.e(TAG, e.getMessage());
        }
        return true;
    }
}
