package com.tong.zyang.cpfm.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2017/4/25.
 */

public class SwipActionRelativeLayout extends RelativeLayout {
    private Context mContext;
    private int mSLiding;
    private int lLimit;
    private int mX, mY;
    private int rLimit;
    private int slidingTime = 250;
    private float lLimitPercent = 0.65f;
    private float rLimitPercent = 0.5f;
//    private float

    public SwipActionRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mSLiding = ViewConfiguration.get(mContext).getScaledTouchSlop();
        scroller = new Scroller(mContext, new LinearInterpolator());

    }


    public SwipActionRelativeLayout(Context context) {
        super(context);
    }

    public SwipActionRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        lLimit = -(int) (getWidth() * lLimitPercent);
        rLimit = (int) (getWidth() * rLimitPercent);
        mX = l;
        mY = t;
    }

    private float downX;
    private float downY;

    private boolean isScroll = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - downX;
                float dy = ev.getY() - downY;
                downY = ev.getY();
                downX = ev.getX();
                if (Math.abs(dy) < Math.abs(dx)) {
                    isScroll = true;
                    return true;
                }
        }
        isScroll = false;
        return super.onInterceptTouchEvent(ev);
    }


    private int scrollDis = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScroll)
            getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getRawX() - downX;
                float dy = ev.getRawY() - downY;
                downY = ev.getRawY();
                downX = ev.getRawX();
                if (Math.abs(dy) < Math.abs(dx) && Math.abs(dx) < 200) {
                    scrollDis += dx;
                    if (scrollDis > rLimit) {
                        scrollDis = rLimit;
                    }
                    setX(scrollDis);
                }
                break;
            case MotionEvent.ACTION_UP:
                resume();
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        if (isScroll) {
            return true;
        }
        return super.onTouchEvent(ev);
    }


    private Scroller scroller;

    private void resume() {
        if (scrollDis <= lLimit) {
            slidingLeftOut();
        } else if (scrollDis >= rLimit) {
            slidingRightOut();
        } else {
            resumePosition();
        }
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void slidingLeftOut() {
        slidingLOut();
        if (callback != null) {
            callback.left();
        }
//        resumePosition();
    }

    private void slidingRightOut() {
       /* scroller.startScroll(scrollDis, 0, 0, 0);
        scrollDis = 0;
        invalidate();*/
        resumePosition();
        if (callback != null) {
            callback.right();
        }

    }

    private void slidingLOut() {
        ObjectAnimator x = ObjectAnimator.ofFloat(this, "x", getX(), -getWidth());
        x.setDuration((long) ((1 - Math.abs(getX()) * 1f / getWidth()) * slidingTime));
        x.start();
//        scrollDis = 0;
    }

    public void resumePosition() {
        if (Math.abs(scrollDis) > mSLiding) {
            ObjectAnimator x = ObjectAnimator.ofFloat(this, "x", scrollDis, mX);
            x.setDuration((long) (Math.abs(getX()) * 1f * slidingTime / getWidth()));
            x.start();
        }
        scrollDis = 0;
        Log.i("resume posi", "");
    }

  /*  @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            setX(scroller.getCurrX());
//            postInvalidate();
        }
    }*/

    public interface Callback {
        void left();

        void right();
    }

    public void reset() {
        setX(mX);
        scrollDis = 0;
    }

}
