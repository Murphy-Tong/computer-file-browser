package com.tong.zyang.cpfm.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/4/27.
 */
public class Root implements Parcelable {
    public String path;
    public String name;
    public long maxSize;
    public long freeSize;

    public Root() {
    }



    protected Root(Parcel in) {
        path = in.readString();
        name = in.readString();
        maxSize = in.readLong();
        freeSize = in.readLong();
    }

    public static final Creator<Root> CREATOR = new Creator<Root>() {
        @Override
        public Root createFromParcel(Parcel in) {
            return new Root(in);
        }

        @Override
        public Root[] newArray(int size) {
            return new Root[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public long getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(long freeSize) {
        this.freeSize = freeSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(maxSize);
        dest.writeLong(freeSize);
    }
}
