package com.tong.zyang.cpfm.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/4/25.
 */

public class Format {
    private static DecimalFormat format = new DecimalFormat("####.00");

    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return format.format(size / 1024f) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            return format.format(size / 1024f / 1024f) + "MB";
        } else
            return format.format(size / 1024f / 1024f / 1024f) + "GB";
    }

    public static String formatDate(long mill) {
        if (mill <= 0) return null;
        long secondTotal = mill / 1000;
        String s = String.valueOf(secondTotal % 60);
        String m = String.valueOf(secondTotal / 60 % 60);
        String h = String.valueOf(secondTotal / 60 / 60);
        StringBuilder builder = new StringBuilder();
        if (h.length() == 1) {
            builder.append("0");
        }
        builder.append(h);
        builder.append(":");
        if (m.length() == 1)
            builder.append("0");
        builder.append(m);
        builder.append(":");
        if (s.length() == 1)
            builder.append("0");
        builder.append(s);
        return builder.toString();
    }

}
