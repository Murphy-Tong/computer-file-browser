package com.tong.zyang.cpfm.view;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tong.zyang.cpfm.R;

import java.util.ArrayList;


public class BottomDialogFrag extends BaseBottomSheetFrag {


    public static final String TAG = "BottomDialogFrag";

    private ArrayList<String> titles;
    private ArrayList<OnItemClickListener> listeners;
    private ArrayList<String> hideTitles;

    TextView txtBottomCancel;
    LinearLayout layoutItemGroup;

    public BottomDialogFrag() {

    }

    @SuppressLint("ValidFragment")
    private BottomDialogFrag(BottomSheetBuilder builder) {
        this.titles = builder.titles;
        this.listeners = builder.listeners;
        hideTitles = new ArrayList<>(builder.titles.size());
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_base_bottomsheet;
    }

    @Override
    public void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        layoutItemGroup = (LinearLayout) rootView.findViewById(R.id.layout_item_group);
        txtBottomCancel = (TextView) rootView.findViewById(R.id.txt_bottom_cancel);
        txtBottomCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(true);
            }
        });

        for (int i = 0, size = titles.size(); i < size; i++) {
            String title = titles.get(i);
            boolean isBreak = false;
            for (String hideTitle : hideTitles) {
                if (hideTitle.equals(title)) {
                    isBreak = true;
                    break;
                }
            }
            if (!isBreak) {
                TextView txtItem = (TextView) inflater.inflate(R.layout.item_base_bottomsheet, layoutItemGroup, false);
                txtItem.setText(title);
                final OnItemClickListener listener = listeners.get(i);
                txtItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.OnClick();
                        }
                    }
                });
                layoutItemGroup.addView(txtItem);
            }

        }
    }


    /**
     * 隐藏某个item
     *
     * @param title
     */
    public void hideTitleItem(String title) {
        hideTitles.add(title);
    }

    /**
     * 清除隐藏集合
     */
    public void clearHideTitle() {
        hideTitles.clear();
    }

    public interface OnItemClickListener {
        void OnClick();
    }

    public static class BottomSheetBuilder {

        //必要参数 标题集合
        private ArrayList<String> titles;
        //必要参数 监听器集合
        private ArrayList<OnItemClickListener> listeners;

        //非必要参数 配置默认参数


        /**
         * 构造器的构造方法 初始化参数
         */
        public BottomSheetBuilder() {
            titles = new ArrayList<>();
            listeners = new ArrayList<>();
        }

        public BottomDialogFrag build() {
            if (titles == null || titles.isEmpty()) {
                Log.e("error", "can not empty titles");
            }

            if (listeners == null || listeners.isEmpty()) {
                Log.e("error", "can not empty listeners");
            }

            return new BottomDialogFrag(this);
        }

        public BottomSheetBuilder appendItem(String title, OnItemClickListener clickListener) {
            this.titles.add(title);
            this.listeners.add(clickListener);
            return this;
        }
    }
}
