package com.byobdev.kamal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.byobdev.kamal.helpers.DrawerItemClickListener;
import com.byobdev.kamal.helpers.LocationGPS;
import com.byobdev.kamal.helpers.Notificaciones;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener {
    //Menu
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
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
        //Menu
        mPlanetTitles = getResources().getStringArray(R.array.menu_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
        //Short description fragment set
        shortDescriptionFragment = (FrameLayout) findViewById(R.id.shortDescriptionFragment);
        shortDescriptionFragment.setOnTouchListener(this);
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
    public void onRestart(){
        super.onRestart();
        //Borrar notificacion




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

