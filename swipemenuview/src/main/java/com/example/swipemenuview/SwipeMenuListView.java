package com.example.swipemenuview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by louiewh on 16/6/16.
 */
public class SwipeMenuListView extends ListView{

    private Adapter mAdapter;
    private View mTouchView;
    private View mContentView;

    private float mDownX;
    private float mDownY;

    private static final int CONTENT_VIEW_ID = 1;
    private static final int MENU_VIEW_ID = 2;

    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_Y = 5;
    private int MAX_X = 3;
    private int mTouchState;
    private int mTouchPosition;

    public SwipeMenuListView(Context context) {
        super(context);
//        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
//        mTouchSlop = configuration.getScaledTouchSlop();

    }

    public SwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.d("louie", "SwipeMenuListView onInterceptTouchEvent Event ACTION_UP!");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("louie", "SwipeMenuListView onInterceptTouchEvent Event ACTION_MOVE!");
                break;
            default:
                break;
        }
        boolean result = super.onInterceptTouchEvent(ev);
        Log.d("louie", "SwipeMenuListView onInterceptTouchEvent result:"+result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
//            return super.onTouchEvent(ev);
//
//        Log.d("louie", "onTouchEvent Action:"+ev.getAction());
//        switch(ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = ev.getX();
//                mDownY = ev.getY();
//                mTouchState = TOUCH_STATE_NONE;
//                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
//
//                if(mTouchView != null && ((SwipeMenuLayout)mTouchView).isMenuOpen()) {
//                    ((SwipeMenuLayout)mTouchView).closeMenu();
//                }
//
//                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
//                if(view instanceof SwipeMenuLayout && mTouchView != view) {
//
//                    mTouchView = view;
//                }
//
//                return mTouchView.onTouchEvent(ev);
//            case MotionEvent.ACTION_MOVE:
//                super.onTouchEvent(ev);
//                return mTouchView.onTouchEvent(ev);
//            case MotionEvent.ACTION_UP:
//                mTouchView.onTouchEvent(ev);
//                break;
//            default:
//                super.onTouchEvent(ev);
//        }

        return super.onTouchEvent(ev);
    }
}
