//package com.tong.zyang.cpfm.view.adapter;
//
//import android.content.Context;
//import android.util.Log;
//
//
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//
///**
// * Created by Administrator on 2017/4/28.
// */
//
//public class IjkController implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener {
//    private IjkVideoView mIjkVideoView;
//    public String mUrl;
//    private int mPosition;
//    private CallBack mCallBack;
//    private Context mContext;
//    private AndroidMediaController mediaController;
//    private IMediaPlayer iMediaPlayer;
//
//
//    public IjkController(Context context, IjkVideoView ijkVideoView, CallBack callBack) {
//        this.mContext = context;
//        this.mCallBack = callBack;
//        this.mIjkVideoView = ijkVideoView;
//    }
//
//    public void start() {
//        if (mCallBack != null) {
//            mCallBack.onPrepare();
//        }
//        mediaController = new AndroidMediaController(mContext, false);
//        mediaController.setAnchorView(mIjkVideoView);
//        mIjkVideoView.setMediaController(mediaController);
//        mIjkVideoView.setKeepScreenOn(true);
//        mIjkVideoView.setOnInfoListener(this);
//        mIjkVideoView.setOnErrorListener(this);
//        mIjkVideoView.setOnPreparedListener(this);
//        mIjkVideoView.setVideoPath(mUrl);
//        mIjkVideoView.start();
//    }
//
//    public void pause() {
//        if (mIjkVideoView.isPlaying()) {
//            mPosition = mIjkVideoView.getCurrentPosition();
//            mIjkVideoView.pause();
//        }
//        if (mCallBack != null) {
//            mCallBack.onPause();
//        }
//    }
//
//    public int getPosition() {
//        if (mIjkVideoView.isPlaying()) {
//            return mIjkVideoView.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    public void stop() {
//        if (mIjkVideoView.isPlaying()) {
//            mIjkVideoView.stopPlayback();
//        }
//        mIjkVideoView.release(true);
//        if (mCallBack != null) {
//            mCallBack.onStop();
//        }
//    }
//
//    public void resume() {
////        mIjkVideoView.seekTo(mPosition);
//        mIjkVideoView.resume();
//        if (mCallBack != null) {
//            mCallBack.onResume();
//        }
//    }
//
//    @Override
//    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//        Log.i("ijk info", i + "- " + i1);
////        iMediaPlayer.setDisplay();
//        return false;
//    }
//
//    @Override
//    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
//        if (mCallBack != null) {
//            mCallBack.onError();
//        }
//        stop();
//        return false;
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
//        this.iMediaPlayer = iMediaPlayer;
//        if (mCallBack != null) {
//            mCallBack.onStart();
//        }
//    }
//
//    public interface CallBack {
//        void onStop();
//
//        void onPause();
//
//        void onStart();
//
//        void onResume();
//
//        void onError();
//
//        void onPrepare();
//    }
//
//    public void hideController() {
//        if (mediaController.isShowing())
//            mediaController.hide();
//    }
//
//    public IMediaPlayer getPlayer() {
//        return iMediaPlayer;
//    }
//
//}
