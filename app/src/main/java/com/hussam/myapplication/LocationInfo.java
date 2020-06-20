package com.hussam.myapplication;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;


class LocationInfo {
    private Context activity;
    private double locationAccuracy, locationLongitude, locationLatitude;
    private static final int FASTEST_INTERVAL = 5000;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean track;

    LocationInfo(Context context) {
        activity = context;
        track = true;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    void startTracking() {
        track = true;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (track) {
                    updateValues(location);
                    activity.sendBroadcast(new Intent("new_location"));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }

        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates("gps", FASTEST_INTERVAL, 0, locationListener);

    }

    void stopTracking(){
        track = false;
    }

    private void updateValues(Location location){
        locationAccuracy = location.getAccuracy();
        locationLatitude = location.getLatitude();
        locationLongitude = location.getLongitude();
    }

    double getLatitude(){
        return locationLatitude;
    }
    double getLongitude(){
        return locationLongitude;
    }
    double getAccuracy(){
        return locationAccuracy;
    }
    LocationListener getLocationListener() {return locationListener;}
    LocationManager getLocationManager(){return locationManager;}

}

