package com.tong.zyang.cpfm.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tong.zyang.cpfm.HandlerObtainer;
import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.util.AsyncLoader;
import com.tong.zyang.cpfm.util.FileHelper;
import com.tong.zyang.cpfm.util.FileLoader;
import com.tong.zyang.cpfm.util.FileObj;
import com.tong.zyang.cpfm.util.Format;
import com.tong.zyang.cpfm.view.BottomDialogFrag;
import com.tong.zyang.cpfm.view.FileDisFragment;
import com.tong.zyang.cpfm.view.MainActivity;
import com.tong.zyang.cpfm.view.SwipActionRelativeLayout;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MyFileDisFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FileObj> fileObjs;
    private Context mcContext;
    public VideoViewHolder playingHolder = null;
    private FragmentManager fragmentManager;

    private FileDisFragment.OnListFragmentInteractionListener2 listener;
    private AsyncLoader.DoPost doPost = new AsyncLoader.DoPost() {
        @Override
        public void post(List<FileObj> fileObjs) {
            if (distory) return;
            if (MyFileDisFragmentRecyclerViewAdapter.this.fileObjs != null)
                MyFileDisFragmentRecyclerViewAdapter.this.fileObjs.clear();
            MyFileDisFragmentRecyclerViewAdapter.this.fileObjs = fileObjs;
            if (listener != null) {
                listener.onAllItemsGet();
            }
            notifyDataSetChanged();
        }
    };

    private boolean distory = false;

    public void clear() {
        distory = true;
        if (this.fileObjs != null)
            this.fileObjs.clear();
        this.fileObjs = null;
    }

    public MyFileDisFragmentRecyclerViewAdapter(Context mcContext, FragmentManager fragmentManager, FileDisFragment.OnListFragmentInteractionListener2 listener) {
        this.mcContext = mcContext;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
        this.distory = false;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file_list_video, parent, false);
            return new VideoViewHolder((SwipActionRelativeLayout) view);
        } else if (viewType == TYPE_PIC) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_pic_layout, parent, false);
            return new PicViewHolder((SwipActionRelativeLayout) view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_filedisfragment, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sholder, final int position) {
        /**********************************folder*********************************/
        if (sholder != null && sholder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) sholder;
            holder.mItem = fileObjs.get(position);
            holder.title.setText(holder.mItem.fileName);
            holder.type.setText(null);
            holder.position = position;
            holder.confg.setText(null);
            holder.mItem.layOutPosition = position;
            if (holder.mItem.type == FileObj.file) {

                holder.confg.setText(Format.formatFileSize(holder.mItem.fileSize));
                String suf = holder.mItem.filePath.substring(holder.mItem.filePath.lastIndexOf(".") + 1);
                holder.type.setText(suf);
            }

        }
        /*********************************pic**********************************/
        else if (sholder != null && sholder instanceof PicViewHolder) {
            final PicViewHolder holder = (PicViewHolder) sholder;
            holder.mItem = fileObjs.get(position);
            holder.img.setImageResource(R.mipmap.ic_image_black_48dp);
            holder.position = position;
            holder.mItem.layOutPosition = position;
            FileHelper.loadScaledPic(holder, position);
            holder.mView.reset();

        }
        /*******************************video************************************/
        else if (sholder != null && sholder instanceof VideoViewHolder) {
            final VideoViewHolder holder = (VideoViewHolder) sholder;
            holder.mItem = fileObjs.get(position);
            holder.imageView.setImageResource(R.mipmap.ic_movie_creation_black_48dp);
            holder.textureView.setSurfaceTextureListener(holder);
            holder.resume.setVisibility(View.VISIBLE);
            holder.mItem.layOutPosition = position;
//            holder.play.setVisibility(View.VISIBLE);
            holder.titleText.setText(holder.mItem.getFileName());
            holder.position = position;
            holder.confg.setText(Format.formatFileSize(holder.mItem.fileSize));
            FileHelper.getVideoImage(holder, position);
            holder.mView.reset();
        }
    }

    private final int TYPE_VIDEO = 1;
    private final int TYPE_PIC = 3;

    @Override
    public int getItemViewType(int position) {
        if (fileObjs != null && position < fileObjs.size() && fileObjs.get(position) != null && fileObjs.get(position).type == FileObj.file) {
            if (FileHelper.isVideo(fileObjs.get(position).getFileName())) {
                return TYPE_VIDEO;
            } else if (FileHelper.isPic(fileObjs.get(position).getFileName())) {
                return TYPE_PIC;
            }
        }

        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return fileObjs == null ? 0 : fileObjs.size();
    }

    private String path;

    public void load(String path) {
        this.path = path;
        new AsyncLoader(doPost).execute(path);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView confg;
        public final TextView type;
        public FileObj mItem;
        public int position;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            confg = (TextView) view.findViewById(R.id.confg);
            type = (TextView) view.findViewById(R.id.type);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (playingHolder != null && playingHolder.mController != null) {
                        playingHolder.mController.stop();
                        playingHolder.showImgs();
                    }*/

                    SurfaceController.getInstance().stop();

                    if (mItem.type == FileObj.file)
                        showDialog(mItem);
                    else if (mItem.type == FileObj.dir) {
                        if (HandlerObtainer.get() != null) {

                            Message msg = Message.obtain();
                            msg.what = MainActivity.ACTION_NEWDIR;
                            msg.obj = mItem;
                            HandlerObtainer.get().sendMessage(msg);
                        }
                    }

                }
            });
        }
    }

    public class PicViewHolder extends RecyclerView.ViewHolder implements SwipActionRelativeLayout.Callback, View.OnClickListener {
        public final SwipActionRelativeLayout mView;

        public final ImageView img;
        public FileObj mItem;
        public int position;
        public final ImageButton downButton;
        public final ImageButton delete;
        public final ImageButton fullscrnBtn;

        public PicViewHolder(SwipActionRelativeLayout view) {
            super(view);
            mView = view;
            mView.setCallback(this);
            downButton = (ImageButton) view.findViewById(R.id.downLoadBtn);
            delete = (ImageButton) view.findViewById(R.id.delete);
            fullscrnBtn = (ImageButton) view.findViewById(R.id.fullscreenBtn);
            fullscrnBtn.setVisibility(View.GONE);
            img = (ImageView) view.findViewById(R.id.picImg);
            img.setOnClickListener(this);
            delete.setOnClickListener(this);
            downButton.setOnClickListener(this);
            fullscrnBtn.setOnClickListener(this);
        }

        @Override
        public void left() {
            if (mItem != null)
                delete(mItem, new DeleteCallback() {
                    @Override
                    public void onCancel() {
                        mView.resumePosition();
                    }
                });

        }

        @Override
        public void right() {
            if (mItem != null)
                download(mItem);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.picImg || v.getId() == R.id.fullscreenBtn) {
            } else if (v.getId() == R.id.downLoadBtn) {
                download(mItem);
            } else if (v.getId() == R.id.delete) {
                share(mItem);
            }
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements SurfaceController.CallBack, SwipActionRelativeLayout.Callback, TextureView.SurfaceTextureListener {
        public final SwipActionRelativeLayout mView;
        public final ImageView imageView;
        public final ImageView resume;
        public final ImageButton smallWindow;
        public final ImageButton delete;
        public final ImageButton fullscrnBtn;
        public FileObj mItem;
        public SurfaceHolder holder;
        public int position;
        //        public final SurfaceView surfaceView;
        public final TextView titleText;
        public final TextureView textureView;
        public final TextView confg;
        //        public final SurfaceController2 mController;
        public final SeekBar seekBar;
        public final View controlView;
        public boolean fullScreen = false;

        public VideoViewHolder(SwipActionRelativeLayout view) {
            super(view);
            mView = view;
            fullScreen = false;
            mView.setCallback(this);
            titleText = (TextView) view.findViewById(R.id.title);
            confg = (TextView) view.findViewById(R.id.confg);

            textureView = (TextureView) view.findViewById(R.id.surfaceView);
            imageView = (ImageView) view.findViewById(R.id.img);
            seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            view.findViewWithTag("circleProgress").setVisibility(View.GONE);
            controlView = view.findViewById(R.id.controllerBar);
            controlView.setVisibility(View.GONE);

            resume = (ImageView) view.findViewById(R.id.resume);
            smallWindow = (ImageButton) view.findViewById(R.id.smallWindow);
            delete = (ImageButton) view.findViewById(R.id.delete);
            fullscrnBtn = (ImageButton) view.findViewById(R.id.fullscreenBtn);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share(mItem);

                }
            });

            smallWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playingHolder = VideoViewHolder.this;
                    windowPlay(mItem);
                }
            });
            fullscrnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playingHolder != VideoViewHolder.this) {
                        SurfaceController.getInstance().stop();
                    }
                    SurfaceController.getInstance().setIngoreStop(true);
                    playingHolder = VideoViewHolder.this;
                    fullScreen = true;
                    listener.startActivityForResult(mItem);
                    onStart();
                }
            });


            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {

                        closeWindow();

                        playingHolder = VideoViewHolder.this;
                        SurfaceController instance = SurfaceController.getInstance();
                        instance.setPath(FileLoader.getUrlForPath(mItem.filePath));
                        instance.setDisplayView(textureView);
                        instance.setControllerView(mView);
                        instance.setCallback(VideoViewHolder.this);
                        instance.setDisplay(mSurface);
                        onStart();
                        instance.play();
                    }
                }
            });

        }

        private void closeWindow() {
            HandlerObtainer.get().sendEmptyMessage(MainActivity.CLOSEWINDOW);
        }


        public void showImgs() {
            imageView.setVisibility(View.VISIBLE);
            controlView.setVisibility(View.GONE);
            resume.setVisibility(View.VISIBLE);
        }

        @Override
        public void ondestoryed() {
            imageView.setVisibility(View.VISIBLE);
            controlView.setVisibility(View.GONE);
            resume.setVisibility(View.VISIBLE);
        }


        @Override
        public void onStart() {
            imageView.setVisibility(View.GONE);
            controlView.setVisibility(View.GONE);
            resume.setVisibility(View.GONE);
        }


        @Override
        public void onVideoInitError() {
            Toast.makeText(mcContext, R.string.video_play_error, Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onResetPath() {
            ondestoryed();
        }

        @Override
        public void left() {
            if (mItem != null)
                delete(mItem, new DeleteCallback() {
                    @Override
                    public void onCancel() {
                        mView.resumePosition();
                    }
                });

        }

        @Override
        public void right() {
            if (mItem != null)
                download(mItem);

        }

        public Surface mSurface;

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurface = new Surface(surface);
            if (playingHolder == MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder.this) {
                SurfaceController.getInstance().setDisplayView(textureView);
                SurfaceController.getInstance().setControllerView(mView);
                SurfaceController.getInstance().setCallback(MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder.this);
                SurfaceController.getInstance().setDisplay(mSurface);
                fullScreen = false;
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

            if (!fullScreen && playingHolder == MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder.this) {
                playingHolder = null;
                showImgs();
                surface.release();
                SurfaceController.getInstance().stop();
            }
            return false;

        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    private void share(FileObj mItem) {
        if (HandlerObtainer.get() != null) {
            Message message = Message.obtain();
            message.what = MainActivity.ACTION_SHARE2;
            message.obj = mItem;
            HandlerObtainer.get().sendMessage(message);

        }
    }


    private void download(FileObj fileObj) {
        try {
            FileHelper.downLaod(mcContext, fileObj);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private BottomDialogFrag bottomDialogFrag = null;

    private void showDialog(final FileObj fileObj) {
        if (fileObj == null) return;

        BottomDialogFrag.BottomSheetBuilder build = new BottomDialogFrag.BottomSheetBuilder();
        build.appendItem(mcContext.getString(R.string.download), new BottomDialogFrag.OnItemClickListener() {
            @Override
            public void OnClick() {
                download(fileObj);
                dismiss();
            }
        });
        build.appendItem(mcContext.getString(R.string.share), new BottomDialogFrag.OnItemClickListener() {
            @Override
            public void OnClick() {
                if (HandlerObtainer.get() != null) {

                    Message msg = Message.obtain();
                    msg.what = MainActivity.ACTION_SHARE;
                    msg.obj = fileObj;
                    HandlerObtainer.get().sendMessage(msg);
                }
                dismiss();
            }
        });
        build.appendItem(mcContext.getString(R.string.delete), new BottomDialogFrag.OnItemClickListener() {
            @Override
            public void OnClick() {
                delete(fileObj, null);
                dismiss();
            }
        });

        bottomDialogFrag = build.build();
        bottomDialogFrag.show(fragmentManager, null);

    }

    private void dismiss() {
        if (bottomDialogFrag != null && bottomDialogFrag.isShowing()) {
            bottomDialogFrag.dismiss();
        }
    }

    private void windowPlay(FileObj fileObj) {
        Message msg = Message.obtain();
        msg.what = MainActivity.WINDOW;
        msg.obj = fileObj;
        HandlerObtainer.get().sendMessage(msg);
    }

    private void delete(final FileObj fileObj, final DeleteCallback deleteCallback) {
        if (fileObj == null) return;
        final AlertDialog.Builder builder = new AlertDialog.Builder(mcContext);
        builder.setTitle(mcContext.getResources().getString(R.string.delete));
        AlertDialog alertDialog = null;
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileHelper.delete(fileObj);
                fileObjs.remove(fileObj);
                notifyItemRemoved(fileObj.layOutPosition);

            }
        });
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteCallback != null) {
                    deleteCallback.onCancel();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (deleteCallback != null) {
                    deleteCallback.onCancel();
                }
            }
        });
//        builder.show();
        alertDialog = builder.create();
        alertDialog.show();
    }

    private interface DeleteCallback {
        void onCancel();
    }
}
