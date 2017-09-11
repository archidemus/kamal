package com.byobdev.kamal;

import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.byobdev.kamal.AppHelpers.ConnectivityStatus;
import com.byobdev.kamal.AppHelpers.NotificationHelper;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.google.android.gms.maps.model.MapStyleOptions;

import static com.byobdev.kamal.R.id.map;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;


public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {

    //Maps
    Polyline initiativePath;
    boolean polylineActive;
    GoogleMap initiativesMap;
    SupportMapFragment mapFragment;
    private static final String TAG = InitiativesActivity.class.getSimpleName();
    //Others
    FrameLayout shortDescriptionFragment;
    private float mLastPosY;
    //int notificationID = 10;
    private DatabaseReference userInterestsDB;
    LocationGPS start;
    Vector<String> sectors;
    Vector<DatabaseReference> sectorsDB;
    LatLng lastMarkerPosition;
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
    View vista;
    TextView txtv_user, txtv_mail;
    ImageView img_profile;
    String msg = "Inicia sesion para habilitar otras funciones";
    boolean opened_bottom;
    public int authListenerCounter=0;

    GoogleMap.OnCameraIdleListener cameraIdleListener=new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            //double latitud=initiativesMap.getCameraPosition().target.latitude;
            //double longitud=initiativesMap.getCameraPosition().target.longitude;
            //String newSector=getSector(latitud,longitud);
            //initiativesMap.clear();
            //removeListeners();
            //loadInitiatives();
            updateInitiatives();
            /*if(!(newSector.equals(currentSector))){
                initiativesMap.clear();
                removeListeners();
                initListeners(latitud,longitud);
                currentSector=newSector;


            }*/


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
                userInterests=new Interests(false,false,false,false, false, true, false);
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
            Marker aux= (Marker)markerHashMap.get(dataSnapshot.getKey());
            Initiative initiative=dataSnapshot.getValue(Initiative.class);
            if(aux==null){return;}
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

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public String getSector(double latitude, double longitude){
        return Integer.toString((int)(latitude*50))+","+Integer.toString((int)(longitude*50));
    }

    void removeListeners(){
        initiativeHashMap.clear();
        markerHashMap.clear();
        comidaInitiativeIDList.clear();
        teatroInitiativeIDList.clear();
        deporteInitiativeIDList.clear();
        for(DatabaseReference aux2:sectorsDB){
            aux2.removeEventListener(initiativesListener);
        }
        sectors.clear();
        sectorsDB.clear();
    }
    void unloadSector(String sector){
        Iterator<Map.Entry<String, Marker>> it = markerHashMap.entrySet().iterator();
        String currentSector;
        Initiative initiative;
        Vector<String> keys=new Vector<>();
        Vector<String> markerIds=new Vector<>();
        while (it.hasNext()) {
            Map.Entry<String, Marker> pair = it.next();
            initiative=(Initiative)initiativeHashMap.get(pair.getValue().getId());
            currentSector=getSector(initiative.Latitud,initiative.Longitud);
            if(currentSector.equals(sector)){
                keys.add(pair.getKey());
                markerIds.add(pair.getValue().getId());
                pair.getValue().remove();
                if(initiative.Tipo.equals("Comida")){
                    for(int i=0;i<comidaInitiativeIDList.size();i++){
                        if(comidaInitiativeIDList.get(i).equals(pair.getKey())){
                            comidaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
                else if(initiative.Tipo.equals("Deporte")){
                    for(int i=0;i<deporteInitiativeIDList.size();i++){
                        if(deporteInitiativeIDList.get(i).equals(pair.getKey())){
                            deporteInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
                else if(initiative.Tipo.equals("Teatro")){
                    for(int i=0;i<teatroInitiativeIDList.size();i++){
                        if(teatroInitiativeIDList.get(i).equals(pair.getKey())){
                            teatroInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
                else if(initiative.Tipo.equals("Musica")){
                    for(int i=0;i<musicaInitiativeIDList.size();i++){
                        if(musicaInitiativeIDList.get(i).equals(pair.getKey())){
                            musicaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
            }
        }
        for(String aux:keys){
            markerHashMap.remove(aux);
        }
        for(String aux:markerIds){
            initiativeHashMap.remove(aux);
        }
    }
    void updateInitiatives(){
        LatLngBounds curScreen = initiativesMap.getProjection()
                .getVisibleRegion().latLngBounds;
        int south=(int)(curScreen.southwest.latitude*50);
        int north=(int)(curScreen.northeast.latitude*50);
        int west=(int)(curScreen.southwest.longitude*50);
        int east=(int)(curScreen.northeast.longitude*50);
        int southIterator=south;
        int westIterator;
        boolean sectorloaded;
        int sectorSize=sectors.size();
        Vector<String> sectors2=new Vector<>();
        Vector<DatabaseReference> sectorsDB2=new Vector<>();
        while(southIterator<=north){
            westIterator=west;
            while(westIterator<=east){
                sectorloaded=false;
                String sector=Integer.toString(southIterator)+","+Integer.toString(westIterator);
                for(int i=0;i<sectorSize;i++){
                    if(sectors.get(i).equals(sector)){
                        sectorloaded=true;
                        sectors2.add(sector);
                        sectorsDB2.add(sectorsDB.get(i));

                    }

                }
                if(!sectorloaded){
                    sectors2.add(sector);
                    DatabaseReference sectorDB=FirebaseDatabase.getInstance().getReference("Initiatives/"+sector);
                    sectorDB.addChildEventListener(initiativesListener);
                    sectorsDB2.add(sectorDB);
                }
                westIterator++;
            }
            southIterator++;
        }
        boolean del;
        for(int i=0;i<sectors.size();i++){
            del=true;
            for(int j=0;j<sectors2.size();j++){
                if(sectors2.get(j).equals(sectors.get(i))){
                    del=false;
                }
            }
            if(del){
                unloadSector(sectors.get(i));
            }
        }
        sectors.clear();
        sectors=sectors2;
        sectorsDB.clear();
        sectorsDB=sectorsDB2;
    }
    void loadInitiatives(){
        LatLngBounds curScreen = initiativesMap.getProjection()
                .getVisibleRegion().latLngBounds;
        int south=(int)(curScreen.southwest.latitude*50);
        int north=(int)(curScreen.northeast.latitude*50);
        int west=(int)(curScreen.southwest.longitude*50);
        int east=(int)(curScreen.northeast.longitude*50);
        sectors=new Vector<>();
        sectorsDB=new Vector<>();
        int southIterator=south;
        int westIterator;
        while(southIterator<=north){
            westIterator=west;
            while(westIterator<=east){
                String sector=Integer.toString(southIterator)+","+Integer.toString(westIterator);
                sectors.add(sector);
                DatabaseReference sectorDB=FirebaseDatabase.getInstance().getReference("Initiatives/"+sector);
                sectorDB.addChildEventListener(initiativesListener);
                sectorsDB.add(sectorDB);
                westIterator++;
            }
            southIterator++;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ConnectivityStatus.isConnected(getApplicationContext())){
                ((NotificationHelper)getApplication()).Setc(6);
            }
            else {
            }

        }
    };

    //Toolbar set
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiatives);
        startService(new Intent(getBaseContext(), MyFirebaseInstanceIDService.class));
        startService(new Intent(getBaseContext(), MyFirebaseMessagingService.class));
        NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        opened_bottom = true;
        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_bottom_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
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
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                DateFormat formatter1 = new SimpleDateFormat("HH:mm");
                bn.putString("hInicio", formatter.format(new Date(initiative.fechaInicio)));
                bn.putString("hFin", formatter1.format(new Date(initiative.fechaFin)));
                //le paso los datos al fragment
                DescriptionFragment DF = new DescriptionFragment();
                DF.setArguments(bn);
                lastMarkerPosition=marker.getPosition();
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
        initiativesMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                loadInitiatives();
                initiativesMap.setOnCameraIdleListener(cameraIdleListener);
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
    public void showPath(View view){
        if(polylineActive && initiativePath!=null){
            initiativePath.remove();
            polylineActive=false;
        }
        OvershootInterpolator interpolator;
        interpolator = new OvershootInterpolator(1);
        shortDescriptionFragment.animate().setInterpolator(interpolator).translationY(shortDescriptionFragment.getMeasuredHeight()).setDuration(600);
        vista.animate().setInterpolator(interpolator).translationYBy(-vista.getMeasuredHeight()).setDuration(600);
        opened_bottom = true;
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(new LatLng(start.getLatitud(),start.getLongitud()))
                .to(lastMarkerPosition)
                .transportMode(TransportMode.WALKING)
                .language(Language.SPANISH)
                .unit(Unit.METRIC)

                .execute(new DirectionCallback()
                {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        if(status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
                            initiativePath=initiativesMap.addPolyline(polylineOptions);
                            polylineActive=true;
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });

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
        else if(polylineActive && initiativePath!=null){
            initiativePath.remove();
            polylineActive=false;
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
        getApplicationContext().unregisterReceiver(receiver);

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
        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

}
