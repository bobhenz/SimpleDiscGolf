package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfGameData {
    private List<DiscGolfHoleData> mHoleArray = new ArrayList<DiscGolfHoleData>();
    private int mCurrentHoleIndex;
    private long mStartTimeMilliseconds;
    private long mDbId;

    DiscGolfGameData() {
        mCurrentHoleIndex = 0;
        mStartTimeMilliseconds = System.currentTimeMillis();
        mDbId = -1;
    }

    public long getDbId() { return mDbId; }
    public void setDbId(long id) { mDbId = id; }
    public void setStartTime(long timeMilliseconds) { mStartTimeMilliseconds = timeMilliseconds; }

    public boolean isEmpty() {
        return mHoleArray.isEmpty();
    }

    public void setCurrentHole(int index) {
        mCurrentHoleIndex = index;
    }

    public boolean gotoPriorHole() {
        if (mCurrentHoleIndex > 0) {
            mCurrentHoleIndex--;
            return true;
        } else {
            return false;
        }
    }

    public boolean gotoNextHole() {
        if (mCurrentHoleIndex < mHoleArray.size() - 1) {
            mCurrentHoleIndex++;
            return true;
        } else {
            return false;
        }
    }

    public List<DiscGolfHoleData> getHoleList() {
        return mHoleArray;
    }

    public void addHole(long holeInfoDbId) {
        DiscGolfHoleData hole = new DiscGolfHoleData(holeInfoDbId);
        mHoleArray.add(hole);
        mCurrentHoleIndex = mHoleArray.size()-1;
    }

    public void addHole(DiscGolfHoleData hole) {
        mHoleArray.add(hole);
        mCurrentHoleIndex = mHoleArray.size()-1;
    }

    public int getScore() {
        int totalScore = 0;
        for (DiscGolfHoleData hole : mHoleArray) {
            totalScore += hole.getStrokeCount();
        } // for

        return totalScore;
    }

    public DiscGolfHoleData getHole() {
        return mHoleArray.get(mCurrentHoleIndex);
    }
}
