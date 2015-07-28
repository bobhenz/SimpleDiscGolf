package com.bobhenz.simplediscgolf;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by bhenz on 7/18/2015.
 */
public class DiscGolfThrow implements View.OnClickListener, DiscGolfLocation.HiResWatcherCallbacks {

    interface ChangeCallbacks {
        public abstract void update(DiscGolfThrow object, Location location);
        public abstract void removing(DiscGolfThrow object);
    }

    private Button mMarkButton;
    private Button mRemoveButton;
    private int mStroke;
    private DiscGolfLocation mDgLocation;
    private View mLayout;
    private ChangeCallbacks mCallbacks;
    private Location mStartLocation;
    private Location mMarkedLocation;

    public DiscGolfThrow(int stroke, ViewGroup parentView, Location startLocation, DiscGolfLocation dgLocation, ChangeCallbacks cb) {
        mStroke = stroke;
        mCallbacks = cb;
        mDgLocation = dgLocation;
        mStartLocation = startLocation;
        Context context = parentView.getContext();
        mLayout = LayoutInflater.from(context).inflate(R.layout.throwgroup, parentView, false);
        mMarkButton = (Button) mLayout.findViewById(R.id.id_button_throw);
        mMarkButton.setOnClickListener(this);
        mRemoveButton = (Button) mLayout.findViewById(R.id.id_button_rmthrow);
        mRemoveButton.setOnClickListener(this);
        //mMarkedLocation = mDgLocation.getCurrentLocation();
        updateText();
        parentView.addView(mLayout);
    }

    private void updateText() {
        String buttonText;
        Log.d("dgthrow-uptxt", "HERE");
        if (mStartLocation != null)
            Log.d("dgthrow-uptxt:start", mStartLocation.toString());
        else
            Log.d("dgthrow-uptxt:start", "null");
        if (mMarkedLocation != null) Log.d("dgthrow-uptxt:mark", mMarkedLocation.toString());
        if ((mStartLocation != null) && (mMarkedLocation != null)) {
            float distance = mStartLocation.distanceTo(mMarkedLocation);
            buttonText = String.format("%-3d  |  %.2f ft", mStroke, (distance * 100.0) / 2.54 / 12.0);
        } else {
            buttonText = String.format("%-3d  |  Unknown", mStroke);
        }
        mMarkButton.setText(buttonText);
    }

    public Location getMarkedLocation () {
        return mMarkedLocation;
    }

    public void setStartLocation (Location location) {
        Log.d("setStartLocation", "HERE");
        mStartLocation = location;
        updateText();
    }

    public int getStroke () {
        return mStroke;
    }

    public void setStroke(int stroke) {
        if (stroke > 0) {
            if (stroke != mStroke) {
                mStroke = stroke;
                updateText();
            }
        }
    }

    public void update(int count, int maxCount, Location location, boolean bIsLast) {
        mMarkedLocation = location;
        if (bIsLast) {
            updateText();
            mCallbacks.update(this, mMarkedLocation);
        } else {
            String buttonText = String.format("Wait... %d", maxCount - count);
            mMarkButton.setText(buttonText);
        }
    }

    public void onClick(View v) {
        if (v == mMarkButton) {
            Log.d("onClick mark", "HERE");
            mDgLocation.getHiResStationaryLocation(this);
        } else if (v == mRemoveButton) {
            Log.d("onClick remove", "HERE");
            mCallbacks.removing(this);
            ViewGroup parentView = (ViewGroup) mLayout.getParent();
            parentView.removeView(mLayout);
            mLayout = null;
            mStroke = 0;
            mMarkButton = null;
            mRemoveButton = null;
            mDgLocation = null;
        } else {
            Log.d("onClick unknown", "HERE");
        }
    }
}
