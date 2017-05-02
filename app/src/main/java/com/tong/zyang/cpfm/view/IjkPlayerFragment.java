//package com.tong.zyang.cpfm.view;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.DragEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.tong.zyang.cpfm.R;
//import com.tong.zyang.cpfm.media.example.widget.media.AndroidMediaController;
//import com.tong.zyang.cpfm.media.example.widget.media.IMediaController;
//import com.tong.zyang.cpfm.media.example.widget.media.IjkVideoView;
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//
///**
// * Created by Administrator on 2017/4/23.
// */
//
//public class IjkPlayerFragment extends Fragment implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, View.OnDragListener {
//    private View mRootView;
//    private IjkVideoView ijkVideoView;
//    private IMediaController mediaController;
//    private final Long mDelay = 1000l;
//    private Handler mHandler = new Handler();
//    private Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            play();
//        }
//    };
//
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mRootView = inflater.inflate(R.layout.ijkplayer_fragment, container, false);
//        ijkVideoView = (IjkVideoView) mRootView.findViewById(R.id.ijkVideo);
////        ijkVideoView.buildDrawingCache(true);
//        position = 0;
//        return mRootView;
//    }
//
//    private String TAG = "IJKPLAYERFRAGMENT";
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Log.i(TAG, "onViewCreated");
//        mediaController = new AndroidMediaController(getContext(), false);
//        mediaController.setAnchorView(ijkVideoView);
//        ijkVideoView.setMediaController(mediaController);
////        ijkVideoView.
//        ijkVideoView.setOnPreparedListener(this);
//        ijkVideoView.setKeepScreenOn(true);
//        ijkVideoView.setOnInfoListener(this);
////        ijkVideoView.scroll
//        ijkVideoView.setOnDragListener(this);
////        mediaController.setAnchorView();
//    }
//
//    public boolean auto = false;
//
//
//    private String mPlayUrl;
//
//    public void setPlayUrl(String url) {
//        if (TextUtils.isEmpty(url)) return;
//        this.mPlayUrl = url;
//    }
//
//    private int position = 0;
//    public void setPosition(int p){
//        this.position = p;
//    }
//
//    public void play() {
//        if (ijkVideoView == null) {
//            mHandler.postDelayed(mRunnable, mDelay);
//            return;
//        }
//        ijkVideoView.setVideoPath(mPlayUrl);
//        ijkVideoView.seekTo(position);
//        ijkVideoView.start();
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
//
//    }
//
//    @Override
//    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//        return false;
//    }
//
//    @Override
//    public boolean onDrag(View v, DragEvent event) {
//        return false;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        resume();
//    }
//
//    private void resume() {
//        if (ijkVideoView != null) {
//            ijkVideoView.resume();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        pause();
//    }
//
//    private void pause() {
//        if (ijkVideoView != null && ijkVideoView.isPlaying()) {
//            ijkVideoView.pause();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        destory();
//    }
//
//    private void destory() {
//        if (ijkVideoView != null) {
//            ijkVideoView.release(true);
//        }
//    }
//}
