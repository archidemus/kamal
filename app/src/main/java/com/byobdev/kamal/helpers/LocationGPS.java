package com.byobdev.kamal.helpers;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by nano on 7/23/17.
 */

public class LocationGPS extends Service implements LocationListener{

    private final Context context;

    boolean isGPSOn = false;
    boolean isNetOn = false;
    boolean canGetLoc = false;

    Location mobileLocation;
    double latitude, longitude;

    private static final long MIN_DIST_UPDATE = 10; // 10 meters
    private static final long MIN_TIME_UPDATE = 1000 * 60; // 60 seconds
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    protected LocationManager locationManager;

    public LocationGPS(Context context) {
        this.context = context;
        getGPS();
    }


    private Location getGPS() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetOn && !isGPSOn) {
                Toast.makeText(context.getApplicationContext(), "No fue posible determinar la ubicación", Toast.LENGTH_LONG).show();
            } else {
                this.canGetLoc = true;
                if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return null;
                }
                if (isNetOn) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DIST_UPDATE,this);
                    if (locationManager != null){

                        mobileLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.d("NetOn:","isOn"); //Si aparece en el log de android -> well played
                        if(mobileLocation !=null){
                            latitude = mobileLocation.getLatitude();
                            longitude = mobileLocation.getLongitude();
                        }
                    }
                }
            }

            if(isGPSOn){
                if (mobileLocation == null){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_UPDATE,MIN_DIST_UPDATE, this);
                    Log.d("GPSOn:","isOn");//Si aparece en el log de android -> well played
                    if(locationManager != null){
                        mobileLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (mobileLocation != null){
                            latitude = mobileLocation.getLatitude();
                            longitude = mobileLocation.getLongitude();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mobileLocation;
    }

    public String getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> adresses;

        adresses = geocoder.getFromLocation(latitude,longitude,1);

        if(adresses.get(0).getAddressLine(0) != null){
            return adresses.get(0).getAddressLine(0);
        }
        else{
            return "No se obtuvo la dirección";
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public double getLatitud(){
        if(mobileLocation != null){
            latitude = mobileLocation.getLatitude();
        }
        // return latitude
        return latitude;
    }

    public double getLongitud(){
        if(mobileLocation != null){
            longitude = mobileLocation.getLongitude();
        }
        // return longitude
        return longitude;
    }
    public boolean canGetLocation() {
        return this.canGetLoc;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (isBetterLocation(location, mobileLocation)){
            getGPS();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
