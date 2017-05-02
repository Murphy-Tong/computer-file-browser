package com.tong.zyang.cpfm.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.Values;
import com.tong.zyang.cpfm.util.FileObj;
import com.tong.zyang.cpfm.view.adapter.MyFileDisFragmentRecyclerViewAdapter;
import com.tong.zyang.cpfm.view.adapter.SurfaceController;


import static com.tong.zyang.cpfm.util.NetHelper.context;


public class FileDisFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private String path;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener interactionListener;

    private MyFileDisFragmentRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileDisFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FileDisFragment newInstance(int columnCount) {
        FileDisFragment fragment = new FileDisFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public FileObj mFileObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Slide slide = new Slide(Gravity.RIGHT);
        slide.setDuration(250);
        setEnterTransition(slide);*/
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            if (getArguments().getParcelable(Values.FILEOBJ) != null) {
                FileObj fileObj = (FileObj) getArguments().getParcelable(Values.FILEOBJ);
                if (fileObj != null) {
                    path = fileObj.getFilePath();
                    this.mFileObj = fileObj;
                }
            }
        }
        adapter = new MyFileDisFragmentRecyclerViewAdapter(getContext(), getChildFragmentManager(), new OnListFragmentInteractionListener2() {


            @Override
            public void onAllItemsGet() {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void startActivityForResult( FileObj fileObj) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra(Values.FILEOBJ, fileObj);
                FileDisFragment.this.startActivity(intent);
            }
        }
        );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_filedisfragment_list, container, false);
        recyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.list);
        // Set the adapter

        Context context = mSwipeRefreshLayout.getContext();

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        recyclerView.setAdapter(adapter);

        return mSwipeRefreshLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent));
        mSwipeRefreshLayout.setDistanceToTriggerSync(100);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.primary_dark));
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.load(path);
            }
        });
        if (adapter != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            adapter.load(path);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null && adapter.playingHolder != null && !adapter.playingHolder.fullScreen)
            SurfaceController.getInstance().pause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mFileObj != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mFileObj.getFileName());
        if (adapter != null && adapter.playingHolder != null) {
            SurfaceController.getInstance().resume();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getFragmentManager().beginTransaction().hide(this).commit();
        clear();
    }

    public void clear() {
        if (adapter != null && adapter.playingHolder != null) {
            SurfaceController.getInstance().stop();
        }
        if (adapter != null) {
            adapter.clear();
            recyclerView.removeAllViews();
            recyclerView.setAdapter(null);
            adapter = null;
            recyclerView = null;
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            mSwipeRefreshLayout = null;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MyFileDisFragmentRecyclerViewAdapter.VideoViewHolder holder);

        void onAllItemsGet();

        void startActivityForResult(SurfaceHolder iMediaPlayer, FileObj fileObj);
    }
    public interface OnListFragmentInteractionListener2 {
        // TODO: Update argument type and name

        void onAllItemsGet();

        void startActivityForResult(FileObj fileObj);
    }
}
