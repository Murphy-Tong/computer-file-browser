package com.tong.zyang.cpfm.view.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tong.zyang.cpfm.util.Format;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2017/4/24.
 */

public class SurfaceController implements IjkMediaPlayer.OnPreparedListener, IjkMediaPlayer.OnBufferingUpdateListener, IjkMediaPlayer.OnSeekCompleteListener, IjkMediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, IjkMediaPlayer.OnVideoSizeChangedListener, IjkMediaPlayer.OnInfoListener {
    private final String TAG = "SurfaceController";
    private final int HANDLECLICK = 12;
    private final int TOGGLESEEKBAR = 11;
    private TextView tvl, tvr;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.what == HANDLECLICK) {
                mHandler.removeMessages(HANDLECLICK);
                handleClick();
            } else if (msg != null && msg.what == TOGGLESEEKBAR) {
                toggleSeekBar();
            }
        }
    };
    private View displayView;
    private IjkMediaPlayer mMediaPlayer;

    public void setDisplayView(View mSurfaceView) {
        this.displayView = mSurfaceView;
        this.displayView.setOnClickListener(this);
        setVideoSize();
    }


    private long mDelay = 1000l;
    private ProgressBar circleProgressBar;
    private long mToggleDelay = 1000;
    private long mResetClicksDelay = 250;
    private Surface mHolder;
    private String mUrl;


    private void handleClick() {
        if (clicks >= 2) {
            pauseOrResume();
        } else {
            showOrHideSeekBar();
        }
        clicks = 0;
    }

    private void showOrHideSeekBar() {
        if (mControllerView != null) {
            if (mControllerView.getVisibility() != View.VISIBLE) {
                showController();
            } else {
                hideController();
            }
        }
    }

    private void pauseOrResume() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                pause();
            } else {
                resume();
            }
        }
    }


    private Runnable toggleSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            toggleSeekBar();
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            play();
        }
    };
    private static SurfaceController controller = new SurfaceController();

    private SurfaceController() {

    }

    public static SurfaceController getInstance() {
        return controller;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setPath(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (url.equals(mUrl)) {
            return;
        }
        if (mCallBack != null) {
            mCallBack.onResetPath();
        }
        this.mUrl = url;
        try {
            init();
            prepared = false;
            preparing = false;
            mMediaPlayer.setDataSource(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDisplay(Surface display) {
        if (display != null) {
            if (mMediaPlayer == null) {
                try {
                    init();
                    prepared = false;
                    preparing = false;
                    mMediaPlayer.setDataSource(mUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.mHolder = display;
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setSurface(mHolder);
           /* if (mCallBack != null)
                mCallBack.onDisplaySet();*/
        }
    }

    private boolean preparing = false;
//    private boolean pause = false;

    public void play() {
        if (mControllerView != null) {
            mControllerView.setVisibility(View.VISIBLE);
        }
        if (displayView != null) {
            displayView.setClickable(false);
        }
        if (circleProgressBar != null)
            circleProgressBar.setVisibility(View.VISIBLE);
        if (mSeekBar != null) {
            mSeekBar.setEnabled(false);
        }
        if (mMediaPlayer != null) {
            if (prepared) {
                start();
            } else if (!preparing) {
                prepare();
            }
        }

    }


    private void init() {
        try {
            if (mMediaPlayer != null) mMediaPlayer.reset();
            mMediaPlayer = new IjkMediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mWidth = 0;
            mHeight = 0;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mHandler.removeMessages(TOGGLESEEKBAR);
            mMediaPlayer.pause();

        }
    }

    private void prepare() {

        if (circleProgressBar != null)
            circleProgressBar.setVisibility(View.VISIBLE);

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            preparing = true;
            mMediaPlayer._prepareAsync();
        }
    }

    private boolean prepared = false;

    private void start() {
        toggleSeekBar();
        if (displayView != null) {
            displayView.setClickable(true);
        }
        if (mControllerView != null) {
            mControllerView.setVisibility(View.VISIBLE);
        }
        if (circleProgressBar != null)
            circleProgressBar.setVisibility(View.GONE);

        if (mSeekBar != null) {
            mSeekBar.setEnabled(true);
        }
        if (mMediaPlayer != null) {

            if (tvr != null) {
                tvr.setText(Format.formatDate(mMediaPlayer.getDuration()));
            }


            mMediaPlayer.start();
            setVideoSize();
        }

    }

    public void resume() {
        if (mMediaPlayer != null && mMediaPlayer.isPlayable() && !mMediaPlayer.isPlaying()) {
            try {

                start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (ingoreStop) return;
        if (mCallBack != null) {
            mCallBack.ondestoryed();
        }
        mCallBack = null;
        mHandler.removeMessages(TOGGLESEEKBAR);
        mHandler.removeMessages(HANDLECLICK);
        if (mControllerView != null)
            mControllerView.setVisibility(View.GONE);
        if (mMediaPlayer != null) {
            release(mMediaPlayer);

        }
        mMediaPlayer = null;

    }

    private void release(final IjkMediaPlayer mp) {
        new Thread() {
            @Override
            public void run() {
                if (mp.isPlaying()) mp.stop();
                mp.reset();
                mp.release();
            }
        }.start();
    }

    private void toggleSeekBar() {
        if (mMediaPlayer != null) {
            long l = mMediaPlayer.getCurrentPosition();
            float f = l * 1f / mMediaPlayer.getDuration();
            if (mSeekBar != null) {
                mSeekBar.setProgress((int) (f * mSeekBar.getMax()));
            }
            if (tvl != null) {
                tvl.setText(Format.formatDate(l));
            }
            mHandler.sendEmptyMessageDelayed(TOGGLESEEKBAR, mToggleDelay);
        } else {
            mSeekBar.setProgress(0);
        }
    }

    private SeekBar mSeekBar;

    public void setSeekBar(SeekBar seekBar) {
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(this);
            seekBar.setMax(1000);
            this.mSeekBar = seekBar;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        float per = seekBar.getProgress() * 1f / seekBar.getMax();
        if (mMediaPlayer != null) {
            seekBar.setProgress(seekBar.getProgress());
            circleProgressBar.setVisibility(View.VISIBLE);
            mMediaPlayer.seekTo((int) (per * mMediaPlayer.getDuration()));
        }
    }


    private int clicks = 0;

    @Override
    public void onClick(View v) {
        if (mMediaPlayer == null) return;
        clicks++;
        mHandler.sendEmptyMessageDelayed(HANDLECLICK, mResetClicksDelay);

    }

    private void hideController() {
//        mSeekBar.setAlpha();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mControllerView, "alpha", 1f, 0f);
        alpha.setDuration(200);
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mControllerView.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alpha.start();

    }

    private void showController() {
        mControllerView.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mControllerView, "alpha", 0f, 1f);
        alpha.setDuration(200);
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alpha.start();
    }

    private View mControllerView;


    public void setControllerView(View rootView) {
        if (rootView == null) return;
        this.tvl = (TextView) rootView.findViewWithTag("tvl");
        this.tvr = (TextView) rootView.findViewWithTag("tvr");
        this.mControllerView = rootView.findViewWithTag("controllerBar");
        this.circleProgressBar = (ProgressBar) rootView.findViewWithTag("circleProgress");
        this.mSeekBar = (SeekBar) rootView.findViewWithTag("seekBar");
        setSeekBar(mSeekBar);
//        controlImg = (ImageView) controlView.findViewWithTag("controllerImg");
    }

    private CallBack mCallBack;


    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        Log.i(TAG, " buf " + percent);

        if (mSeekBar != null) {
            mSeekBar.setSecondaryProgress(percent * mSeekBar.getMax() / 100);
        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        Log.i("controller what", i + " extra:" + i1);
        stop();

        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        Log.i("controller", " what " + what + " extra" + extra);
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        this.mMediaPlayer = (IjkMediaPlayer) iMediaPlayer;
        preparing = false;
        prepared = true;
        start();
        if (mCallBack != null) {
            mCallBack.onStart();
        }

    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        this.mMediaPlayer = (IjkMediaPlayer) iMediaPlayer;
        circleProgressBar.setVisibility(View.GONE);
        start();
    }

    private int mWidth;
    private int mHeight;

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int i2, int i3) {
        if (width == 0 || height == 0) {
            stop();
            if (mCallBack != null) {
                mCallBack.onVideoInitError();
            }
            return;
        }

        mWidth = width;
        mHeight = height;
        setVideoSize();

    }


    private void setVideoSize() {
        if (mWidth == 0 || mHeight == 0) return;
        int sh = displayView.getHeight();
        int sw = displayView.getWidth();
        if (sh == 0 || sw == 0) return;
        float scaleW = mWidth * 1f / sw;
        float scaleH = mHeight * 1f / sh;
        int h, w;
        w = sw;
        if (fixedWight) {
            h = (int) (mHeight * 1f / scaleW);
        } else {
            float scale = scaleH > scaleW ? scaleH : scaleW;
            h = (int) (mHeight * 1f / scale);
//            w = sw;
        }

        ViewGroup.LayoutParams mLayoutParams = displayView.getLayoutParams();
        if (mLayoutParams == null) {
            mLayoutParams = new ViewGroup.LayoutParams(w, h);
        }
        mLayoutParams.height = h;
        mLayoutParams.width = w;
        displayView.setLayoutParams(mLayoutParams);

    }

    public void setCallback(CallBack callback) {
        this.mCallBack = callback;
    }

    private boolean ingoreStop = false;

    public void setIngoreStop(boolean b) {
        this.ingoreStop = b;
    }

    private boolean fixedWight = false;

    public void setFixedWidght(boolean b) {
        this.fixedWight = b;
    }

    public interface CallBack {
        void ondestoryed();

//        void onPause();

        void onStart();

//        void onResume();

        void onVideoInitError();

//        void onDisplaySet();

        void onResetPath();
    }
}
