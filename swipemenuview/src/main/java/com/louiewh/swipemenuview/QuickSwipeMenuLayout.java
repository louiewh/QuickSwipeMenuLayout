package com.louiewh.swipemenuview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by louiewh on 16/6/16.
 */
public class QuickSwipeMenuLayout extends FrameLayout {
    public final static String TAG = "QuickSwipeMenuLayout";

    private View mMenuLeftView;
    private View mMenuRightView;
    private View mContextView;

    private int mIdMenuLeft;
    private int mIdMenuRight;
    private int mIdContext;

    private float mDownX;
    private int mLeftMargin;
    private int mRightMargin;

    private int mTouchSlop;
    private int mInitLeft;
    private int mScrollTime = 500;

    private ScrollerCompat  mScroller;
    private static QuickSwipeMenuLayout sOpenQuickSwipeMenuLayout;

    public QuickSwipeMenuLayout(Context context) {
        super(context);
    }

    public QuickSwipeMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public QuickSwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs){
        mScroller = ScrollerCompat.create(getContext());
        ViewConfiguration config = ViewConfiguration.get(getContext());
        mTouchSlop = config.getScaledTouchSlop();

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout);
        mScrollTime = a.getInt(R.styleable.SwipeMenuLayout_swipe_scroll_duration, mScrollTime);

        mIdMenuLeft = a.getResourceId(R.styleable.SwipeMenuLayout_swipe_menu_left_id, View.NO_ID);
        mIdMenuRight = a.getResourceId(R.styleable.SwipeMenuLayout_swipe_menu_right_id, View.NO_ID);
        mIdContext = a.getResourceId(R.styleable.SwipeMenuLayout_swipe_context_id, View.NO_ID);

        a.recycle();
    }

    public void initView() {

        if(mMenuLeftView == null && mIdMenuLeft != View.NO_ID) {
            mMenuLeftView = this.findViewById(mIdMenuLeft);

            FrameLayout.LayoutParams layoutParams = (LayoutParams) mMenuLeftView.getLayoutParams();
            layoutParams.gravity = Gravity.START;
        }

        if(mMenuRightView == null && mIdMenuRight != View.NO_ID) {
            mMenuRightView = this.findViewById(mIdMenuRight);

            FrameLayout.LayoutParams layoutParams = (LayoutParams) mMenuRightView.getLayoutParams();
            layoutParams.gravity = Gravity.END;
        }

        if(mContextView == null && mIdContext != View.NO_ID) {
            mContextView = this.findViewById(mIdContext);
            this.bringChildToFront(mContextView);
        }

        if(mMenuLeftView != null){
            mLeftMargin = mMenuLeftView.getMeasuredWidth();
        }

        if(mMenuRightView != null){
            mRightMargin = mMenuRightView.getMeasuredWidth();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                if(mContextView != null) {
                    mInitLeft = mContextView.getLeft();
                }

                if(mInitLeft > 0 && mInitLeft == mLeftMargin){
                    if(mMenuRightView != null) {
                        RectF rectLeft = getViewScreenCoordinate(mMenuRightView);
                        if (rectLeft.contains(event.getRawX(), event.getRawY())) {
                            return true;
                        }
                    }

                    return super.onInterceptTouchEvent(event);
                } else if( mInitLeft < 0 && mInitLeft == -mRightMargin){
                    if(mMenuLeftView != null) {
                        RectF rectRight = getViewScreenCoordinate(mMenuLeftView);
                        if (rectRight.contains(event.getRawX(), event.getRawY())) {
                            return true;
                        }
                    }

                    return super.onInterceptTouchEvent(event);
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                if(sOpenQuickSwipeMenuLayout != null && sOpenQuickSwipeMenuLayout != this){
                    sOpenQuickSwipeMenuLayout.closeMenu();
                    sOpenQuickSwipeMenuLayout = this;
                }

                if(sOpenQuickSwipeMenuLayout == null){
                    sOpenQuickSwipeMenuLayout = this;
                }

                super.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getX() - mDownX);
                if(Math.abs(dx) < mTouchSlop){
                    break;
                }

                int layoutX = dx + mInitLeft;
                if(dx > 0){
                    layoutX -= mTouchSlop;
                } else {
                    layoutX += mTouchSlop;
                }

                getParent().requestDisallowInterceptTouchEvent(true);

                if(layoutX > 0 && mMenuLeftView == null) break;
                if(layoutX < 0 && mMenuRightView == null) break;

                if(layoutX > 0 && layoutX > mLeftMargin) {
                    layoutX = mLeftMargin;
                } else if (layoutX < 0 && layoutX < -mRightMargin) {
                    layoutX = -mRightMargin;
                }

                layoutContextView(layoutX);
                event.setAction(MotionEvent.ACTION_CANCEL);
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int dis = mContextView.getLeft();
                /*
                   dis > 0, move to right, dis < mLeftMargin/2, close menu, dis < mLeftMargin open menu.
                   dis < 0, move to left, dis > -mRightMargin/2, close menu, dis > -mRightMargin, open menu
                 */
                if(dis > 0 && mMenuLeftView != null) {

                    if(dis < mLeftMargin/2) {
                        mScroller.startScroll(dis, 0, -dis, 0, mScrollTime);
                    } else if(dis < mLeftMargin) {
                        mScroller.startScroll(dis, 0, mLeftMargin-dis, 0, mScrollTime);
                    }

                    postInvalidate();
                } else if(dis < 0 && mMenuRightView != null) {

                    if(dis > -mRightMargin/2) {
                        mScroller.startScroll(dis, 0, -dis, 0, mScrollTime);  //close
                    } else if(dis > -mRightMargin) {
                        mScroller.startScroll(dis, 0, -mRightMargin-dis, 0, mScrollTime);
                    }

                    postInvalidate();
                }

                mDownX = 0;
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initView();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if(mScroller.computeScrollOffset()) {
            layoutContextView(mScroller.getCurrX());
            postInvalidate();
        }
    }

    private void setEnableTouchEvent(View view, boolean enable){
        if(view != null) {
            view.setEnabled(enable);
            view.setClickable(enable);
            view.setLongClickable(enable);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setContextClickable(enable);   // api require >= 23
            }
        }
    }

    private void layoutContextView(int dx) {
        if(mContextView != null) {
            mContextView.layout(dx, 0, mContextView.getMeasuredWidth() + dx, mContextView.getMeasuredHeight());
        }
    }

    public void closeMenu(){
        if(mContextView != null ){

            if(mScroller.computeScrollOffset()){
                mScroller.abortAnimation();
            }

            int left = mContextView.getLeft();
            mScroller.startScroll(left, 0, -left, 0, mScrollTime);

            postInvalidate();
        }
    }

    private RectF getViewScreenCoordinate(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }

//    @Override
//    public void onViewAdded(View child) {
//        super.onViewAdded(child);
//
//            if(mMenuLeftView == null && mIdMenuLeft == child.getId()) {
//                mMenuLeftView = this.findViewById(mIdMenuLeft);
//
//                FrameLayout.LayoutParams layoutParams = (LayoutParams) mMenuLeftView.getLayoutParams();
//                layoutParams.gravity = Gravity.START;
//            }
//
//            if(mMenuRightView == null && mIdMenuRight == child.getId()) {
//                mMenuRightView = this.findViewById(mIdMenuRight);
//
//                FrameLayout.LayoutParams layoutParams = (LayoutParams) mMenuLeftView.getLayoutParams();
//                layoutParams.gravity = Gravity.END;
//            }
//
//            if(mContextView == null && mIdContext != child.getId()) {
//                mContextView = this.findViewById(mIdContext);
//                this.bringChildToFront(mContextView);
//            }
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(sOpenQuickSwipeMenuLayout != null){
            sOpenQuickSwipeMenuLayout = null;
        }
    }
}
