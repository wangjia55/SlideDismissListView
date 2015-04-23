package com.jacob.dismisslistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by jacob-wj on 2015/4/23.
 */
public class SlideDismissListView extends ListView {

    public static final int SNAP_VELOCITY = 1500;

    /**
     * 滑动辅助类
     */
    private Scroller mScroller;
    /**
     * 速度辅助类
     */
    private VelocityTracker mVelocityTracker;
    /**
     * 方向，自定义枚举
     */
    private Direction mDirection;
    /**
     *  滑动点的位置
     */
    private int mPosition;
    /**
     *  屏幕的宽度
     */
    private int mScreenWidth;
    /**
     *  触摸的itemview
     */
    private View mItemView;
    /**
     * 记录上次的触摸位置
     */
    private int mLastX;

    private int mLastY;

    private OnSlideListener mSlideListener;
    /**
     * 是否在滑动
     */
    private boolean isSliding;

    enum Direction {
        left,
        right
    }

    public SlideDismissListView(Context context) {
        this(context, null);
    }

    public SlideDismissListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDismissListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mScreenWidth = getScreenWidth();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                addVelocityTracker(ev);
                int index = pointToPosition(x, y);
                if (index == ListView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }
                mPosition = index;
                mItemView = getChildAt(mPosition - getFirstVisiblePosition());
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (isSliding){
            return true;
        }
        addVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                addVelocityTracker(ev);
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                mItemView.scrollBy(-deltaX, 0);
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                int velocityX = (int) getScrollVelocity();
                if (velocityX > SNAP_VELOCITY) {
                    slideRight();
                } else if (velocityX < -SNAP_VELOCITY) {
                    slideLeft();
                } else {
                    slideDistance();
                }
                recycleVelocityTracker();
                break;
        }

        if (isSliding){
            return  true;
        }
        return super.onTouchEvent(ev);
    }

    private void slideDistance() {
        int deltaX = mItemView.getScrollX();
        if (deltaX>=mScreenWidth/2){
            slideLeft();
        }else if(deltaX<=-mScreenWidth/2){
            slideRight();
        }else{
           mItemView.scrollTo(0,0);
        }
    }

    private void slideLeft() {
        mDirection = Direction.left;
        int deltaX = mScreenWidth - mItemView.getScrollX();
        mScroller.startScroll(mItemView.getScrollX(), 0,deltaX, 0, Math.abs(deltaX));
        invalidate();
    }

    private void slideRight() {
        mDirection = Direction.right;
        int deltaX = -(mScreenWidth + mItemView.getScrollX());
        mScroller.startScroll(mItemView.getScrollX(), 0, deltaX, 0, Math.abs(deltaX));
        invalidate();
    }

    private void addVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    private float getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        return mVelocityTracker.getXVelocity();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItemView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            isSliding = true;
            if (mScroller.isFinished()) {
                isSliding = false;
                mItemView.scrollTo(0, 0);
                if (mSlideListener != null) {
                    mSlideListener.removeItem(mDirection,mItemView, mPosition);
                }
            }
        }
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public interface OnSlideListener {
        void removeItem(Direction direction, View view,int position);
    }

    public void setOnSlideListener(OnSlideListener listener) {
        this.mSlideListener = listener;
    }

}
