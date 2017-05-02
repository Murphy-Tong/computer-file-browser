package com.tong.zyang.cpfm.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.disklrucache.DiskLruCache;
import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.Values;
import com.tong.zyang.cpfm.view.FullscreenActivity;
import com.tong.zyang.cpfm.view.adapter.MyFileDisFragmentRecyclerViewAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/4/20.
 */

public class FileHelper {
    private static final int vibrateLast = 200;
    private static final String FILEHOST = "CPDownload";

    public static final void play(Context context, FileObj fileObj) {
        if (context == null || fileObj == null) return;
        Intent intent = new Intent(context, FullscreenActivity.class);
        intent.putExtra(Values.FILEOBJ, fileObj);
//        intent.putExtra(Values.POSITION, position);
        context.startActivity(intent);
    }

    public static long downLaod(Context context, FileObj fileObj) throws UnsupportedEncodingException {
        if (context == null || fileObj == null) return -1;

        if (!Environment.isExternalStorageEmulated()) {
            Toast.makeText(context, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
            return -1;
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        String apkUrl = FileLoader.getUrlForPath(fileObj.getFilePath());

        DownloadManager.Request request = new

                DownloadManager.Request(Uri.parse(apkUrl));

        request.setDestinationInExternalPublicDir(FILEHOST, fileObj.getFileName());

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long downloadId = downloadManager.enqueue(request);

        SystemServiceHelper.vibrate(context, vibrateLast);
//        Toast.makeText(context, R.string.download_start, Toast.LENGTH_SHORT).show();
        return downloadId;
    }

    public int[] getBytesAndStatus(long downloadId, DownloadManager downloadManager) {
        int[] bytesAndStatus = new int[]{-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    public static boolean isVideo(String name) {
        if (TextUtils.isEmpty(name)) return false;
        MediaFile.MediaFileType fileType = MediaFile.getFileType(name);
        if (fileType == null) return false;
        return MediaFile.isVideoFileType(fileType.fileType);
    }

    public static boolean isPic(String name) {
        if (TextUtils.isEmpty(name)) return false;
        MediaFile.MediaFileType fileType = MediaFile.getFileType(name);
        if (fileType == null) return false;
        return MediaFile.isImageFileType(fileType.fileType);
    }

    private static final LinkedList<MmrDomain> mmrs = new LinkedList<>();
    private static LruCache<String, Bitmap> bitmapLruCache = new LruCache<String, Bitmap>((int) (SystemServiceHelper.getAvaliableMemSize() / 8)) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };

    private static Context mContext;
    private static final String LAG = "FileHelper";
    private static String CACHE_PATH;
    private static Md5Digest md5Digest = new Md5Digest(LAG, "MD5");

    public static void setMContext(Context context) {
        mContext = context;
        CACHE_PATH = mContext.getApplicationContext().getExternalCacheDir().getPath() + File.separator;
    }

    private static File getCacheDir(String name) {
        return new File(CACHE_PATH + md5Digest.encode(name));
    }

    private static DiskLruCache getDiskCache(String filePath) {
        try {

            return DiskLruCache.open(getCacheDir(filePath), 1, 1, 1024 * 1024 * 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static FFmpegMediaMetadataRetriever makeMmr(MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder) {
        FFmpegMediaMetadataRetriever mmr = null;
        try {
            mmr = new FFmpegMediaMetadataRetriever();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mmr;
    }


    public static void enQuene(final MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder, final int position) {
        MmrDomain mmr = null;
        if (mmrs.size() > 3) {
            mmr = mmrs.getLast();
            if (mmr != null && mmr.mmr != null) {
                mmr.mmr.release();
            }
            mmr = mmrs.removeLast();
        }
        mmr = new MmrDomain(null, holder, position, 2);

        mmrs.addFirst(mmr);
        getFromNet();
    }

    private static boolean onLoading = false;

    private static void getFromNet() {

        if (onLoading) return;
        if (mmrs.size() > 0 && mmrs.getFirst() != null && mmrs.getFirst().holder != null) {
            if (mmrs.getFirst().errorTimes >= mmrs.getFirst().maxTryTimes) {
                mmrs.getFirst().errorTimes = 0;
                mmrs.removeFirst();
                return;
            }
            onLoading = true;
            getFromNet(mmrs.getFirst());
        } else {
            onLoading = false;
        }
    }

    private static void getFromNet(final MmrDomain first) {
        if (first == null || first.holder == null) return;

        new AsyncTask<MmrDomain, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(MmrDomain... params) {
                Bitmap bitmap = null;

                FFmpegMediaMetadataRetriever mmr = params[0].mmr;

                if (mmr == null) {
                    mmr = makeMmr(params[0].holder);
                }
                if (mmr == null) return null;

                String str = FileLoader.getUrlForPath(first.holder.mItem.getFilePath());
                try {
                    mmr.setDataSource(str, new HashMap<String, String>());
                    String time = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (!TextUtils.isEmpty(time) && first.errorTimes == 0) {
                        bitmap = mmr.getFrameAtTime(((Long.parseLong(time) >> 2) * 1000), FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                    }

                    if (first.errorTimes > 0) {
                        bitmap = mmr.getFrameAtTime();
                    }

                    if (bitmap != null) {

                        int h = bitmap.getHeight();
                        float s = bitmap.getWidth() * 1f / Values.screenWidght;
                        h = h / Math.round(s);

                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, /*(int) (Values.screenWidght / againScale)*/Values.screenWidght, /*(int) (h / againScale)*/h, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

                    }
                } catch (Exception e) {
                    first.errorTimes++;
                    e.printStackTrace();
                    Log.i("mmr get faild", str);
                } finally {
                    mmr.release();

                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    bitmapLruCache.put(first.holder.mItem.filePath, bitmap);
                    if (first.holder.imageView != null && first.holder.position == first.position) {
                        setBitMap(first.holder, bitmap);
                        writeToDisk(bitmap, first);
                    }
                    mmrs.remove(first);
                } else {
                    first.errorTimes++;
                }
                onLoading = false;
                getFromNet();

            }
        }.execute(first);
    }

    private static void setBitMap(MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder, Bitmap bitmap) {
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener(null);
//        holder.imageView.setVisibility(View.VISIBLE);
    }


    public static void getVideoImage(final MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder, final int position) {
        if (holder == null || holder.mItem == null || holder.imageView == null || TextUtils.isEmpty(holder.mItem.filePath))
            return;
/**********************************************load pic from memcache**********************************************************/
        if (bitmapLruCache.get(holder.mItem.getFilePath()) != null) {

            setBitMap(holder, bitmapLruCache.get(holder.mItem.getFilePath()));
            return;
        }
/**********************************************load pic from disk**********************************************************/
        getFromDisk(holder.mItem.getFilePath(), new DiskGetCallback<Bitmap>() {
            @Override
            public void get(Bitmap bitmap) {
                if (bitmap != null) {
                    bitmapLruCache.put(holder.mItem.filePath, bitmap);
                    setBitMap(holder, bitmap);
                } else {
                    /**********************************************load pic from net**********************************************************/
                    enQuene(holder, position);
                }
            }

        });

    }

    private interface DiskGetCallback<T> {
        void get(T t);
    }

    private static void getFromDisk(String path, final DiskGetCallback callback) {
        if (TextUtils.isEmpty(path)) return;
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {

                DiskLruCache diskLruCache = null;
                try {
                    diskLruCache = getDiskCache(params[0]);
                    if (diskLruCache == null) return null;
                    DiskLruCache.Snapshot snapshot = diskLruCache.get(md5Digest.encode(params[0]));
                    if (snapshot != null) {
                        InputStream is = snapshot.getInputStream(0);
                        if (is != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            return bitmap;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (diskLruCache != null && !diskLruCache.isClosed())
                        try {
                            diskLruCache.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (callback != null) {
                    callback.get(bitmap);
                }
            }
        }.execute(path);
    }


    private static void writeToDisk(final Bitmap bitmap, final MmrDomain domain) {
        if (bitmap == null || domain == null || domain.holder == null || domain.holder.mItem == null)
            return;
        writeToDisk(bitmap, domain.holder.mItem.filePath);
    }

    private static void writeToDisk(final Bitmap bitmap, final String path) {
        if (bitmap == null || TextUtils.isEmpty(path))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                DiskLruCache diskLruCache = null;
                DiskLruCache.Editor editor = null;
                try {
                    diskLruCache = getDiskCache(path);
                    editor = diskLruCache.edit(md5Digest.encode(path));
                    if (editor != null) {
                        OutputStream os = editor.newOutputStream(0);
                        if (bitmap.compress(Bitmap.CompressFormat.PNG, 80, os)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                        if (!diskLruCache.isClosed()) {
                            diskLruCache.flush();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {

                        if (diskLruCache != null && !diskLruCache.isClosed())
                            diskLruCache.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        editor = null;
                        diskLruCache = null;

                    }
                }

            }
        }).start();

    }


    public static void delete(final FileObj fileObj) {
        if (fileObj != null) {
            new Thread() {
                @Override
                public void run() {
                    FileLoader.deleteFile(fileObj.getFilePath());
                }
            }.start();
        }
    }


    public static void loadScaledPic(final MyFileDisFragmentRecyclerViewAdapter.PicViewHolder holder, final int position) {
        if (holder == null || holder.mItem == null || holder.img == null) return;
        /**********************************************load pic from memcache**********************************************************/
        Bitmap pf = bitmapLruCache.get(holder.mItem.filePath);
        if (pf != null) {
            setPicImg(holder, pf);
            return;
        }
/**********************************************load pic from disk**********************************************************/
        getFromDisk(holder.mItem.getFilePath(), new DiskGetCallback<Bitmap>() {

            @Override
            public void get(Bitmap pf) {
                if (pf != null) {
                    bitmapLruCache.put(holder.mItem.filePath, pf);
                    setPicImg(holder, pf);
                } else {
/**********************************************load pic from net**********************************************************/
                    getPicFromNet(holder, position);
                }
            }
        });


    }

    private static void getPicFromNet(final MyFileDisFragmentRecyclerViewAdapter.PicViewHolder holder, final int position) {
        final int toW = Values.screenWidght;
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected void onPostExecute(Bitmap picFile) {
                if (picFile != null) {
                    writeToDisk(picFile, holder.mItem.filePath);
                    bitmapLruCache.put(holder.mItem.filePath, picFile);
                    if (holder.position == position) {
                        setPicImg(holder, picFile);
                    }
                }
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                if (params == null) return null;
                String path = params[0];
                HttpURLConnection inputStream = FileLoader.getInputStream(path);
                byte[] imgByts = null;
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    imgByts = Is2Btyes(inputStream.getInputStream());
                    inputStream.disconnect();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(imgByts, 0, imgByts.length, opts);
                    int outH = opts.outHeight;
                    int outW = opts.outWidth;

                    if (outH == 0 || outW == 0) return null;

                    float scale = outW * 1f / toW;
                    opts.inSampleSize = Math.round(scale);
//                    Log.i("source img", "w:" + outW + " h:" + outH + " scale:" + opts.inSampleSize);
                    int toH = (outH / opts.inSampleSize);
                    opts.outHeight = toH;
                    opts.outWidth = toW / opts.inSampleSize;
                    opts.inJustDecodeBounds = false;
                    return BitmapFactory.decodeByteArray(imgByts, 0, imgByts.length, opts);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("pic source img", "error" + path);
                } finally {
                    imgByts = null;
                }

                return null;
            }
        }.execute(holder.mItem.filePath);


    }

    private static void setPicImg(MyFileDisFragmentRecyclerViewAdapter.PicViewHolder holder, Bitmap pf) {
        ViewGroup.LayoutParams layoutParams = holder.img.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        layoutParams.height = pf.getHeight();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        holder.img.setLayoutParams(layoutParams);
        holder.img.setImageBitmap(pf);

    }

    private static byte[] Is2Btyes(InputStream is) {
        if (is == null) return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = -1;
        byte[] buf = new byte[1024 * 10];
        try {
            while ((len = is.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buf = null;
                bos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static class MmrDomain {
        public FFmpegMediaMetadataRetriever mmr;
        public MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder;
        public int position;
        public int errorTimes = 0;
        public int maxTryTimes;

        public MmrDomain(FFmpegMediaMetadataRetriever mmr,
                         MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder,
                         int position, int maxTryTimes) {
            this.mmr = mmr;
            this.maxTryTimes = maxTryTimes;
            this.holder = holder;
            errorTimes = 0;
            this.position = position;
        }
    }

    public static void writeHost(Context context, String host) {
        if (TextUtils.isEmpty(host)) return;

        SharedPreferences.Editor editor = context.getSharedPreferences("host", MODE_PRIVATE).edit();
        editor.putString("host", host);
        editor.commit();
        editor = null;
    }

    public static String readHost(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("host", MODE_PRIVATE);
        return sharedPreferences.getString("host", null);
    }

}
