package com.tong.zyang.cpfm.view;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tong.zyang.cpfm.HandlerObtainer;
import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.Values;
import com.tong.zyang.cpfm.util.ExceptionHandle;
import com.tong.zyang.cpfm.util.FileHelper;
import com.tong.zyang.cpfm.util.FileLoader;
import com.tong.zyang.cpfm.util.FileObj;
import com.tong.zyang.cpfm.util.MediaFile;
import com.tong.zyang.cpfm.util.Root;
import com.tong.zyang.cpfm.view.adapter.SurfaceController;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.tong.zyang.cpfm.util.NetHelper.context;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {
    public static final int ACTION_NEWDIR = 18;
    public static final int WINDOW = 38;
    public static final int ACTION_SHARE2 = 28;
    public static final int CLOSEWINDOW = 88;
    private Handler handler;
    private LinearLayout navigationView;
    private DrawerLayout drawer;
    private Surface mSurface;
    private static RootFragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setAllowEnterTransitionOverlap(true);
        Slide slide = new Slide(Gravity.RIGHT);
        slide.setDuration(300);
        getWindow().setEnterTransition(slide);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileHelper.setMContext(getApplicationContext());

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);
        Values.screenHeight = ds.heightPixels;
        Values.screenWidght = ds.widthPixels;
        handler = new MyHandler(this);
        HandlerObtainer.set(handler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (LinearLayout) findViewById(R.id.nav_view);
        initWindow();
        initMenu();
    }

    private View tvRoot;
    private TextureView tv;

    private void initWindow() {
        tvRoot = getWindow().getLayoutInflater().inflate(R.layout.window, null, false);
        tv = (TextureView) tvRoot.findViewById(R.id.tv);

        tvRoot.findViewById(R.id.closeWindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
                SurfaceController.getInstance().stop();
            }
        });
        tvRoot.findViewById(R.id.fullScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra(Values.FILEOBJ, mFileObj);
                MainActivity.this.startActivity(intent);
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) (Values.screenWidght * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
        tvRoot.setLayoutParams(params);
        tvRoot.setVisibility(View.GONE);
        tv.setSurfaceTextureListener(this);
        getWindow().addContentView(tvRoot, params);
    }


    private void closeWindow() {
        tvRoot.setVisibility(View.GONE);
        SurfaceController.getInstance().setFixedWidght(false);
        SurfaceController.getInstance().setIngoreStop(false);
    }

    private FileObj mFileObj;

    private void showWindow(FileObj fileObj) {
        tvRoot.setVisibility(View.VISIBLE);
        this.mFileObj = fileObj;
        final SurfaceController surfaceController = SurfaceController.getInstance();
        surfaceController.setIngoreStop(true);
        surfaceController.setFixedWidght(true);
        surfaceController.setDisplayView(tv);
        surfaceController.setPath(FileLoader.getUrlForPath(fileObj.filePath));
        if (mSurface != null) {
            SurfaceController.getInstance().setDisplay(mSurface);
            SurfaceController.getInstance().play();
        }
    }


    private void initMenu() {
        if (getIntent().getParcelableArrayListExtra(Values.FILEOBJ) == null) {

        } else {
            ArrayList<Parcelable> fileObjs = getIntent().getParcelableArrayListExtra(Values.FILEOBJ);
            Root fileObj = null;
            TextView textView = null;
            Iterator<Parcelable> iterator = fileObjs.iterator();
            View view = null;
            map = new HashMap<>(fileObjs.size());

            while (iterator != null && iterator.hasNext()) {
                fileObj = (Root) iterator.next();
                if (fileObj != null && !TextUtils.isEmpty(fileObj.path)) {
                    textView = (TextView) View.inflate(this, R.layout.nav_item, null);

                    if (TextUtils.isEmpty(fileObj.name)) {
                        continue;
                    } else {
                        textView.setTag(fileObj);
                        textView.setText(fileObj.name);
                        textView.setOnClickListener(this);
                        navigationView.addView(textView);
                        if (view == null) {
                            view = textView;
                        }
                    }

                }
            }
            onClick(view);
        }
    }


    public static final int ACTION_SHARE = 16;
    private Map<String, RootFragment> map;

    @Override
    public void onClick(View v) {
        drawer.closeDrawers();

        Object o = v.getTag();
        if (o != null && o instanceof Root) {
            Root root = (Root) o;
            RootFragment rootFragment = map.get(root.getPath());
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (currentFragment != null) {
                fragmentTransaction.hide(currentFragment);
                if (rootFragment == currentFragment) return;
            }


            if (rootFragment == null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Values.ROOTS, root);
                rootFragment = (RootFragment) Fragment.instantiate(this, RootFragment.class.getName(), bundle);
                map.put(root.path, rootFragment);
                fragmentTransaction.add(R.id.container, rootFragment);
            }

            getSupportActionBar().setTitle(root.getName());
            currentFragment = rootFragment;
            fragmentTransaction.show(rootFragment);
            fragmentTransaction.commit();

        }

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i("surfacetextUre", "onSurfaceTextureAvailable");
        this.mSurface = new Surface(surface);
        SurfaceController.getInstance().setDisplay(mSurface);
        SurfaceController.getInstance().play();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        surface.release();
        this.mSurface = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private static class MyHandler extends Handler {

        private SoftReference<FragmentActivity> activitySoftReference;


        public MyHandler(FragmentActivity context) {
            activitySoftReference = new SoftReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                if (msg.what == 1) {
                    ((MainActivity) activitySoftReference.get()).finish = false;
                    return;
                } else if (msg.what == CLOSEWINDOW) {
                    ((MainActivity) (activitySoftReference.get())).closeWindow();
                } else if (msg.what == WINDOW) {
                    ((MainActivity) (activitySoftReference.get())).showWindow((FileObj) msg.obj);
                } else if (msg.what == ExceptionHandle.CONNECT_ERROR) {
                    Toast.makeText(activitySoftReference.get(), R.string.connect_error, Toast.LENGTH_SHORT).show();
                } else if (msg != null && msg.obj != null) {

                    FileObj fileObj = (FileObj) msg.obj;

                    if (msg.what == ACTION_NEWDIR && fileObj.type == FileObj.dir) {
                        MainActivity.currentFragment.openNewDir(fileObj, true);
                    } else if (msg.what == ACTION_SHARE) {
                        share(fileObj);
                    } else if (msg.what == ACTION_SHARE2) {
                        share(fileObj);
                    }
                }
            }
        }


        private void share(FileObj fileObj) {
           /* Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setDataAndType(Uri.parse(FileLoader.getUrlForPath(fileObj.filePath)), MediaFile.getMimeTypeForFile(fileObj.getFilePath()));
            activitySoftReference.get().startActivity(Intent.createChooser(intent1, activitySoftReference.get().getString(R.string.intent_share)));*/
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(FileLoader.getUrlForPath(fileObj.filePath)), MediaFile.getMimeTypeForFile(fileObj.getFileName()));
            activitySoftReference.get().startActivity(Intent.createChooser(intent, activitySoftReference.get().getString(R.string.movie_send_intent_toast)));

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SurfaceController.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceController.getInstance().resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SurfaceController.getInstance().stop();
    }

    private boolean finish = false;

    @Override
    public void onBackPressed() {
        if (currentFragment.onBackPressed()) {
            return;
        }

        //确认退出程序
        if (finish) {
            this.finish();
        } else {
            finish = true;
            Toast.makeText(this, R.string.press_out, Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessageDelayed(1, 1000);
            return;
        }
        super.onBackPressed();
    }
}
