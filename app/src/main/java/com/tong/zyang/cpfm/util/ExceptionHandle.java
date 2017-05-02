package com.tong.zyang.cpfm.util;

import android.os.Message;

import com.tong.zyang.cpfm.HandlerObtainer;

/**
 * Created by Administrator on 2017/4/27.
 */

public class ExceptionHandle {
    public static final int CONNECT_ERROR = 400;

    public static void socketTimeOut() {
        if (HandlerObtainer.get() != null) {
            Message message = Message.obtain();
            message.what = CONNECT_ERROR;
            HandlerObtainer.get().sendMessage(message);
        }
    }
}
