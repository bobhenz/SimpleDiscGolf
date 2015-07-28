package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class DiscGolfHole implements DiscGolfThrow.ChangeCallbacks {
    private List<DiscGolfThrow> mThrowArray = new ArrayList<DiscGolfThrow>();

    private String mName;
    private int mPar;

    public enum TeeCategory {UNKNOWN, NOVICE, ADVANCED};
    private TeeCategory mTeeCategory;
    private ViewGroup mThrowGroup;
    private DiscGolfLocation mDgLocation;
    private DiscGolfLocationButton mTeeButton;
    private DiscGolfLocationButton mBasketButton;
    private View mLayout;

    public View getView() {
        return mLayout;
    }


    public DiscGolfHole(String name, int par, TeeCategory category, DiscGolfLocation dgl, ViewGroup layout) {
        if (name.isEmpty()) {
            name = "1";
        }

        if (par <= 0) {
            par = 3;
        }

        if (category == TeeCategory.UNKNOWN) {
            category = TeeCategory.ADVANCED;
        }

        mName = name;
        mTeeCategory = category;
        mPar = par;
        mDgLocation = dgl;
        mLayout = layout;
        mTeeButton = new DiscGolfLocationButton((Button)mLayout.findViewById(R.id.button_tee), "tee", "Tee", mDgLocation);
        mBasketButton = new DiscGolfLocationButton((Button)mLayout.findViewById(R.id.button_basket), "basket", "Basket", mDgLocation);
        mThrowGroup = (ViewGroup)mLayout.findViewById(R.id.throw_group);
        for (int count = 0; count < mPar; count++) {
            addThrow();
        }
    }

    public String getName() {
        return mName;
    }

    public int getPar() {
        return mPar;
    }

    public TeeCategory getTeeCategory() {
        return mTeeCategory;
    }

    private void updateThrows (int startIndex) {
        // Touch up the remaining throws.
        Location startLocation;
        if (startIndex == 0) {
            startLocation = mTeeButton.getMarkedLocation();
        } else {
            startLocation = mThrowArray.get(startIndex - 1).getMarkedLocation();
        }

        // Touch up the throws that came after the changed one.
        for (int throwIndex = startIndex; throwIndex < mThrowArray.size(); throwIndex++) {
            DiscGolfThrow dgthrow = mThrowArray.get(throwIndex);
            dgthrow.setStroke(throwIndex + 1);
            dgthrow.setStartLocation(startLocation);
            startLocation = dgthrow.getMarkedLocation();
        }
    }

    public void update(DiscGolfThrow object, Location location) {
        int changedIndex = mThrowArray.indexOf(object);
        updateThrows(changedIndex + 1);
    }

    public void removing(DiscGolfThrow object)
    {
        // Remove the throws from our list.
        int removedIndex = mThrowArray.indexOf(object);
        mThrowArray.remove(object);
        updateThrows(removedIndex);
    }

    public void addThrow() {
        Location startLocation;

        if (mThrowArray.size() > 0) {
            startLocation = mThrowArray.get(mThrowArray.size()-1).getMarkedLocation();
        } else {
            startLocation = mTeeButton.getMarkedLocation();
        }

        mThrowArray.add(new DiscGolfThrow(mThrowArray.size() + 1, mThrowGroup, startLocation, mDgLocation, this));
    }

    public static String getNextHoleName(String name) {
        // divide name into numeric prefix and non-numeric postfix
        // if numeric prefix, increment prefix, append postfix, return
        // else divide name into non-numeric prefix and numeric postfix
        // if numeric postfix, increment postfix, prepend prefix, return
        // else see if there are any numbers in name. If so, increment the first one, append and prepend other characters, return
        // if we get here then we didn't find numbers
        // if first character of string is not 'Z' or 'z', increment character, return
        // We give up. Return the
        // return name
        int value = Integer.parseInt(name);
        value = value + 1;
        return Integer.toString(value);
    }
}
