package com.tong.zyang.cpfm.util;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by Administrator on 2017/4/26.
 */

public class SystemServiceHelper {

    public static void vibrate(Context context, int l) {
        if (context == null) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(l);
        }
    }

    public static long getAvaliableMemSize(){
        return Runtime.getRuntime().maxMemory()/1024;
    }

}
