package com.bobhenz.simplediscgolf;

import android.location.Location;
import android.os.Bundle;
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
    private String mId;
    private String mText;

    public DiscGolfLocationButton(Button button, String id, String text, DiscGolfLocation dgLocation) {
        mDgLocation = dgLocation;
        mButton = button;
        mButton.setOnClickListener(this);
        mDgLocation.addListener(this);
        mState = State.UNMARKED;
        mId = id;
        mText = text;
        updateText();
    }

    public void saveState(Bundle state) {
        Log.d("save-tee-button", "HERE");
        state.putSerializable("button-state" + mId, mState);
        state.putParcelable("button-marked-location" + mId, mMarkedLocation);
        state.putParcelable("button-current-location" + mId, mCurrentLocation);
    }

    public void restoreState(Bundle state) {
        Log.d("restore-tee-button", "HERE");
        if (state != null) {
            Log.d("RESTORE-tee-button", "HERE");
            mState = (State)state.getSerializable("button-state" + mId);
            mMarkedLocation = state.getParcelable("button-marked-location" + mId);
            mCurrentLocation = state.getParcelable("button-current-location" + mId);
        }
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
            mButton.setText(String.format("%s\n%.2f m", mText, mCurrentLocation.distanceTo(mMarkedLocation)));
        } else if (mState == State.WAITING){
            mButton.setText(mText + "\n(Waiting for GPS accuracy...)");
        } else {
            mButton.setText(mText + "\n(Unmarked)");
        }
    }
}
