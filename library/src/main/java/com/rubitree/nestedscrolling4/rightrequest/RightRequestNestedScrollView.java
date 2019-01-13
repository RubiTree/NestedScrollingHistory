package com.rubitree.nestedscrolling4.rightrequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * >> Description <<
 * <p>
 * >> Attention <<
 * <p>
 * >> Others <<
 * <p>
 * Created by RubiTree ; On 2019-01-13.
 */
public class RightRequestNestedScrollView extends NestedScrollView {
    public RightRequestNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public RightRequestNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RightRequestNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*--------------------------------------------------------------------------------------------*/

    private int downScreenOffset = 0;
    private int[] offsetInWindow = new int[2];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            downScreenOffset = getOffsetY();
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            final int activePointerIndex = ev.findPointerIndex(getInt("mActivePointerId"));
            if (activePointerIndex != -1) {
                final int y = (int) ev.getY(activePointerIndex);
                int mLastMotionY = getInt("mLastMotionY");
                int deltaY = mLastMotionY - y - (getOffsetY() - downScreenOffset);

                if (!getBoolean("mIsBeingDragged") && Math.abs(deltaY) > getInt("mTouchSlop")) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    setBoolean("mIsBeingDragged", true);
                }
            }
        }

        return super.onTouchEvent(ev);
    }

    private int getOffsetY() {
        getLocationInWindow(offsetInWindow);
        return offsetInWindow[1];
    }

    /*--------------------------------------------------------------------------------------------*/

    private int getInt(String name) {
        int value = -1;
        try {
            Field fieldTag = NestedScrollView.class.getDeclaredField(name);
            fieldTag.setAccessible(true);
            value = fieldTag.getInt(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    private boolean getBoolean(String name) {
        boolean value = false;
        try {
            Field fieldTag = NestedScrollView.class.getDeclaredField(name);
            fieldTag.setAccessible(true);
            value = fieldTag.getBoolean(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    private void setBoolean(String name, boolean value) {
        try {
            Field fieldTag = NestedScrollView.class.getDeclaredField(name);
            fieldTag.setAccessible(true);
            fieldTag.setBoolean(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
