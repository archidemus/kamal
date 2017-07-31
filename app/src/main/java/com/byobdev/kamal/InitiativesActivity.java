package com.byobdev.kamal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.byobdev.kamal.helpers.LocationGPS;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Vector;

import static android.R.attr.data;

public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {
    //Maps
    GoogleMap initiativesMap;
    SupportMapFragment mapFragment;
    //Others
    Marker interestedMarker;
    FrameLayout shortDescriptionFragment;
    private float mLastPosY;
    //int notificationID = 10;
    private DatabaseReference userInterestsDB;
    private DatabaseReference initiativesDB;
    public Interests userInterests;
    public List<Initiative> initiativeList;

    //User Interests Listener
    ValueEventListener userInterestslistener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userInterests = dataSnapshot.getValue(Interests.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //Initiatives Init Listener
    ValueEventListener initiativesInitListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot initiativeSnapshot : dataSnapshot.getChildren()) {
                Initiative initiative=initiativeSnapshot.getValue(Initiative.class);
                initiativeList.add(initiative);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //Initiatives Permanent Listener
    ChildEventListener initiativesListener=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Initiative initiative=dataSnapshot.getValue(Initiative.class);
            initiativeList.add(initiative);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Read interests listener
        userInterests=new Interests(false,false,false);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            userInterestsDB = FirebaseDatabase.getInstance().getReference("Interests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userInterestsDB.addValueEventListener(userInterestslistener);
        }

        //Read initiatives listener
        initiativeList=new Vector<>();
        initiativesDB=FirebaseDatabase.getInstance().getReference("Initiatives");
        initiativesDB.addListenerForSingleValueEvent(initiativesInitListener);
        initiativesDB.addChildEventListener(initiativesListener);
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
                Toast.makeText(this,"asdf", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.initiates_login:
                Intent login = new Intent();
                login.setClassName("com.byobdev.kamal","com.byobdev.kamal.LoginActivity");
                startActivityForResult(login,0);
            case R.id.initiates_logout:
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    new AlertDialog.Builder(this)
                            .setTitle("Logout Confirmation")
                            .setMessage("Do you Really Want To Logout?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();
                                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                    navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.initiates_login).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.initiates_logout).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(true);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
                break;
            case R.id.initiates_initiative:
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intentMain3 = new Intent(this, LoginActivity.class);
                    this.startActivity(intentMain3);
                    break;
                }
                else{
                    Intent intentMain2 = new Intent(this, CreateInitiativeActivity.class);
                    this.startActivity(intentMain2);
                    break;
                }
            case R.id.initiates_manage:
                //Mostrar Intereses
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intentMain3 = new Intent(this, LoginActivity.class);
                    this.startActivity(intentMain3);
                    break;
                }
                else{
                    Intent intentMain2 = new Intent(this, SetInterestsActivity.class);
                    intentMain2.putExtra("userInterests",userInterests);
                    this.startActivity(intentMain2);
                    break;
                }
            case R.id.initiates_settings:
                //CODIGO DE PRUEBA PARA LEER LA LISTA DE INICIATIVAS
                for (Initiative initiativeListItem : initiativeList) {
                    Toast.makeText(this,initiativeListItem.Nombre, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.initiates_recent:
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.initiates_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(true);
        }
        else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(false);
            navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(false);
            navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(true);
        }
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