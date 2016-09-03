# SwipeMenuLayout
View for listView item

##一 介绍

     ListView 通常用来展示多个个体，比如QQ 微信中的联系人列表。一个比较常见的功能是侧滑删除。这个功能属于比较常见的一个菜单，网络上也有很多实现。
#### 1 scroller 方式。

    最常见的一个实现是ListView 的Item View 为一个LinerLayout, 菜单在LinerLayout的最右端超出屏幕的位置，当手指滑动的时候，通过scrollTo 的方法在ListView 中控制Item View 的滑动，使菜单滑动出来。但是在IOS 上菜单是隐藏在Item View 的下面，层叠式的，当滑动的时候不是拉出来的方式，而是显示出来。
#### 2.NineOldAndroids

    在属性动画没有加入android的远古时代，github 上有一个NineOldAndroids项目，有人通过这个实现一个和IOS接近，其原理是FrameLayout， context 为显示的内容，menu嵌套在context下面，属性动画的方式移动context。
#### 3.SwipeMenuListView

    在github 上有一个 https://github.com/baoyongzhang/SwipeMenuListView 实现效果和1 类似，但是View 移动采用layout 方式，我修改了下 https://github.com/louiewh/SwipeMenuListView   实现效果和IOS 一样。但是总觉这几种方式都不太完美，要么效果打了折扣，要么代码量太大，方式复杂，通常需要重写ListView 和Adapter。
## 二 原理

     下面一个SwipeMenuLayout 大概不到300行的代码，完美实现ListView 的侧滑菜单。原理是继承FrameLayout， 作为ListView 的Item View 。在View中层叠两层，上层context View 为要显示的内容，下面menu View 为菜单。重写SwipeMenuLayout的OnTouchEvent ,在OnTouchEvent 中控制context 的移动。如图：
