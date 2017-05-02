package com.tong.zyang.cpfm;

import android.os.Handler;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2017/4/19.
 */

public class HandlerObtainer {
    public static Handler tmp;
    public static IjkMediaPlayer tmpPlayer;

    private static final ThreadLocal<Handler> HANDLER_THREAD_LOCAL = new ThreadLocal<>();

    public static Handler get(){
        return HANDLER_THREAD_LOCAL.get();
    }

    public static void set(Handler handler){
        HANDLER_THREAD_LOCAL.set(handler);
    }
}
