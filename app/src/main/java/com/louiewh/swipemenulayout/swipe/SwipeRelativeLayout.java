package com.louiewh.swipemenulayout.swipe;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by louie.wang on 2017/9/8.
 */

public class SwipeRelativeLayout extends RelativeLayout {

    final static String TAG = "SwipeRelativeLayout";

    public SwipeRelativeLayout(Context context) {
        super(context);
    }

    public SwipeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);
        Log.e(TAG, "dispatchTouchEvent:"+event.getAction()+"  result:"+result);

        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean result = super.onInterceptTouchEvent(event);
        Log.e(TAG, "onInterceptTouchEvent:"+event.getAction()+"  result:"+result);

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        Log.e(TAG, "onTouchEvent:"+event.getAction()+" result:"+result);

        return result;
    }
}
