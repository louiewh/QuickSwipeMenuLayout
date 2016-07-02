package com.example.swipemenuview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by louiewh on 16/6/16.
 */
public class SwipeMenuLayout extends FrameLayout {
    final static String TAG = "SwipeMenuLayout";

    private View mLeftMenuView;
    private View mRightMenuView;
    private View mContextView;

    private int mLeftMenuViewId;
    private int mRightMenuViewId;
    private int mContextViewId;

    final String LEFTMENUVIEW  = "swipe_left_menu";
    final String RIGHTMENUVIEW = "swipe_right_menu";
    final String CONTEXTVIEW   = "swipe_context";

    private float mDownX;
    private int mLeftMargin;
    private int mRightMargin;
    private int mTouchSlop;
    private int mPosition;
    private int defScrollTime = 500;
    private int mScrollTime;
    private boolean mAutoMenu;
    private boolean mMenuShow;

    private ScrollerCompat  mScroller;
    private OnMenuClickListener  mOnMenuClickListener;

    static  public SwipeMenuLayout mSlideView;

    public SwipeMenuLayout(Context context) {
        super(context);
        initUI();
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initUI();
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initUI();
    }

    private void initAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout);
        mScrollTime = a.getInt(R.styleable.SwipeMenuLayout_duration, defScrollTime);
        mAutoMenu   = a.getBoolean(R.styleable.SwipeMenuLayout_auto_menu, true);
    }

    private void initUI() {
        mScroller = ScrollerCompat.create(getContext());

        ViewConfiguration config = ViewConfiguration.get(getContext());
        mTouchSlop = config.getScaledTouchSlop();

        mLeftMenuViewId  = getContext().getResources().getIdentifier(LEFTMENUVIEW, "id", getContext().getPackageName());
        mRightMenuViewId = getContext().getResources().getIdentifier(RIGHTMENUVIEW, "id", getContext().getPackageName());
        mContextViewId   = getContext().getResources().getIdentifier(CONTEXTVIEW, "id", getContext().getPackageName());

        if (mLeftMenuViewId == 0 || mRightMenuViewId == 0 || mContextViewId == 0) {
            throw new RuntimeException(String.format("initUI Exception" ));
        }
    }

    public void initView() {

        if(mLeftMenuView == null && mLeftMenuViewId != View.NO_ID) {
            mLeftMenuView = this.findViewById(mLeftMenuViewId);
        }

        if(mRightMenuView == null && mRightMenuViewId != View.NO_ID) {
            mRightMenuView = this.findViewById(mRightMenuViewId);
        }

        if(mContextView == null && mContextViewId != View.NO_ID)
            mContextView = this.findViewById(mContextViewId);

    }

    private void menuViewShow() {
        if(mLeftMenuView != null ) {
            mLeftMenuView.setVisibility(View.VISIBLE);
        }

        if(mRightMenuView != null) {
            mRightMenuView.setVisibility(View.VISIBLE);
        }

        mMenuShow = true;
    }

    private void menuViewHide() {
        if(mLeftMenuView != null ) {
            mLeftMenuView.setVisibility(View.INVISIBLE);
        }

        if(mRightMenuView != null) {
            mRightMenuView.setVisibility(View.INVISIBLE);
        }

        mMenuShow = false;
    }

    public void setLeftMenuView(View view) {
        this.mLeftMenuView = view;
    }

    public void setRightMenuView(View view) {
        this.mRightMenuView = view;
    }

    public void setContextView(View view) {
        this.mContextView = view;
    }

    public void setOnMenuClickListener(OnMenuClickListener  listener) {
       mOnMenuClickListener = listener;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();

                if(mLeftMenuView != null)
                    mLeftMargin = mLeftMenuView.getWidth();

                if(mRightMenuView != null)
                    mRightMargin = mRightMenuView.getWidth();

                if(mSlideView != null && this != mSlideView && mSlideView.isMenuOpen())
                    mSlideView.closeMenu();

                Log.d(TAG, "Event ACTION_DOWN! mMenuShow:" + mMenuShow);
                super.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getX() - mDownX);
                if(Math.abs(dx) < mTouchSlop)
                    break;

                if(!mMenuShow ) {
                    menuViewShow();
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                if(dx > 0 && mLeftMenuView == null) break;
                if(dx < 0 && mRightMenuView == null) break;


                mSlideView = this;
                if(dx > 0 && dx > mLeftMargin) {
                    dx = mLeftMargin;
                    registerListener(mLeftMenuView);
                } else if (dx < 0 && dx < -mRightMargin) {
                    dx = -mRightMargin;
                    registerListener(mRightMenuView);
                }

                mContextView.layout(dx, 0, mContextView.getMeasuredWidth()+dx, mContextView.getMeasuredHeight());
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "Event ACTION_CANCEL! mMenuShow:" + mMenuShow);
            case MotionEvent.ACTION_UP:
                int dis = mContextView.getLeft();
                /**
                 *  dis > 0, move to right, dis < mLeftMargin/2, close menu, dis < mLeftMargin open menu.
                 *  dis < 0, move to left, dis > -mRightMargin/2, close menu, dis > -mRightMargin, open menu
                 */
                if(dis > 0 && mLeftMenuView != null) {
                    if(dis < mLeftMargin/2) {
                        mScroller.startScroll(dis, 0, -dis, 0, mScrollTime);
                    } else if(dis < mLeftMargin) {
                        mScroller.startScroll(dis, 0, mLeftMargin-dis, 0, mScrollTime);
                        registerListener(mLeftMenuView);
                    }

                    postInvalidate();
                } else if(dis < 0 && mRightMenuView != null) {
                    if(dis > -mRightMargin/2) {
                        mScroller.startScroll(dis, 0, -dis, 0, mScrollTime);  //close
                    } else if(dis > -mRightMargin) {
                        mScroller.startScroll(dis, 0, -mRightMargin-dis, 0, mScrollTime);
                        registerListener(mRightMenuView);
                    }

                    postInvalidate();
                }

                mDownX = 0;
                Log.d(TAG, "Event ACTION_UP! mMenuShow:" + mMenuShow);
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mAutoMenu)
            initView();
        menuViewHide();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if(mScroller.computeScrollOffset()) {
            layoutContextView(mScroller.getCurrX());
            postInvalidate();
        }
    }

    private void layoutContextView(int dx) {
        if(mContextView != null)
            mContextView.layout(dx, 0, mContextView.getMeasuredWidth()+dx, mContextView.getMeasuredHeight());
    }

    public boolean isMenuOpen() {
        int dis = mContextView.getLeft();
        if(dis == mLeftMargin || dis == -mRightMargin )
            return true;

        return false;
    }

    public  void closeMenu () {
        while(mScroller.computeScrollOffset()) {
            mScroller.abortAnimation();
        }
        layoutContextView(0);
        menuViewHide();
        unregisterListener();
    }

    private void registerListener(View view) {
        if(view != null ) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMenuClickListener != null)
                        mOnMenuClickListener.onMenuClick(v, mPosition);
                }
            });
        }
    }

    private void unregisterListener() {
        if(mLeftMenuView != null ) {
            mLeftMenuView.setOnClickListener(null);
        }

        if(mRightMenuView != null ) {
            mRightMenuView.setOnClickListener(null);
        }
    }
    public static interface OnMenuClickListener {

        void onMenuClick(View v, int position);
    }

    public static void clearSideView() {
        if (mSlideView != null) {
            mSlideView.unregisterListener();
        }
        mSlideView = null;
    }
}