![image](https://www.processon.com/chart_image/5777d61fe4b08b6f021cd4a1.png)



####1. init View.
定义ListView 的Item View，如果使用左菜单:

1. 左菜单的ID为：swipe_left_menu
2. 右菜单ID：swipe_right_menu
3.  context 菜单ID：swipe_context
  
这个是默认ID，这样在SwipeMenuLayout会自动找到菜单View  ID :

```
    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initUI();
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
```

重写 onMeasure方法， 在onMeasure 方法中调用findViewByID， 为什么在onMeasure 而不是在构造函数中，因为在构造函数中View 的子View还没有初始化，findViewByID 为空。需要强调的是菜单View 在init 的时候设置为不可以见，这样在开始滑动的时候，菜单View不会干扰SwipeMenuLayout的滑动事件

```
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        initView();
        menuViewHide();
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

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnMenuClickListener != null) {
                    mOnMenuClickListener.onItemClick(v, mPosition);
                }
            }
        });

    }            
```

####2 处理滑动事件

    重写onTouchEvent 在down 事件中return true，拦截事件分发，在move 事件中判断滑动距离是否大于阀值，大于阀值，设置菜单View 可见，移动context View，在UP 事件中处理动画，根据滑动的距离和方向，开始对应的动画。
    
    1. cancle 事件的处理， ListView 的 OnInteruptTouchEvent 会判断View 的Y轴滑动距离，如果大于一定的距离，拦截事件，响应ListView 的上下滑动。按照android 的标准处理，cancle 按照up 事件处理。
    
    2. ListView 和 SwipeMenuLayout 的事件冲突，如果SwipeMenuLayout 进入侧滑后，上下滑动距离过大，ListView 会拦截事件，所以一旦进入侧滑模式，要禁止ListView 拦截事件.调用getParent().requestDisallowInterceptTouchEvent(true);
    

```
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();

                if(mLeftMenuView != null)
                    mLeftMargin = mLeftMenuView.getWidth();

                if(mRightMenuView != null)
                    mRightMargin = mRightMenuView.getWidth();

                if(mSlideView != null && this != mSlideView && mSlideView.isMenuOpen()) {
                    mSlideView.closeMenu();
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                
                Log.d(TAG, "Event ACTION_DOWN mMenuShow:" + mMenuShow);
                super.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getX() - mDownX);
                if(Math.abs(dx) < mTouchSlop)
                    break;

                if(!mMenuShow ) {
                    menuViewShow(dx);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mSlideView = this;
                }

                if(dx > 0 && mLeftMenuView == null) break;
                if(dx < 0 && mRightMenuView == null) break;


                if(dx > 0 && dx > mLeftMargin) {
                    dx = mLeftMargin;
                } else if (dx < 0 && dx < -mRightMargin) {
                    dx = -mRightMargin;
                }

                layoutContextView(dx);

                if(mMenuShow)
                    event.setAction(MotionEvent.ACTION_CANCEL);

                return super.onTouchEvent(event);
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "Event ACTION_CANCEL mMenuShow:" + mMenuShow);
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
                    }

                    postInvalidate();
                } else if(dis < 0 && mRightMenuView != null) {
                    if(dis > -mRightMargin/2) {
                        mScroller.startScroll(dis, 0, -dis, 0, mScrollTime);  //close
                    } else if(dis > -mRightMargin) {
                        mScroller.startScroll(dis, 0, -mRightMargin-dis, 0, mScrollTime);
                    }

                    postInvalidate();
                }

                mDownX = 0;
                Log.d(TAG, "Event ACTION_UP mMenuShow:" + mMenuShow);
                if(mMenuShow)
                    event.setAction(MotionEvent.ACTION_CANCEL);
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }
```

#### 3 contextView 的移动和动画

contextView 的移动方式采用layout 方法移动:

```
    private void layoutContextView(int dx) {
        if(mContextView != null)
            mContextView.layout(dx, 0, mContextView.getMeasuredWidth()+dx,     mContextView.getMeasuredHeight());

        if(!mResterListener && (dx == mLeftMargin || dx == -mRightMargin)) {
            Log.d(TAG, "registerListener dx:" + dx);
            registerListener(dx);
        } else if (mResterListener && (dx ==0 || (dx > 0 && dx != mLeftMargin ) || (dx < 0 && dx != -mRightMargin))) {
            Log.d(TAG, "unregisterListener dx:" + dx);
            unregisterListener(dx);
        }
    }
```

当滑动到一半的时候手里离开，这是菜单要关闭或者打开，动画采用Scroller 的方式，在UP 事件中调用mScroller.startScroll， 重写computerScroll

```
    public void computeScroll() {
        super.computeScroll();

        if(mScroller.computeScrollOffset()) {
            layoutContextView(mScroller.getCurrX());
            postInvalidate();
        }
    }
```

#### 4 监听事件的处理
1. 定义interface 和 事件注册，
2. menu 菜单在init 的时候已经设置不可见，在 layoutContextView 的时候，判断滑动距离，如果大于0 设置菜单menu 可见，如果滑动到了菜单宽度的位置，菜单完全打开，注册监听事件。避免menu View 和SwipeMenuLayout 滑动事件冲突。
3. 由于我们拦截了事件，ListView 的 OnItemClick 无法响应，因此需要我们单独写一个Listener，处理整个SwipeMenuLayout 的点击事件响应。把这个事件响应放到整个SwipeMenuLayout，在initView() 函数中设置了OnClickListener，所以在 OnTouchEvent 中每个事件都调用了 super.OnTouchEvent()。但是这样处理的话响应滑动的同时也响应了OnClickListener， 需要处理滑动和OnClickListener 的事件冲突，CANCAL 事件来了，一旦我们进入了滑动模式，在MOVE 事件中发送CANCAL事件给super，OnClickListener就无法响应。

```
    public interface OnMenuClickListener {

        void onMenuClick(View v, int position);

        void onItemClick(View v, int position);
    }
    
    private void  registerListener(int dis) {
        if(mLeftMenuView != null && dis == mLeftMargin) {
            mLeftMenuView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuClickListener != null)
                        mOnMenuClickListener.onMenuClick(v, mPosition);
                }
            });
        }

        if(mRightMenuView != null && dis == -mRightMargin) {
            mRightMenuView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuClickListener != null)
                        mOnMenuClickListener.onMenuClick(v, mPosition);
                }
            });
        }

        mResterListener = true;
    }

    private void unregisterListener(int dis) {
        if(mLeftMenuView != null && dis > 0) {
            mLeftMenuView.setOnClickListener(null);
        }

        if(mRightMenuView != null && dis < 0) {
            mRightMenuView.setOnClickListener(null);
        }

        mResterListener = false;
    }
```

#### 5 使用
1. 定义ListView 的Item XML  注意左右菜单 和context View的ID；左右菜单根据需要定义，不需要不定义即可
2. 在重写Adapter 的时候 getView 中设置position， 重写OnMenuClickListener，设置监听事件：
```
    ((SwipeMenuLayout)convertView).setPosition(position);
    ((SwipeMenuLayout)convertView).setOnMenuClickListener(ListViewActivity.this);
```

## 三 总结

不需要重写ListView 和 Adapter，菜单可以用XML定义好即可，是不是很简单。
传送门：[github](https://github.com/louiewh/SwipeMenuLayout)
