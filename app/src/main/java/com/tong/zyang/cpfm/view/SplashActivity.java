package com.tong.zyang.cpfm.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tong.zyang.cpfm.HandlerObtainer;
import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.Values;
import com.tong.zyang.cpfm.util.FileHelper;
import com.tong.zyang.cpfm.util.FileLoader;
import com.tong.zyang.cpfm.util.NetHelper;
import com.tong.zyang.cpfm.util.Root;

import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class SplashActivity extends AppCompatActivity {
    public static final int NOTGET = 404;
    public static final int HOSTOK = 202;


    private ProgressBar pb;
    private TextView tv;
    private EditText editText;
    private Button find;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null && msg.what == HOSTOK) {
                getRoots();
            } else if (msg != null && msg.what == NOTGET) {
                notGet();
            }
        }
    };

    private void notGet() {
        pb.setVisibility(View.GONE);
        find.setEnabled(true);
        editText.setEnabled(true);
        tv.setText(getResources().getString(R.string.connect_error));
        find.setText(R.string.try_again);
    }

    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setVisibility(View.GONE);
        tv = (TextView) findViewById(R.id.tv);
        find = (Button) findViewById(R.id.find);
        editText = (EditText) findViewById(R.id.et);
        HandlerObtainer.tmp = handler;

        Slide slide = new Slide(Gravity.LEFT);
        slide.setDuration(300);
        getWindow().setExitTransition(slide);
        NetHelper.context = getApplicationContext();
//        initWindow();
        String host = FileHelper.readHost(this);
        if(!TextUtils.isEmpty(host)){
            editText.setText(host);
        }

    }

    private void initWindow() {
        View v = getWindow().getLayoutInflater().inflate(R.layout.window, null, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(Values.screenWidght / 2, (int) ((Values.screenWidght / 2) * 0.4));
        v.setLayoutParams(params);
        getWindow().addContentView(v, params);

    }

    private void findHost() {
        pb.setVisibility(View.VISIBLE);
        find.setEnabled(false);
        editText.setEnabled(false);
        editText.clearFocus();
        tv.setText(null);
        FileLoader.checkAroundHost();
    }

    private void getRoots() {
        new AsyncTask<Void, Void, ArrayList<Root>>() {

            @Override
            protected void onPostExecute(ArrayList<Root> fileObjs) {
                if (fileObjs == null || fileObjs.size() == 0) {
//                    Toast.makeText(SplashActivity.this, "error", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    find.setEnabled(true);
                    editText.setEnabled(true);
                    tv.setText(getResources().getString(R.string.connect_error));
                    find.setText(R.string.try_again);
                } else {
                    start(fileObjs);
                }

            }

            @Override
            protected ArrayList<Root> doInBackground(Void... params) {
                return FileLoader.initialPath(null);
//                return null;
            }
        }.execute();
    }

    private void start(ArrayList<Root> fileObjs) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra(Values.FILEOBJ, fileObjs);
        startActivity(intent);
        FileHelper.writeHost(this,FileLoader.getHost());
        finishAfterTransition();
    }

    public void find(View v) {
        String s = editText.getText().toString().trim();
        FileLoader.setHost(s);
        if (!TextUtils.isEmpty(s) && (s.startsWith("1") || s.startsWith("2") || s.startsWith("3"))) {
            if (!s.endsWith("/")) s = s + "/";
            FileLoader.setHost("http://" + s);
        } else if (!TextUtils.isEmpty(s) && s.startsWith("http://")) {
            if (!s.endsWith("/")) s = s + "/";
            FileLoader.setHost(s);
        }
        findHost();
    }

    @Override
    public void finish() {
        HandlerObtainer.tmp = null;
        handler = null;
        super.finish();

    }
}
