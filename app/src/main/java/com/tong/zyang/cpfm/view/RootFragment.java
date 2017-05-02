package com.tong.zyang.cpfm.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tong.zyang.cpfm.R;
import com.tong.zyang.cpfm.Values;
import com.tong.zyang.cpfm.util.FileObj;
import com.tong.zyang.cpfm.util.Root;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */

public class RootFragment extends Fragment {

    public RootFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.root_fragment_layout, container, false);
    }

    private Root mRoot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getParcelable(Values.ROOTS) != null) {
            mRoot = getArguments().getParcelable(Values.ROOTS);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FileObj fileObj = new FileObj();
        fileObj.filePath = mRoot.path;
        fileObj.fileName = mRoot.name;
        fileObj.type = FileObj.dir;
        openNewDir(fileObj, false);
    }

    public void openNewDir(FileObj fileObj, boolean add) {
        if (fileObj != null) {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();

            List<Fragment> fl = manager.getFragments();
            if (fl != null && fl.size() > manager.getBackStackEntryCount()) {
                Fragment fragment1 = fl.get(manager.getBackStackEntryCount());
                if (fragment1 != null)
                    fragmentTransaction.hide(fragment1);
            }

            FileDisFragment fragment = FileDisFragment.newInstance(1);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Values.FILEOBJ, fileObj);
            fragment.setArguments(bundle);
            fragmentTransaction.add(R.id.container, fragment, FileDisFragment.class.getName() + fileObj.getFilePath());
            if (add) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.show(fragment);
            fragmentTransaction.commit();
        }
    }

    public boolean onBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {

            List<Fragment> fl = getChildFragmentManager().getFragments();
            if (fl != null) {
                FileDisFragment fragment = (FileDisFragment) fl.get(getChildFragmentManager().getBackStackEntryCount());
                if (fragment != null) {
                    fragment.clear();
                }
            }

            getChildFragmentManager().popBackStackImmediate();

            fl = getChildFragmentManager().getFragments();
            if (fl != null) {
                FileDisFragment fragment = (FileDisFragment) fl.get(getChildFragmentManager().getBackStackEntryCount());
                if (fragment != null) {
                    ((AppCompatActivity) (getActivity())).getSupportActionBar().setTitle(fragment.mFileObj.getFileName());
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRoot != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mRoot.getName());
    }
}
