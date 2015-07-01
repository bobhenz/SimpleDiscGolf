package com.bobhenz.simplediscgolf;

import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DiscGolfLocationButton implements View.OnClickListener, DiscGolfLocationListener {
    private DiscGolfLocation mDgLocation;
    private Button mButton;
    private Location mCurrentLocation;
    private Location mMarkedLocation;
    private enum State {UNMARKED, WAITING, MARKED};
    private State mState;

    public DiscGolfLocationButton(Button button, DiscGolfLocation dgLocation) {
        mDgLocation = dgLocation;
        mButton = button;
        mButton.setOnClickListener(this);
        mDgLocation.addListener(this);
        mState = State.UNMARKED;
        updateText();
    }

    public void onLocationChanged(Location location) {
        if (location.hasAccuracy() && location.getAccuracy() <= 10.0) {
            if (mState == State.WAITING) {
                mMarkedLocation = location;
                mState = State.MARKED;
            }
            mCurrentLocation = location;
            if (mCurrentLocation != null) { Log.d("button-changed", mCurrentLocation.toString()); }
        }
        updateText();
    }
    public void onClick(View view) {
        Log.d("button-clicked", "HERE");
        Location location = mDgLocation.getCurrentLocation();
        if (location.hasAccuracy() && location.getAccuracy() <= 10.0) {
            mMarkedLocation = location;
            mState = State.MARKED;
        } else {
            mState = State.WAITING;
        }
        updateText();
    }

    private void updateText() {
        if (mMarkedLocation != null) { Log.d("updateText:marked", mMarkedLocation.toString()); }
        if (mCurrentLocation != null) { Log.d("updateText:current", mCurrentLocation.toString()); }

        if ((mState == State.MARKED) && (mCurrentLocation != null) && (mMarkedLocation != null)) {
            mButton.setText(String.format("Tee\n(%.2f meters)", mCurrentLocation.distanceTo(mMarkedLocation)));
        } else if (mState == State.WAITING){
            mButton.setText("Tee\n(Waiting for GPS accuracy...)");
        } else {
            mButton.setText("Tee\n(Unmarked)");
        }
    }
}
