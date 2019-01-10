package com.rubitree.nestedscrolling4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

/**
 * >> Description <<
 * Optimize fling interaction for NestedScrollView with NestedScrollingParent3 and NestedScrollingChild3.
 * You can see more details at http://blog.rubitree.com.
 * <p>
 * >> Attention <<
 * <p>
 * >> Others <<
 * We all know, this view's name need be NestedScrollView4.
 * <p>
 * Created by RubiTree ; On 2019-01-08.
 */
public class NestedScrollView4 extends NestedScrollView {
    NestedScrollingChildHelper childHelper;

    public NestedScrollView4(@NonNull Context context) {
        super(context);
        init();
    }

    public NestedScrollView4(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedScrollView4(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initChildHelper();
    }

    private void initChildHelper() {
        try {
            Field fieldTag = NestedScrollView.class.getDeclaredField("mChildHelper");
            fieldTag.setAccessible(true);
            childHelper = (NestedScrollingChildHelper) fieldTag.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (childHelper == null) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            return;
        }

        onNestedScrollInternal(dyUnconsumed, ViewCompat.TYPE_TOUCH, null);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (childHelper == null) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
            return;
        }

        onNestedScrollInternal(dyUnconsumed, type, consumed);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
        if (childHelper == null) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            return;
        }

        onNestedScrollInternal(dyUnconsumed, type, null);
    }

    private void onNestedScrollInternal(int dyUnconsumed, int type, @Nullable int[] consumed) {
        final int oldScrollY = getScrollY();
        if (!mIsBeingTouched) scrollBy(0, dyUnconsumed);
        final int myConsumed = getScrollY() - oldScrollY;

        if (consumed != null) {
            consumed[1] += myConsumed;
        }
        final int myUnconsumed = dyUnconsumed - myConsumed;

        childHelper.dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed);
    }

    /*--------------------------------------------------------------------------------------------*/

    private boolean mIsBeingTouched = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingTouched = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingTouched = false;
                break;
        }

        return super.onTouchEvent(ev);
    }
}
