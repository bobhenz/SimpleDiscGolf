package com.bobhenz.simplediscgolf;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class DiscGolfHoleInfo implements Parcelable{
    public enum TeeCategory {NOVICE, INTERMEDIATE, ADVANCED};
    public enum BasketCategory {A, B, C};

    private String mName;
    private int mPar;
    private long mDbId;
    private long mCourseDbId;
    private EnumMap<TeeCategory, Location> mTeeLocationMap = new EnumMap<TeeCategory, Location>(TeeCategory.class);
    private EnumMap<BasketCategory, Location> mBasketLocationMap = new EnumMap<BasketCategory, Location>(BasketCategory.class);
/*
    public DiscGolfHoleInfo() {
        mDbId = -1;
        mName = null;
        mPar = 0;
    }
*/
    public DiscGolfHoleInfo(String name, int par) {
        mDbId = -1;
        mName = name;
        mPar = par;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getDbId() {
        return mDbId;
    }
    public void setCourseDbId(long id) { mCourseDbId = id; }
    public long getCourseDbId() { return mCourseDbId; }

    public void setDbId(long dbId) {
        mDbId = dbId;
    }

    public Location getTeeLocation(TeeCategory category) {
        return mTeeLocationMap.get(category);
    }

    public void setTeeLocation(TeeCategory category, Location location) {
        mTeeLocationMap.put(category, location);
    }

    public Location getBasketLocation(BasketCategory category) {
        return mBasketLocationMap.get(category);
    }

    public void setBasketLocation(BasketCategory category, Location location) {
        mBasketLocationMap.put(category, location);
    }

    public int getPar() {
        return mPar;
    }

    public void setPar(int par) {
        mPar = par;
    }

    // Parcelable implementation.
    public int describeContents() {
        return 0;
    }

    private DiscGolfHoleInfo(Parcel in) {
        mName = in.readString();
        mPar = in.readInt();
        mDbId = in.readInt();
        int teeCount = in.readInt();
        for (int count = 0; count < teeCount; count++) {
            TeeCategory key = TeeCategory.values()[in.readInt()];
            Location location = in.readParcelable(Location.class.getClassLoader());
            mTeeLocationMap.put(key, location);
        }
        int basketCount = in.readInt();
        for (int count = 0; count < basketCount; count++) {
            BasketCategory key = BasketCategory.values()[in.readInt()];
            Location location = in.readParcelable(Location.class.getClassLoader());
            mBasketLocationMap.put(key, location);
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeInt(mPar);
        out.writeLong(mDbId);
        out.writeInt(mTeeLocationMap.size());
        for (TeeCategory key : mTeeLocationMap.keySet()) {
            out.writeInt(key.ordinal());
            out.writeParcelable(mTeeLocationMap.get(key), flags);
        }
        out.writeInt(mBasketLocationMap.size());
        for (BasketCategory key : mBasketLocationMap.keySet()) {
            out.writeInt(key.ordinal());
            out.writeParcelable(mBasketLocationMap.get(key), flags);
        }
    }

    public static final Parcelable.Creator<DiscGolfHoleInfo> CREATOR =
            new Parcelable.Creator<DiscGolfHoleInfo>() {
                public DiscGolfHoleInfo createFromParcel(Parcel in) {
                    return new DiscGolfHoleInfo(in);
                }
                public DiscGolfHoleInfo[] newArray(int size) {
                    return new DiscGolfHoleInfo[size];
                }
            };
}
