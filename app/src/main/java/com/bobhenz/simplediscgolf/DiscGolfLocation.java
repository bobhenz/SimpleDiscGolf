package com.bobhenz.simplediscgolf;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DiscGolfLocation extends Service implements LocationListener {

    protected LocationManager mLocationManager;
    protected Context mActivityContext;
    protected boolean mIsGpsEnabled;
    protected Location mCurrentLocation;

    // The minimum distance to change Updates in meters
    //private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimum time between updates in milliseconds
   //private static final long MIN_TIME_BW_UPDATES = 0;

    public DiscGolfLocation(Context context) {
        mActivityContext = context;
    }

    public void start() {
        Log.d("SDGLocation", "start");
        if (mLocationManager == null) {
            try {
                mLocationManager = (LocationManager)mActivityContext.getSystemService(mActivityContext.LOCATION_SERVICE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mLocationManager != null) {
            mIsGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!mIsGpsEnabled) {
                showSettingsAlert();
                return;
            }

            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0, 0, this);

            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mCurrentLocation != null) {
                Log.d("SDGLocation", "Latitude:" + Double.toString(mCurrentLocation.getLatitude()));
                Log.d("SDGLocation", "Longitude:" + Double.toString(mCurrentLocation.getLongitude()));
                notifyListeners();
            }
        }

    }

    public void stop() {
        Log.d("SDGLocation", "stop");
        if(mLocationManager != null) {
            mLocationManager.removeUpdates(DiscGolfLocation.this);
        }
    }

    protected void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivityContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mActivityContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    interface HiResWatcherCallbacks {
        public abstract void update(int count, int maxCount, Location location, boolean bIsLast);
    }

    private class HiResWatcher {
        private HiResWatcherCallbacks cb;
        private int count;
        private int maxCount;
        private List<Location> sampleArray = new ArrayList<Location>();
        
        public HiResWatcher(HiResWatcherCallbacks callbacks, Location initialLocation) {
            cb = callbacks;
            maxCount = 5;
            // kick off the process by giving the caller an initial estimate.
            cb.update(0, maxCount, initialLocation, false);
        }

        private Location calculateBestLocation() {
            Location best = null;
            for (Location sample : sampleArray) {
                if (sampleArray.size() == maxCount)
                    Log.d("sample", String.format("%2d:", sampleArray.indexOf(sample)) + sample.toString());
                if ((best == null) || (sample.getAccuracy() <= best.getAccuracy())) {
                    best = sample;
                }
            }
            return best;
        }

        private static final float SAMPLE_ACCURACY_THRESHOLD = (float)10;
        private static final float SAMPLE_SPEED_THRESHOLD = (float)0.1;
        public boolean update(Location location) {
            boolean bIsLast = false;
            if ((location.getAccuracy() <= SAMPLE_ACCURACY_THRESHOLD) && (location.getSpeed() < SAMPLE_SPEED_THRESHOLD)) {
                count++;
                sampleArray.add(location);
                Location meanLocation = calculateBestLocation();
                bIsLast = (count >= maxCount);
                if (bIsLast) Log.d("Final Calculated", meanLocation.toString());
                cb.update(count, maxCount, meanLocation, bIsLast);
            } else {
                Log.d("Reject", location.toString());
            }
            return bIsLast;
        }

    }

    private List<HiResWatcher> mHiResWatchersArray = new ArrayList<HiResWatcher>();

    public void getHiResStationaryLocation(HiResWatcherCallbacks cb) {
        mHiResWatchersArray.add(new HiResWatcher(cb, mCurrentLocation));
    }

    private List<DiscGolfLocationListener> mListenerArray = new ArrayList<DiscGolfLocationListener>();

    public void addListener(DiscGolfLocationListener listener) {
        mListenerArray.add(listener);
    }

    public void removeListener(DiscGolfLocationListener listener) {
        mListenerArray.remove(listener);
    }

    private void notifyListeners() {
        for (HiResWatcher hires : mHiResWatchersArray) {
            boolean bDone = hires.update(mCurrentLocation);
            if (bDone) {
                mHiResWatchersArray.remove(hires);
            }
        }
        for (DiscGolfLocationListener listener : mListenerArray) {
            listener.onLocationChanged(mCurrentLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mCurrentLocation != null) { Log.d("SDGLocation-changed", mCurrentLocation.toString()); }
        notifyListeners();
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider == LocationManager.GPS_PROVIDER) {
            mIsGpsEnabled = false;
            showSettingsAlert();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider == LocationManager.GPS_PROVIDER) {
            mIsGpsEnabled = true;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
