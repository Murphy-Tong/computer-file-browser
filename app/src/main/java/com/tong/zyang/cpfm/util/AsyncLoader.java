package com.tong.zyang.cpfm.util;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class AsyncLoader extends AsyncTask<String, Void, List<FileObj>> {
    public static final int FILES = 1;
    private DoPost doPost = null;

    public AsyncLoader(DoPost doPost) {
        this.doPost = doPost;
    }

    @Override
    protected void onPostExecute(List<FileObj> fileObjs) {
        super.onPostExecute(fileObjs);
        if (doPost != null) {
            doPost.post(fileObjs);
        }

    }

    @Override
    protected List<FileObj> doInBackground(String... params) {
        if (params == null || TextUtils.isEmpty(params[0])) return FileLoader.getPath(null);
        return FileLoader.getPath(params[0]);
    }

    public static interface DoPost {
        void post(List<FileObj> fileObjs);
    }
}
