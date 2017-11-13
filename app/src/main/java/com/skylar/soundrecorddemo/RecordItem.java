package com.skylar.soundrecorddemo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * Created by Administrator on 2017/9/10.
 */
public class RecordItem implements Parcelable {

    private int mId;
    private String mName;
    private String mPath;
    private int mLength;
    private long mTime;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {//把数据写入parcel
        parcel.writeInt(this.mId);
        parcel.writeString(this.mName);
        parcel.writeString(this.mPath);
        parcel.writeInt(this.mLength);
        parcel.writeLong(mTime);
    }

    public static final Parcelable.Creator<RecordItem> CREATOR = new ClassLoaderCreator<RecordItem>() {

        @Override
        public RecordItem createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return null;
        }

        @Override
        public RecordItem createFromParcel(Parcel parcel) {
            return new RecordItem(parcel);
        }

        @Override
        public RecordItem[] newArray(int i) {
            return new RecordItem[i];
        }

    };

    public RecordItem(Parcel parcel){//从parcel中读取数据
        mId = parcel.readInt();
        mName = parcel.readString();
        mPath = parcel.readString();
        mLength = parcel.readInt();
        mTime = parcel.readLong();
    }

    public RecordItem(){}
}
