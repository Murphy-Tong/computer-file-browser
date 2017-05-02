package com.tong.zyang.cpfm.util;

/**
 * Created by Administrator on 2017/4/19.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/4/19.
 */
public class FileObj implements Parcelable {
    public static final int dir = 1;
    public static final int file = 2;
    public String fileName;
    public String filePath;
    public int layOutPosition = -1;
    public long fileSize;
    public int type;
    public boolean addToBack = true;

    public FileObj() {

    }


    protected FileObj(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        layOutPosition = in.readInt();
        fileSize = in.readLong();
        type = in.readInt();
    }

    public static final Creator<FileObj> CREATOR = new Creator<FileObj>() {
        @Override
        public FileObj createFromParcel(Parcel in) {
            return new FileObj(in);
        }

        @Override
        public FileObj[] newArray(int size) {
            return new FileObj[size];
        }
    };

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getType() {
        return type;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeInt(layOutPosition);
        dest.writeLong(fileSize);
        dest.writeInt(type);
    }
}