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
import android.os.AsyncTask;
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

import com.byobdev.kamal.AppHelpers.DirectionsJSONParser;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONObject;

import static android.R.attr.breakStrategy;
import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.id.primary;
import static android.os.Build.VERSION_CODES.M;
import static com.byobdev.kamal.R.id.map;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;


public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {

    //Maps
    GoogleMap initiativesMap;
    SupportMapFragment mapFragment;
    private static final String TAG = InitiativesActivity.class.getSimpleName();
    //Others
    Marker interestedMarker;
    FrameLayout shortDescriptionFragment;
    private float mLastPosY;
    private Polyline polyline;
    //int notificationID = 10;
    private DatabaseReference userInterestsDB;
    LocationGPS start;
    private String currentSector="";
    private DatabaseReference sectorDB;
    private DatabaseReference sectorDB1;
    private DatabaseReference sectorDB2;
    private DatabaseReference sectorDB3;
    private DatabaseReference sectorDB4;
    private DatabaseReference sectorDB5;
    private DatabaseReference sectorDB6;
    private DatabaseReference sectorDB7;
    private DatabaseReference sectorDB8;

    private DatabaseReference userDataDB;
    public Interests userInterests;
    public HashMap initiativeHashMap;
    public HashMap markerHashMap;
    public List<String> comidaInitiativeIDList;
    public List<String> deporteInitiativeIDList;
    public List<String> teatroInitiativeIDList;
    public List<String> musicaInitiativeIDList;
    public static final String PREFS_NAME = "KamalPreferences";
    public boolean comidaOn=false;
    public boolean deporteOn=false;
    public boolean teatroOn=false;
    public boolean musicaOn=false;
    public boolean skipinit=true;
    View vista;
    TextView txtv_user, txtv_mail;
    ImageView img_profile;
    String msg = "Inicia sesion para habilitar otras funciones";
    boolean opened_bottom;
    public int authListenerCounter=0;

    GoogleMap.OnCameraIdleListener cameraIdleListener=new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            double latitud=initiativesMap.getCameraPosition().target.latitude;
            double longitud=initiativesMap.getCameraPosition().target.longitude;
            String newSector=getSector(latitud,longitud);
            if(!(newSector.equals(currentSector))){
                initiativesMap.clear();
                removeListeners();
                initListeners(latitud,longitud);
                currentSector=newSector;


            }


        }
    };

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
                if(userInterests.radio500m){
                    FirebaseMessaging.getInstance().subscribeToTopic("radio500m");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("radio500m");
                }
                if(userInterests.radio3km){
                    FirebaseMessaging.getInstance().subscribeToTopic("radio3km");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("radio3km");
                }
                if(userInterests.radio10km){
                    FirebaseMessaging.getInstance().subscribeToTopic("radio10km");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("radio10km");
                }
            }
            else{
                userInterests=new Interests(false,false,false,false, false, false, false);
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
                float markerColor=HUE_AZURE;
                Marker aux;
                if(initiative.Estado==0){//aun no inicia
                    markerColor=HUE_AZURE;
                }
                else if(initiative.Estado==1){//en curso
                    markerColor=HUE_GREEN;
                }
                else if(initiative.Estado==2){//por terminar
                    markerColor=HUE_RED;
                }
                else if(initiative.Estado==3){//termino
                    continue;
                }

                aux=initiativesMap.addMarker(new MarkerOptions()
                        .position(new LatLng(initiative.Latitud, initiative.Longitud))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(markerColor))
                );
                initiativeHashMap.put(aux.getId(),initiative);
                markerHashMap.put(initiativeSnapshot.getKey(),aux);
                if(initiative.Tipo.equals("Comida")){
                    aux.setVisible(comidaOn);
                    comidaInitiativeIDList.add(initiativeSnapshot.getKey());
                }
                else if(initiative.Tipo.equals("Deporte")){
                    aux.setVisible(deporteOn);
                    deporteInitiativeIDList.add(initiativeSnapshot.getKey());
                }
                else if(initiative.Tipo.equals("Teatro")){
                    aux.setVisible(teatroOn);
                    teatroInitiativeIDList.add(initiativeSnapshot.getKey());
                }
                else if(initiative.Tipo.equals("Musica")){
                    aux.setVisible(musicaOn);
                    musicaInitiativeIDList.add(initiativeSnapshot.getKey());
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
            float markerColor=HUE_AZURE;
            Marker aux;
            if(initiative.Estado==0){//aun no inicia
                markerColor=HUE_AZURE;
            }
            else if(initiative.Estado==1){//en curso
                markerColor=HUE_GREEN;
            }
            else if(initiative.Estado==2){//por terminar
                markerColor=HUE_RED;
            }
            else if(initiative.Estado==3){//termino
                return;
            }
            if(markerHashMap.get(dataSnapshot.getKey())!=null){return;}
            aux=initiativesMap.addMarker(new MarkerOptions()
                    .position(new LatLng(initiative.Latitud, initiative.Longitud))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(markerColor))
            );
            initiativeHashMap.put(aux.getId(),initiative);
            markerHashMap.put(dataSnapshot.getKey(),aux);
            if(initiative.Tipo.equals("Comida")){
                aux.setVisible(comidaOn);
                comidaInitiativeIDList.add(dataSnapshot.getKey());
            }
            else if(initiative.Tipo.equals("Deporte")){
                aux.setVisible(deporteOn);
                deporteInitiativeIDList.add(dataSnapshot.getKey());
            }
            else if(initiative.Tipo.equals("Teatro")){
                aux.setVisible(teatroOn);
                teatroInitiativeIDList.add(dataSnapshot.getKey());
            }
            else if(initiative.Tipo.equals("Musica")){
                aux.setVisible(musicaOn);
                musicaInitiativeIDList.add(dataSnapshot.getKey());
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Marker aux= (Marker)markerHashMap.get(dataSnapshot.getKey());
            Initiative initiative=dataSnapshot.getValue(Initiative.class);
            if(aux==null){return;}
            if(initiative.Estado==0){//aun no inicia
                aux.setIcon(BitmapDescriptorFactory
                        .defaultMarker(HUE_AZURE));
            }
            else if(initiative.Estado==1){//en curso
                aux.setIcon(BitmapDescriptorFactory
                        .defaultMarker(HUE_GREEN));
            }
            else if(initiative.Estado==2){//por terminar
                aux.setIcon(BitmapDescriptorFactory
                        .defaultMarker(HUE_RED));
            }
            else if(initiative.Estado==3){//terminado
                initiativeHashMap.remove(aux.getId());
                markerHashMap.remove(dataSnapshot.getKey());
                aux.remove();
                if(initiative.Tipo.equals("Comida")){
                    for(int i=0;i<comidaInitiativeIDList.size();i++){
                        if(comidaInitiativeIDList.get(i).equals(dataSnapshot.getKey())){
                            comidaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
                else if(initiative.Tipo.equals("Deporte")){
                    for(int i=0;i<deporteInitiativeIDList.size();i++){
                        if(deporteInitiativeIDList.get(i).equals(dataSnapshot.getKey())){
                            deporteInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
                else if(initiative.Tipo.equals("Teatro")){
                    for(int i=0;i<teatroInitiativeIDList.size();i++){
                        if(teatroInitiativeIDList.get(i).equals(dataSnapshot.getKey())){
                            teatroInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
                else if(initiative.Tipo.equals("Musica")){
                    for(int i=0;i<musicaInitiativeIDList.size();i++){
                        if(musicaInitiativeIDList.get(i).equals(dataSnapshot.getKey())){
                            musicaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }

            }
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

    public String getSector(double latitude, double longitude){
        return Integer.toString((int)(latitude*100))+","+Integer.toString((int)(longitude*100));
    }
    void initListeners(double latitude,double longitude){
        int lat=(int)(latitude*100);
        int lg=(int)(longitude*100);
        String nb=Integer.toString(lat)+","+Integer.toString(lg);
        String nb1=Integer.toString(lat+1)+","+Integer.toString(lg);
        String nb2=Integer.toString(lat-1)+","+Integer.toString(lg);
        String nb3=Integer.toString(lat)+","+Integer.toString(lg+1);
        String nb4=Integer.toString(lat)+","+Integer.toString(lg-1);
        String nb5=Integer.toString(lat+1)+","+Integer.toString(lg+1);
        String nb6=Integer.toString(lat-1)+","+Integer.toString(lg-1);
        String nb7=Integer.toString(lat+1)+","+Integer.toString(lg-1);
        String nb8=Integer.toString(lat-1)+","+Integer.toString(lg+1);
        sectorDB=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb);
        sectorDB1=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb1);
        sectorDB2=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb2);
        sectorDB3=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb3);
        sectorDB4=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb4);
        sectorDB5=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb5);
        sectorDB6=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb6);
        sectorDB7=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb7);
        sectorDB8=FirebaseDatabase.getInstance().getReference("Initiatives/"+nb8);
        sectorDB.addChildEventListener(initiativesListener);
        sectorDB1.addChildEventListener(initiativesListener);
        sectorDB2.addChildEventListener(initiativesListener);
        sectorDB3.addChildEventListener(initiativesListener);
        sectorDB4.addChildEventListener(initiativesListener);
        sectorDB5.addChildEventListener(initiativesListener);
        sectorDB6.addChildEventListener(initiativesListener);
        sectorDB7.addChildEventListener(initiativesListener);
        sectorDB8.addChildEventListener(initiativesListener);
    }
    void removeListeners(){
        sectorDB.removeEventListener(initiativesListener);
        sectorDB1.removeEventListener(initiativesListener);
        sectorDB2.removeEventListener(initiativesListener);
        sectorDB3.removeEventListener(initiativesListener);
        sectorDB4.removeEventListener(initiativesListener);
        sectorDB5.removeEventListener(initiativesListener);
        sectorDB6.removeEventListener(initiativesListener);
        sectorDB7.removeEventListener(initiativesListener);
        sectorDB8.removeEventListener(initiativesListener);
        initiativeHashMap.clear();
        markerHashMap.clear();
        comidaInitiativeIDList.clear();
        teatroInitiativeIDList.clear();
        deporteInitiativeIDList.clear();
    }
    void loadInitiatives(){
        LatLngBounds curScreen = initiativesMap.getProjection()
                .getVisibleRegion().latLngBounds;
        int south=(int)(curScreen.southwest.latitude*100);
        int north=(int)(curScreen.northeast.latitude*100);
        int west=(int)(curScreen.southwest.longitude*100);
        int east=(int)(curScreen.northeast.longitude*100);



    }

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


        initiativeHashMap=new HashMap();
        markerHashMap=new HashMap();
        comidaInitiativeIDList = new Vector<>();
        teatroInitiativeIDList= new Vector<>();
        deporteInitiativeIDList = new Vector<>();
        musicaInitiativeIDList = new Vector<>();


        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
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
                    for (String aux:comidaInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(false);
                    }
                }
                else{
                    iniciativaComida.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
                    comidaOn=true;
                    for (String aux:comidaInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(true);
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
                    for (String aux:deporteInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(false);
                    }
                }
                else{
                    iniciativaDeportes.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
                    deporteOn=true;
                    for (String aux:deporteInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(true);
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
                    for (String aux:teatroInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(false);
                    }
                }
                else{
                    iniciativaTeatro.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
                    teatroOn=true;
                    for (String aux:teatroInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(true);
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
                    for (String aux:musicaInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(false);
                    }
                }
                else{
                    iniciativaMusica.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.PrimaryDark));
                    musicaOn=true;
                    for (String aux:musicaInitiativeIDList) {
                        ((Marker)markerHashMap.get(aux)).setVisible(true);
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


        userInterests=new Interests(false,false,false,false, false, false, false);
        vista= findViewById(R.id.bottom_menu);

    }

    @Override
    public void onStart() {
        super.onStart();
        //User Auth Listener
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        //Read initiatives listener
        //initiativesDB.addListenerForSingleValueEvent(initiativesInitListener);
        //initiativesDB = FirebaseDatabase.getInstance().getReference("Initiatives");
        //initiativesDB.addChildEventListener(initiativesListener);
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
        start = new LocationGPS(getApplicationContext());
        final LatLng interested;
        currentSector=getSector(start.getLatitud(),start.getLongitud());
        initListeners(start.getLatitud(),start.getLongitud());
        //initiativesDB = FirebaseDatabase.getInstance().getReference("Initiatives");
        //initiativesDB.addChildEventListener(initiativesListener);
        initiativesMap.setOnCameraIdleListener(cameraIdleListener);
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
                if(polyline!=null){
                    polyline.remove();}
                //Agrego datos del pin
                Bundle bn = new Bundle();
                Initiative initiative=(Initiative)initiativeHashMap.get(marker.getId());
                bn.putString("Titulo",initiative.Titulo);
                bn.putString("imagen",initiative.image);
                bn.putString("Descripcion",initiative.Descripcion);
                bn.putString("Nombre",initiative.Nombre);
                bn.putString("Direccion", initiative.Direccion);
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                DateFormat formatter1 = new SimpleDateFormat("HH:mm");
                bn.putString("hInicio", formatter.format(new Date(initiative.fechaInicio)));
                bn.putString("hFin", formatter1.format(new Date(initiative.fechaFin)));
                //le paso los datos al fragment
                DescriptionFragment DF = new DescriptionFragment();
                DF.setArguments(bn);




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

                String url = getDirectionsUrl(new LatLng(start.getLatitud(),start.getLongitud()), marker.getPosition());

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
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
                    opened_bottom = true;
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
            case R.id.initiates_settings:
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intentMain3 = new Intent(this, LoginActivity.class);
                    this.startActivity(intentMain3);
                    break;
                }
                else{
                    Intent intentMain2 = new Intent(this, ListActivity.class);
                    intentMain2.putExtra("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    this.startActivity(intentMain2);
                    break;
                }

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










    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            polyline=initiativesMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



}
