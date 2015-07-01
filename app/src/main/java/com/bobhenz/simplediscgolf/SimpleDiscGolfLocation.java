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

/**
 * Created by bhenz on 6/29/2015.
 */
public class SimpleDiscGolfLocation extends Service implements LocationListener {

    protected LocationManager mLocationManager;
    protected Context mActivityContext;
    protected boolean mIsGpsEnabled;
    protected Location mCurrentLocation;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;

    public SimpleDiscGolfLocation(Context context) {
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
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            mIsGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!mIsGpsEnabled) {
                showSettingsAlert();
                return;
            }

            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("SDGLocation", "Latitude:" + Double.toString(mCurrentLocation.getLatitude()));
            Log.d("SDGLocation", "Longitude:" + Double.toString(mCurrentLocation.getLongitude()));
        }

    }

    public void stop() {
        Log.d("SDGLocation", "stop");
        if(mLocationManager != null) {
            mLocationManager.removeUpdates(SimpleDiscGolfLocation.this);
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

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("SDGLocation-changed", "Latitude:" + Double.toString(mCurrentLocation.getLatitude()));
        Log.d("SDGLocation-changed", "Longitude:" + Double.toString(mCurrentLocation.getLongitude()));
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider == LocationManager.GPS_PROVIDER) {
            mIsGpsEnabled = false;
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
