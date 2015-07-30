package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfHoleInfo {
    private String mName;
    private int mPar;
    private List<Location> mStrokeArray = new ArrayList<Location>();

    private Location mTeeLocation;
    private Location mBasketLocation;

    public enum TeeCategory {UNKNOWN, NOVICE, ADVANCED};
    private TeeCategory mTeeCategory;

    public DiscGolfHoleInfo(String name, int par, TeeCategory category) {
        mName = name;
        mPar = par;
        mTeeCategory = category;
    }

    public Location getTeeLocation() {
        return mTeeLocation;
    }

    public void setTeeLocation(Location location) {
        mTeeLocation = location;
    }

    public Location getBasketLocation() {
        return mBasketLocation;
    }

    public void setBasketLocation(Location location) {
        mBasketLocation = location;
    }

    public int getPar() {
        return mPar;
    }

    public void setPar(int par) {
        mPar = par;
    }
}
