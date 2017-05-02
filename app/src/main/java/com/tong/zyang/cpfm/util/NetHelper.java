package com.tong.zyang.cpfm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Administrator on 2017/4/27.
 */

public class NetHelper {
    public static Context context;

    public static String findHost() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                DhcpInfo connectionInfo = wifiManager.getDhcpInfo();
                int sd = connectionInfo.serverAddress;
                String ip = (sd & 0xff) + "." + ((sd >> 8) & 0xff) + "." + ((sd >> 16) & 0xff) + "." + ((sd >> 24) & 0xff);
                Log.i("host:", ip);
                return ip;
            }
        }
        return null;
    }
}