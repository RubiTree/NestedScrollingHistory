package com.rubitree.demo.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

/**
 * >> Description <<
 * 虽然没有报错，但这不是可以运行的代码，这是剔除 NestedScrollView 中关于 parent 的部分，得到的可以认为是官方的 NestedScrollingChild
 * 接口的实现建议，关键是在在触摸和滚动时怎么调用 NestedScrollingChild 的方法，也就是下面 onInterceptTouchEvent() 、 onTouchEvent() 、
 * computeScroll() 中大约150行的代码
 * <p>
 * >> Attention <<
 * 这里为了让主线逻辑更加清晰，省略了多点触控和窗体偏移相关的代码，实际开发如果需要，可以直接参考 NestedScrollView 中的写法，也不会很麻烦
 * <p>
 * >> Others <<
 * <p>
 * Created by RubiTree ; On 2019-01-08.
 */
public class NestedScrollChildSample extends FrameLayout implements NestedScrollingChild3 {
    private OverScroller mScroller;

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private boolean mIsBeingDragged = false;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollConsumed = new int[2];
    private int mLastScrollerY;
    private int mLastMotionY;

    private final NestedScrollingChildHelper mChildHelper;

    /*--------------------------------------------------------------------------------------------*/

    public NestedScrollChildSample(@NonNull Context context) {
        this(context, null);
    }

    public NestedScrollChildSample(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollChildSample(@NonNull Context context, @Nullable AttributeSet attrs,
                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    /*--------------------------------------------------------------------------------------------*/

    // NestedScrollingChild3
    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                     int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    // NestedScrollingChild2
    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    // NestedScrollingChild
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionY = (int) ev.getY();

                mVelocityTracker.addMovement(ev);
                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't. mScroller.isFinished should be false when
                 * being flinged. We need to call computeScrollOffset() first so that
                 * isFinished() is correct.
                 */
                mScroller.computeScrollOffset();
                mIsBeingDragged = isSelfScrolling();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!mIsBeingDragged) {
                    final int y = (int) ev.getY();
                    final int yDiff = Math.abs(y - mLastMotionY);

                    if (yDiff > mTouchSlop && (getNestedScrollAxes() & ViewCompat.SCROLL_AXIS_VERTICAL) == 0) {
                        mIsBeingDragged = true;
                        mLastMotionY = y;
                        mVelocityTracker.addMovement(ev);

                        requestParentDisallowInterceptTouchEvent();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                trySpringBack();
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                if ((mIsBeingDragged = isSelfScrolling())) requestParentDisallowInterceptTouchEvent();

                // If being flinged and user touches, stop the fling. isFinished will be false if being flinged.
                if (isSelfScrolling()) abortAnimatedScroll();

                mLastMotionY = (int) ev.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int y = (int) ev.getY();
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, null, ViewCompat.TYPE_TOUCH)) {
                    deltaY -= mScrollConsumed[1];
                }

                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    requestParentDisallowInterceptTouchEvent();

                    mIsBeingDragged = true;
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                if (mIsBeingDragged) {
                    final int oldY = getScrollY();

                    // Calling overScrollByCompat will call onOverScrolled, which calls onScrollChanged if applicable.
                    if (overScrollByCompat(0, deltaY, 0, getScrollY(), 0, getScrollRange(), 0, 0, true) && !hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)) {
                        // Break our velocity if we hit a scroll barrier.
                        mVelocityTracker.clear();
                    }

                    final int scrolledDeltaY = getScrollY() - oldY;
                    final int unconsumedY = deltaY - scrolledDeltaY;

                    mScrollConsumed[1] = 0;

                    dispatchNestedScroll(0, scrolledDeltaY, 0, unconsumedY, null, ViewCompat.TYPE_TOUCH, mScrollConsumed);

                    deltaY -= mScrollConsumed[1];
                    if (canOverscroll()) showOverScrollEdgeEffect();
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();

                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    if (!dispatchNestedPreFling(0, -initialVelocity)) {
                        dispatchNestedFling(0, -initialVelocity, true);
                        fling(-initialVelocity);
                    }
                } else {
                    trySpringBack();
                }

                endDrag();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged && getChildCount() > 0) trySpringBack();
                endDrag();
                break;
        }

        if (mVelocityTracker != null) mVelocityTracker.addMovement(ev);

        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) return;

        mScroller.computeScrollOffset();
        final int y = mScroller.getCurrY();
        int unconsumed = y - mLastScrollerY;
        mLastScrollerY = y;

        // Nested Scrolling Pre Pass
        mScrollConsumed[1] = 0;
        dispatchNestedPreScroll(0, unconsumed, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH);
        unconsumed -= mScrollConsumed[1];

        if (unconsumed != 0) {
            // Internal Scroll
            final int oldScrollY = getScrollY();
            overScrollByCompat(0, unconsumed, getScrollX(), oldScrollY, 0, getScrollRange(), 0, 0, false);
            final int scrolledByMe = getScrollY() - oldScrollY;
            unconsumed -= scrolledByMe;

            // Nested Scrolling Post Pass
            mScrollConsumed[1] = 0;
            dispatchNestedScroll(0, scrolledByMe, 0, unconsumed, null, ViewCompat.TYPE_NON_TOUCH, mScrollConsumed);
            unconsumed -= mScrollConsumed[1];
        }

        if (unconsumed != 0) {
            if (canOverscroll()) showOverScrollEdgeEffect();
            abortAnimatedScroll();
        }

        if (isSelfScrolling()) ViewCompat.postInvalidateOnAnimation(this);
    }

    private void abortAnimatedScroll() {
        mScroller.abortAnimation();
        stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
    }

    public void fling(int velocityY) {
        mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
    }

    private void endDrag() {
        mIsBeingDragged = false;
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    /*--------------------------------------------------------------------------------------------*/

    // Fake
    private int getScrollRange() {
        return 0;
    }

    // Fake
    // scroll self
    private boolean overScrollByCompat(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                       int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return true;
    }

    // Fake
    private void showOverScrollEdgeEffect() {

    }

    private boolean isSelfScrolling() {
        return !mScroller.isFinished();
    }

    private void requestParentDisallowInterceptTouchEvent() {
        final ViewParent parent = getParent();
        if (parent != null) parent.requestDisallowInterceptTouchEvent(true);
    }

    private void trySpringBack() {
        if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean canOverscroll() {
        final int mode = getOverScrollMode();
        return mode == OVER_SCROLL_ALWAYS || (mode == OVER_SCROLL_IF_CONTENT_SCROLLS && getScrollRange() > 0);
    }
}

