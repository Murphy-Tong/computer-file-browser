package com.tong.zyang.cpfm.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tong.zyang.cpfm.HandlerObtainer;
import com.tong.zyang.cpfm.view.SplashActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class FileLoader {
    private static final String init = "init";
    private static final String file = "file?path=";
    private static final String down = "down?path=";
    private static final String delete = "delete?path=";
    private static final String upload = "upload?path=";
    private static final String ENCODE = "UTF-8";
    private static final int CONNECT_TIME_OUT = 10000;
    private static final int READ_TIME_OUT = 20000;
    private static final String METHOD_GET = "GET";
    private static String downLoadPath;
    private static String position = "start";
    private static String host;

    public static void setHost(String s) {
        host = s;
    }


    public static String getUrlForPath(String path) {
//        checkHost();
        try {
            return host + down + URLEncoder.encode(path, ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDeleteUrlForPath(String path) {
//        checkHost();
        try {
            return host + delete + URLEncoder.encode(path, ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUploadUrlForPath(String path) {

//        checkHost();
        try {
            return host + upload + URLEncoder.encode(path, ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


  /*  public static int down(Context context, AsyncDownlaod.OnLoad onLoad, String path, String name) {
        URL url = null;
        if (!TextUtils.isEmpty(path)) {
            try {
                path = URLEncoder.encode(path, ENCODE);
                url = new URL(host + down);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = new URL(host);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        BufferedInputStream bufferedInputStream = null;
        HttpURLConnection httpURLConnection = null;
        SharedPreferences sharedPreferences = null;
        SharedPreferences.Editor editor = null;
        FileOutputStream fileOutputStream = null;
        try {
            File cacheDir = context.getCacheDir();
            if (!cacheDir.exists()) cacheDir.mkdirs();

            String downRecoderFile = cacheDir.getPath() + name;
            sharedPreferences = context.getSharedPreferences(downRecoderFile, 0);
            editor = sharedPreferences.edit();

            if (Environment.isExternalStorageEmulated())
                downLoadPath = Environment.getExternalStorageDirectory().getPath() + "/CPDownload/";
            else {

                return -1;
            }
            File file = new File(downLoadPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            String start = sharedPreferences.getString(position, null);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);

            if (!TextUtils.isEmpty(start)) {

                httpURLConnection.setRequestProperty("Range", "bytes=" + start + "-");

            }
            httpURLConnection.setRequestMethod(METHOD_GET);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() / 100 == 2) {
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                byte[] bytes = new byte[10240];
                int len = 0;
                fileOutputStream = new FileOutputStream(file.getPath() + name);
                int curr = 0;
                if (!TextUtils.isEmpty(position)) {
                    curr = Integer.parseInt(position);
                }
                while ((len = bufferedInputStream.read(bytes)) != -1) {
//                    stringBuilder.append(new String(bytes, 0, len, "UTF-8"));
                    curr += len;
                    fileOutputStream.write(bytes, 0, len);
                    fileOutputStream.flush();
                    editor.putString(position, String.valueOf(curr));
                    editor.commit();
                    if (onLoad != null) {
                        onLoad.onDownLoad(name, curr * 1f / httpURLConnection.getContentLength());
                    }

                }
                fileOutputStream.flush();
                fileOutputStream.close();
                return 1;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException es) {
            ExceptionHandle.socketTimeOut();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            editor.commit();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        }
        return 0;

    }
*/

    public static List<FileObj> getPath(String path) {
//        checkHost();
        URL url = null;
        if (!TextUtils.isEmpty(path)) {
            try {
                path = URLEncoder.encode(path, ENCODE);
                url = new URL(host + file + path);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = new URL(host + file);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        BufferedInputStream bufferedInputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            httpURLConnection.setRequestMethod(METHOD_GET);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() / 100 == 2) {
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                byte[] bytes = new byte[10240];
                int len = 0;
                StringBuilder stringBuilder = new StringBuilder();
                while ((len = bufferedInputStream.read(bytes)) != -1) {
                    stringBuilder.append(new String(bytes, 0, len, ENCODE));
                }
                String str = stringBuilder.toString();
                if (TextUtils.isEmpty(str)) return null;
                Type type = new TypeToken<List<FileObj>>() {
                }.getType();
                return new Gson().fromJson(str, type);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException se) {
            ExceptionHandle.socketTimeOut();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();

            }

        }
        return null;

    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) return;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getDeleteUrlForPath(filePath)).openConnection();
            connection.setConnectTimeout(CONNECT_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.connect();
            connection.getResponseCode();
        } catch (SocketTimeoutException es) {
            ExceptionHandle.socketTimeOut();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static HttpURLConnection getInputStream(String path) {
        if (TextUtils.isEmpty(path)) return null;
        HttpURLConnection is = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getUrlForPath(path)).openConnection();
            connection.setConnectTimeout(CONNECT_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.connect();
            if (connection.getResponseCode() / 100 == 2) {
                return connection;
            }

        } catch (SocketTimeoutException es) {
            ExceptionHandle.socketTimeOut();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }


    public static ArrayList<Root> initialPath(Callback callback) {

        HttpURLConnection is = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(host + init).openConnection();
            connection.setConnectTimeout(CONNECT_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.connect();
            if (connection.getResponseCode() / 100 == 2) {
                bufferedInputStream = new BufferedInputStream(connection.getInputStream());
                byte[] bytes = new byte[10240];
                int len = 0;
                StringBuilder stringBuilder = new StringBuilder();
                while ((len = bufferedInputStream.read(bytes)) != -1) {
                    stringBuilder.append(new String(bytes, 0, len, ENCODE));
                }
                String str = stringBuilder.toString();
                if (TextUtils.isEmpty(str)) return null;
                Type type = new TypeToken<List<Root>>() {
                }.getType();
                return new Gson().fromJson(str, type);

            }

        } catch (IOException e) {
            if (callback != null) {
                callback.onError(e);
            }
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null)
                is.disconnect();
        }
        return null;
    }

    public static String getHost() {
        return host;
    }

    public interface Callback {
        void onError(Exception e);
    }


    public static void checkAroundHost() {
//        final LinkedList<String> ls = new LinkedList<>();
        final LinkedList<Thread> tl = new LinkedList<>();
        if (!TextUtils.isEmpty(host)) {
            new Thread() {
                @Override
                public void run() {
                    int i = checkHost(host);
                    if (i == 0 && HandlerObtainer.tmp != null) {
                        HandlerObtainer.tmp.sendEmptyMessage(SplashActivity.NOTGET);
                    }
                }
            }.start();
            return;
        }

        String ip = NetHelper.findHost();
        final String addr = ip.substring(0, ip.lastIndexOf(".") + 1);
        for (int i = 0; i < 16; i++) {
            final int w = i;
            new Thread() {
                @Override
                public void run() {
                    tl.add(this);
                    for (int j = 0; TextUtils.isEmpty(host) && j < 16; j++) {
                        String s = check(addr + (w * 16 + j));
                        if (!TextUtils.isEmpty(s))
                            checkHost(s);
                    }
                    tl.remove(this);
                    if (tl.size() == 0 && HandlerObtainer.tmp != null) {
                        HandlerObtainer.tmp.sendEmptyMessage(SplashActivity.NOTGET);
                    }
                }
            }.start();

        }

    }

    private static int checkHost(String s) {
        HttpURLConnection httpURLConnection = null;
        try {
            Log.i("check host", s);
            httpURLConnection = (HttpURLConnection) new URL(s).openConnection();
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() / 100 == 2) {
                Log.i("check host success", s);
                host = s;
                HandlerObtainer.tmp.sendEmptyMessage(SplashActivity.HOSTOK);
                return 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return 0;
    }

    private static String check(final String nhost) {

        try {
            Log.i("check ip", nhost);
            Process exec = Runtime.getRuntime().exec("ping -c 1 -w 5 " + nhost);
            int state = exec.waitFor();
            if (state == 0) {
                Log.i("check ip ok ", nhost);
                return "http://" + nhost + ":8080/mp/";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
