package com.byobdev.kamal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byobdev.kamal.DBClasses.Initiative;
import com.byobdev.kamal.DBClasses.Interests;
import com.byobdev.kamal.DBClasses.User;
import com.byobdev.kamal.NotificationServices.MyFirebaseInstanceIDService;
import com.byobdev.kamal.NotificationServices.MyFirebaseMessagingService;
import com.byobdev.kamal.AppHelpers.LocationGPS;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import com.google.android.gms.maps.model.MapStyleOptions;

import static android.R.id.primary;
import static android.os.Build.VERSION_CODES.M;


public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {

    //Maps
    GoogleMap initiativesMap;
    SupportMapFragment mapFragment;
    private static final String TAG = InitiativesActivity.class.getSimpleName();
    //Others
    Marker interestedMarker;
    FrameLayout shortDescriptionFragment;
    private float mLastPosY;
    //int notificationID = 10;
    private DatabaseReference userInterestsDB;
    private DatabaseReference initiativesDB;
    private DatabaseReference userDataDB;
    public Interests userInterests;
    public List<Initiative> initiativeList;
    public HashMap initiativeHashMap;
    public List<Marker> comidaMarkerList;
    public List<Marker> deporteMarkerList;
    public List<Marker> teatroMarkerList;
    public List<Marker> musicaMarkerList;
    public static final String PREFS_NAME = "KamalPreferences";
    public boolean comidaOn=false;
    public boolean deporteOn=false;
    public boolean teatroOn=false;
    public boolean musicaOn=false;
    View vista;
    TextView txtv_user, txtv_mail;
    ImageView img_profile;
    String msg = "Inicia sesion para habilitar otras funciones";
    boolean opened_bottom;
    public int authListenerCounter=0;

    //User Auth Listener
    AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                userDataDB = FirebaseDatabase.getInstance().getReference("Users");
                User user=new User(currentUser.getDisplayName(),currentUser.getEmail(),currentUser.getPhotoUrl().toString(), FirebaseInstanceId.getInstance().getToken());
                userDataDB.child(currentUser.getUid()).setValue(user);
                //Add Read interests listener
                userInterestsDB = FirebaseDatabase.getInstance().getReference("Interests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                userInterestsDB.addValueEventListener(userInterestslistener);
                authListenerCounter++;

                //Button visibility login
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                //navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_login).setVisible(false);
                navigationView.getMenu().findItem(R.id.initiates_logout).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(true);
                //navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(true);
                //navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(true);
                //Menu Header
                txtv_user.setText(currentUser.getDisplayName());
                txtv_mail.setText(currentUser.getEmail());
                Picasso.with(getApplicationContext()).load(currentUser.getProviderData().get(0).getPhotoUrl()).into(img_profile);
            } else{
                //Button visibility logout
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                //navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_login).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_logout).setVisible(false);
                navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(false);
                navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(false);
                //navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(true);
                //navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(false);
                //Menu Header
                txtv_mail.setText(msg);
                txtv_user.setText("");
                img_profile.setImageResource(android.R.color.transparent);
                //Remove Read interests listener
                if(authListenerCounter>0){
                    userInterestsDB.removeEventListener(userInterestslistener);
                    authListenerCounter--;
                }
            }
        }
    };

    //User Interests Listener
    ValueEventListener userInterestslistener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userInterests = dataSnapshot.getValue(Interests.class);
            if(userInterests!=null){
                if(userInterests.Musica){
                    FirebaseMessaging.getInstance().subscribeToTopic("Musica");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Musica");
                }

                if(userInterests.Deporte){
                    FirebaseMessaging.getInstance().subscribeToTopic("Deporte");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Deporte");
                }

                if(userInterests.Comida){
                    FirebaseMessaging.getInstance().subscribeToTopic("Comida");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Comida");
                }
                if(userInterests.Teatro){
                    FirebaseMessaging.getInstance().subscribeToTopic("Teatro");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Teatro");
                }
            }
            else{
                userInterests=new Interests(false,false,false,false);
            }

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
                Marker aux;
                if(initiative.Tipo.equals("Comida")){
                    if(comidaOn){
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                        );
                        comidaMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }
                    else{
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                                .visible(false)
                        );
                        comidaMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }


                }
                else if(initiative.Tipo.equals("Deporte")){
                    if(deporteOn){
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                        );
                        deporteMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }
                    else{
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                                .visible(false)
                        );
                        deporteMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }

                }
                else if(initiative.Tipo.equals("Teatro")){
                    if(teatroOn){
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                        );
                        teatroMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }
                    else{
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                                .visible(false)
                        );
                        teatroMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }

                }
                else if(initiative.Tipo.equals("Musica")){
                    if(musicaOn){
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                        );
                        musicaMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }
                    else{
                        aux=initiativesMap.addMarker(new MarkerOptions()
                                .position(new LatLng(initiative.Latitud, initiative.Longitud))
                                .visible(false)
                        );
                        musicaMarkerList.add(aux);
                        initiativeHashMap.put(aux.getId(),initiative);
                    }
                }
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
            Marker aux;
            if(initiative.Tipo.equals("Comida")){
                if(comidaOn){
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                    );
                    comidaMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }
                else{
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .visible(false)
                    );
                    comidaMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }


            }
            else if(initiative.Tipo.equals("Deporte")){
                if(deporteOn){
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                    );
                    deporteMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }
                else{
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .visible(false)
                    );
                    deporteMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }

            }
            else if(initiative.Tipo.equals("Teatro")){
                if(teatroOn){
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                    );
                    teatroMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }
                else{
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .visible(false)
                    );
                    teatroMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }

            }
            else if(initiative.Tipo.equals("Musica")){
                if(musicaOn){
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                    );
                    musicaMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }
                else{
                    aux=initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .visible(false)
                    );
                    musicaMarkerList.add(aux);
                    initiativeHashMap.put(aux.getId(),initiative);
                }
            }
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
        startService(new Intent(getBaseContext(), MyFirebaseInstanceIDService.class));
        startService(new Intent(getBaseContext(), MyFirebaseMessagingService.class));
        NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        opened_bottom = true;
        // Cancelamos la Notificacion que hemos comenzado
        //nm2.cancel(getIntent().getExtras().getInt("notificationID")); //para rescatar id
        nm2.cancelAll();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        comidaOn = settings.getBoolean("comida", false);
        deporteOn = settings.getBoolean("deporte", false);
        teatroOn = settings.getBoolean("teatro", false);
        musicaOn = settings.getBoolean("musica", false);
        final View iniciativaDeportes = findViewById(R.id.botonDeportes);
        final View iniciativaComida = findViewById(R.id.botonComida);
        final View iniciativaTeatro = findViewById(R.id.botonTeatro);
        final View iniciativaMusica = findViewById(R.id.botonMusica);
        if(comidaOn){
            iniciativaComida.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
        }
        if(deporteOn){
            iniciativaDeportes.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
        }
        if(teatroOn){
            iniciativaTeatro.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
        }
        if(musicaOn){
            iniciativaMusica.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
        }


        initiativeList = new Vector<>();
        initiativeHashMap=new HashMap();
        comidaMarkerList = new Vector<>();
        teatroMarkerList = new Vector<>();
        deporteMarkerList = new Vector<>();
        musicaMarkerList = new Vector<>();


        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Short description fragment set
        shortDescriptionFragment = (FrameLayout) findViewById(R.id.shortDescriptionFragment);
        shortDescriptionFragment.setOnTouchListener(this);

        //Menu
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*View search = findViewById(R.id.search);
        search.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                drawer.openDrawer(Gravity.LEFT);
                return false;
            }
        });*/
        View llMenu = findViewById(R.id.linearLayoutMenu);
        llMenu.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                drawer.openDrawer(Gravity.LEFT);
                return false;
            }
        });

        iniciativaComida.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(comidaOn){
                    iniciativaComida.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.Primary));
                    comidaOn=false;
                    for (Marker marker:comidaMarkerList) {
                        marker.setVisible(false);
                    }
                }
                else{
                    iniciativaComida.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
                    comidaOn=true;
                    for (Marker marker:comidaMarkerList) {
                        marker.setVisible(true);
                    }
                }
                return false;
            }
        });


        iniciativaDeportes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(deporteOn){
                    iniciativaDeportes.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.Primary));
                    deporteOn=false;
                    for (Marker marker:deporteMarkerList) {
                        marker.setVisible(false);
                    }
                }
                else{
                    iniciativaDeportes.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));;
                    deporteOn=true;
                    for (Marker marker:deporteMarkerList) {
                        marker.setVisible(true);
                    }
                }
                return false;
            }
        });

        iniciativaTeatro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(teatroOn){
                    iniciativaTeatro.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.Primary));
                    teatroOn=false;
                    for (Marker marker:teatroMarkerList) {
                        marker.setVisible(false);
                    }
                }
                else{
                    iniciativaTeatro.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));;
                    teatroOn=true;
                    for (Marker marker:teatroMarkerList) {
                        marker.setVisible(true);
                    }
                }
                return false;
            }
        });

        iniciativaMusica.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(musicaOn){
                    iniciativaMusica.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.Primary));
                    musicaOn=false;
                    for (Marker marker:musicaMarkerList) {
                        marker.setVisible(false);
                    }
                }
                else{
                    iniciativaMusica.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));;
                    musicaOn=true;
                    for (Marker marker:musicaMarkerList) {
                        marker.setVisible(true);
                    }
                }
                return false;
            }
        });


        View view = navigationView.getHeaderView(0);


        //User & mail headers
        txtv_user = (TextView)view.findViewById(R.id.initiates_user);
        txtv_mail = (TextView)view.findViewById(R.id.initiates_mail);
        img_profile = (ImageView)view.findViewById(R.id.initiates_img_profile);


        userInterests=new Interests(false,false,false,false);
        vista= findViewById(R.id.bottom_menu);

    }

    @Override
    public void onStart() {
        super.onStart();
        //User Auth Listener
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        //Read initiatives listener
        initiativesDB = FirebaseDatabase.getInstance().getReference("Initiatives");
        initiativesDB.addListenerForSingleValueEvent(initiativesInitListener);
        initiativesDB.addChildEventListener(initiativesListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //User Auth Listener
        if (authListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener);
        }
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("deporte", deporteOn);
        editor.putBoolean("comida", comidaOn);
        editor.putBoolean("teatro", teatroOn);
        editor.putBoolean("musica", musicaOn);
        editor.commit();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initiativesMap = googleMap;
        LocationGPS start = new LocationGPS(getApplicationContext());
        final LatLng interested;
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        //Dummy points
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initiativesMap.setMyLocationEnabled(true);
        }

        interested = new LatLng(start.getLatitud(),start.getLongitud());
        initiativesMap.moveCamera(CameraUpdateFactory.newLatLngZoom(interested,15));
        initiativesMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Agrego datos del pin
                Bundle bn = new Bundle();
                Initiative initiative=(Initiative)initiativeHashMap.get(marker.getId());
                bn.putString("Titulo",initiative.Titulo);
                bn.putString("imagen",initiative.image);
                bn.putString("Descripcion",initiative.Descripcion);
                bn.putString("Nombre",initiative.Nombre);
                bn.putString("Direccion", initiative.Direccion);
                bn.putString("hInicio", initiative.hInicio);
                bn.putString("hFin", initiative.hTermino);
                //le paso los datos al fragment
                DescriptionFragment DF = new DescriptionFragment();
                DF.setArguments(bn);

                //Hago aparecer fragment
                marker.hideInfoWindow();
                if(shortDescriptionFragment.getVisibility() == View.GONE){
                    shortDescriptionFragment.setVisibility(View.VISIBLE);
                }

                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.shortDescriptionFragment, DF);

                //Log
                if (shortDescriptionFragment.getTranslationY() >= shortDescriptionFragment.getHeight()){
                    OvershootInterpolator interpolator;
                    interpolator = new OvershootInterpolator(1);
                    shortDescriptionFragment.animate().setInterpolator(interpolator).translationYBy(-shortDescriptionFragment.getMeasuredHeight()).setDuration(600);
                    vista.animate().setInterpolator(interpolator).translationYBy(vista.getMeasuredHeight()).setDuration(600);
                    opened_bottom = false;
                }
                trans.commit();
                Log.d("MAP", "Entro a " + marker.getTitle());

                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        int maxX = mdispSize.x;
        int maxY = mdispSize.y;
        float currentPosition;
        int fragment_pos[] = new int[2];
        int bottom_pos[] = new int[2];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastPosY = event.getY();
                return true;
            case (MotionEvent.ACTION_MOVE):
                currentPosition = event.getY();
                float deltaY = mLastPosY - currentPosition;
                float transY = View.TRANSLATION_Y.get(v);
                transY -= deltaY;
                if (transY < 0){
                    transY = 0;
                }
                v.setTranslationY(transY);
                return true;
            case (MotionEvent.ACTION_UP):
                v.getLocationOnScreen(fragment_pos);
                if (fragment_pos[1] >= maxY-700){
                    OvershootInterpolator interpolator;
                    interpolator = new OvershootInterpolator(1);
                    shortDescriptionFragment.animate().setInterpolator(interpolator).translationY(shortDescriptionFragment.getMeasuredHeight()).setDuration(600);
                    vista.animate().setInterpolator(interpolator).translationYBy(-vista.getMeasuredHeight()).setDuration(600);
                    opened_bottom = false;
                    return true;
                }
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
        }
        else if(!opened_bottom){
            OvershootInterpolator interpolator;
            interpolator = new OvershootInterpolator(1);
            shortDescriptionFragment.animate().setInterpolator(interpolator).translationY(shortDescriptionFragment.getMeasuredHeight()).setDuration(600);
            vista.animate().setInterpolator(interpolator).translationYBy(-vista.getMeasuredHeight()).setDuration(600);
            opened_bottom = true;
        }
        else {
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
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //case R.id.initiates_search:
            //    break;
            case  R.id.initiates_login:
                Intent login = new Intent();
                login.setClassName("com.byobdev.kamal","com.byobdev.kamal.LoginActivity");
                startActivityForResult(login,0);
            case R.id.initiates_logout:
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    new AlertDialog.Builder(this)
                            .setTitle("Confirmacion de cierre de sesion")
                            .setMessage("Seguro que quieres cerrar sesion?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.unlink(user.getProviderId());
                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();


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
            //case R.id.initiates_settings:
            //    break;
            //case R.id.initiates_recent:
            //    break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
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
