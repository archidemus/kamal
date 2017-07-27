package com.byobdev.kamal;

import android.location.LocationListener;
import android.location.LocationManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import com.byobdev.kamal.helpers.LocationGPS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {
    //Maps
    GoogleMap initiativesMap;
    SupportMapFragment mapFragment;
    //Others
    Marker interestedMarker;
    FrameLayout shortDescriptionFragment;
    private float mLastPosY;
    //int notificationID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiatives);
        NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Cancelamos la Notificacion que hemos comenzado
        //nm2.cancel(getIntent().getExtras().getInt("notificationID")); //para rescatar id
        nm2.cancelAll();

        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Short description fragment set
        shortDescriptionFragment = (FrameLayout) findViewById(R.id.shortDescriptionFragment);
        shortDescriptionFragment.setOnTouchListener(this);
        //Menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initiativesMap = googleMap;
        LocationGPS start = new LocationGPS(getApplicationContext());
        final LatLng interested, initiative1;

        //Dummy points
        interested = new LatLng(start.getLatitud(),start.getLongitud());
        initiative1 = new LatLng(start.getLatitud()-0.005000,start.getLongitud()+0.005000);
        interestedMarker = initiativesMap.addMarker(new MarkerOptions().position(interested).title("interested"));
        initiativesMap.addMarker(new MarkerOptions().position(initiative1).title("initiative1"));
        initiativesMap.moveCamera(CameraUpdateFactory.newLatLngZoom(interested,15));
        initiativesMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Hago aparecer fragment
                if (!marker.getTitle().equals("interested")){
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.shortDescriptionFragment, new DescriptionFragment());
                    //Log
                    if (shortDescriptionFragment.getTranslationY() >= shortDescriptionFragment.getHeight()){
                        OvershootInterpolator interpolator;
                        interpolator = new OvershootInterpolator(5);
                        shortDescriptionFragment.animate().setInterpolator(interpolator).translationYBy(-200).setDuration(500);
                    }
                    trans.addToBackStack(null);
                    trans.commit();
                    Log.d("MAP", "Entro a " + marker.getTitle());
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastPosY = event.getY();
                return true;
            case (MotionEvent.ACTION_MOVE):
                float currentPosition = event.getY();
                float deltaY = mLastPosY - currentPosition;
                float transY = View.TRANSLATION_Y.get(v);
                transY -= deltaY;

                if (transY < 0){
                    transY = 0;
                }
                v.setTranslationY(transY);
                return true;
            default:
                return v.onTouchEvent(event);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.initiates_search:
                break;
            case  R.id.initiates_login:
                Intent login = new Intent();
                login.setClassName("com.byobdev.kamal","com.byobdev.kamal.LoginActivity");
                startActivityForResult(login,0);
            case R.id.initiates_logout:
                break;
            case R.id.initiates_initiative:
                break;
            case R.id.initiates_manage:
                break;
            case R.id.initiates_settings:
                break;
            case R.id.initiates_recent:
                break;
            default:
                return false;
        }
        return false;
    }


    /*****CODIGO NOTIFICACIONES *******/
    public void notificacion(View view){
        NotificationCompat.Builder notificacion = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.kamal_logo) // icono en la barra de notificaciones
                .setLargeIcon((((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.kamal_logo)).getBitmap())) // icono cuando extiendes las notificaciones
                .setContentTitle("Iniciativa de interes cercana") // titulo notificacion
                .setContentText("Apreta aqui para ir a la iniciativa") // descripcion notificacion
                .setTicker("Iniciativa cercana")
                .setVibrate(new long [] {100, 1000}); // tiempo antes de vibrar y por cuanto tiempo vibra


        Intent inotificacion = new Intent(this, InitiativesActivity.class); // se genera el intente
        //inotificacion.putExtra("notificationID", notificationID); //Para rescatar la id despues
        PendingIntent intentePendiente = PendingIntent.getActivity(this,0,inotificacion,0); // se deja como pendiente

        notificacion.setContentIntent(intentePendiente);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //El 10 es la id, se puede poner cualquiera, deberiamos poner que sea
        nm.notify(10,notificacion.build());// se construye la notificacion


    }
}

