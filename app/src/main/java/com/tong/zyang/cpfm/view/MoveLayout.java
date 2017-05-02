package com.tong.zyang.cpfm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/5/2.
 */

public class MoveLayout extends RelativeLayout {
    private Context mContext;
    private int slop;
    private int maxX, maxY;

    public MoveLayout(Context context) {
        super(context);
    }

    public MoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setX(0);
        setY(0);
        x = 0;
        y = 0;
        slop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public MoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoveLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int x, y;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE)
            return true;
        else return super.onInterceptTouchEvent(ev);
    }

    private float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

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
                if (Math.abs(dy) < 60)
                    y += dy;
                if (Math.abs(dx) < 60)
                    x += dx;
                x = x < 0 ? 0 : x > maxX ? maxX : x;
                y = y < 0 ? 0 : y > maxY ? maxY : y;
//                Log.i("move", "downX:" + downX + " downY" + downY + " dx:" + dx + " dy:" + dy + " x:" + x + " y:" + y);
                setX(x);
                setY(y);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        maxX = ((ViewGroup) getParent()).getWidth() - (r - l);
        maxY = ((ViewGroup) getParent()).getHeight() - (b - t);
        Log.i("layout", "maxX:" + maxX + " maxY:" + maxY);
    }
}
