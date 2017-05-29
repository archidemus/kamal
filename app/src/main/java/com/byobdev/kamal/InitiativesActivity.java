package com.byobdev.kamal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class InitiativesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GoogleMap.OnMarkerClickListener clickListener;
    Marker interestedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiatives);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng interested, initiative1, initiative2, initiative3;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (interestedMarker != null){
                    interestedMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
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
            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

        Location start = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Dummy points
        interested = new LatLng(start.getLatitude(),start.getLongitude());
        initiative1 = new LatLng(start.getLatitude()-0.005000,start.getLongitude()+0.005000);
        initiative2 = new LatLng(start.getLatitude()+0.005500,start.getLongitude()-0.004000);
        initiative3 = new LatLng(start.getLatitude()-0.005400,start.getLongitude()+0.003000);
        interestedMarker = mMap.addMarker(new MarkerOptions().position(interested).title("interested"));
        mMap.addMarker(new MarkerOptions().position(initiative1).title("initiative1"));
        mMap.addMarker(new MarkerOptions().position(initiative2).title("initiative2"));
        mMap.addMarker(new MarkerOptions().position(initiative3).title("initiative3"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(interested,15));
        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Hago aparecer fragment
                if (!marker.getTitle().equals("interested")){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.shortDescriptionFragment, new shortDescriptionFragment());
                    ft.addToBackStack(null);
                    ft.commit();
                    //Log
                    Log.d("MAP", "Entro a " + marker.getTitle());
                }
                return false;
            }
        });
    }

}
