package com.tong.zyang.cpfm.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.Values;
import com.tong.zyang.cpfm.util.FileLoader;
import com.tong.zyang.cpfm.util.FileObj;
import com.tong.zyang.cpfm.view.adapter.SurfaceController;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements SurfaceController.CallBack, View.OnClickListener, SurfaceHolder.Callback {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private View resume;
//    private SurfaceController3 mController;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private boolean mVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen2);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        View mainView = findViewById(R.id.mainView);
        hide();
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        resume = findViewById(R.id.img);
        resume.setOnClickListener(this);
        resume.setVisibility(View.GONE);
        mContentView.setVisibility(View.GONE);
        if (getIntent().getParcelableExtra(Values.FILEOBJ) == null) finish();
        FileObj fileObj = getIntent().getParcelableExtra(Values.FILEOBJ);
//        int p = getIntent().getIntExtra(Values.POSITION,0);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        final SurfaceController surfaceController = SurfaceController.getInstance();
//        mController = surfaceController;
        surfaceController.setDisplayView(surfaceView);
        surfaceController.setControllerView(mainView);
        surfaceController.setPath(FileLoader.getUrlForPath(fileObj.filePath));
        surfaceController.setCallback(this);
        surfaceView.getHolder().addCallback(this);
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

//        mControlsView.setVisibility(View.GONE);
        mVisible = false;
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    //
    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void ondestoryed() {
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        resume.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        resume.setVisibility(View.GONE);
    }

    @Override
    public void onVideoInitError() {
        Toast.makeText(this, getResources().getString(R.string.video_play_error), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onResetPath() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img) {
            SurfaceController.getInstance().resume();
            v.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1);
//        mController.pause();
        resume.setVisibility(View.GONE);
        super.onBackPressed();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        SurfaceController.getInstance().setDisplay(holder.getSurface());
        SurfaceController.getInstance().play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        SurfaceController.getInstance().setIngoreStop(false);
    }
}
