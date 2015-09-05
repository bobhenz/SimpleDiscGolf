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

    DiscGolfGameData() {
        mCurrentHoleIndex = 0;
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
