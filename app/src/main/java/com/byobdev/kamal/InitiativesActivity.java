package com.byobdev.kamal;

import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.squareup.picasso.Transformation;
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
import com.byobdev.kamal.NotificationServices.MyFirebaseInstanceIDService;
import com.byobdev.kamal.NotificationServices.MyFirebaseMessagingService;
import com.byobdev.kamal.AppHelpers.LocationGPS;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
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
import static com.byobdev.kamal.R.id.rangeView;
import static com.byobdev.kamal.R.id.time_filter_menu;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static java.lang.Integer.parseInt;

import com.google.android.gms.location.LocationListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import me.bendik.simplerangeview.SimpleRangeView;

public class InitiativesActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    long Time;//Current time without minutes and seconds for the time filter
    long initialTime;//Initial time without minutes and seconds for the time filter
    long currentTime;
    SimpleDateFormat timelineFormatter;//Formatter for timeline month and day
    public static final String PREFS_NAME = "KamalPreferences";
    private static final String TAG = InitiativesActivity.class.getSimpleName();
    public Interests userInterests;
    public List<Marker> swipeMarkerList;
    public HashMap initiativeHashMap;
    public HashMap markerHashMap;
    public HashMap keywordVisibilityHashmap;
    public HashMap timeVisibilityHashmap;
    public List<String> comidaInitiativeIDList;
    public List<String> deporteInitiativeIDList;
    public List<String> teatroInitiativeIDList;
    public List<String> musicaInitiativeIDList;
    SearchView search;
    TextView fromDate;
    TextView toDate;
    public boolean comidaOn = true;
    public boolean deporteOn = true;
    public boolean teatroOn = true;
    public boolean musicaOn = true;
    public int authListenerCounter = 0;
    long lastTimeFilterStart;
    long lastTimeFilterEnd;
    //current position marker
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    //Menu
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    //Maps
    Polyline initiativePath;
    boolean polylineActive;
    GoogleMap initiativesMap;
    SupportMapFragment mapFragment;
    //Others
    List<TextView> timeLabels;
    FrameLayout previewFragment;
    FrameLayout descriptionFragment;
    Bundle selectedInitiative;
    boolean opened_bottom, opened_df, opened_pf, on_way, back_button_active;
    Marker selectedMarker;
    Toolbar toolbar;
    LocationGPS start;
    Vector<String> sectors;
    Vector<DatabaseReference> sectorsDB;
    LatLng lastMarkerPosition;
    View vista;
    TextView txtv_user, txtv_mail;
    RatingBar rtb;
    FirebaseUser currentUser;
    ImageView img_profile;
    View linea;
    float lastCameraZoom;
    SimpleRangeView rangeview;
    LinearLayout timeFilterMenu;
    int currentHour;
    String msg = "Inicia sesion para habilitar otras funciones";
    UiSettings uiSettings;
    //User Interests Listener
    ValueEventListener userInterestslistener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userInterests = dataSnapshot.getValue(Interests.class);
            if (userInterests != null) {
                if (userInterests.Musica) {
                    FirebaseMessaging.getInstance().subscribeToTopic("Musica");
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Musica");
                }

                if (userInterests.Deporte) {
                    FirebaseMessaging.getInstance().subscribeToTopic("Deporte");
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Deporte");
                }

                if (userInterests.Comida) {
                    FirebaseMessaging.getInstance().subscribeToTopic("Comida");
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Comida");
                }
                if (userInterests.Teatro) {
                    FirebaseMessaging.getInstance().subscribeToTopic("Teatro");
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Teatro");
                }
            } else {
                userInterests = new Interests(false, false, false, false, false, true, false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Teatro");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Comida");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Deporte");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Musica");
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Interests");
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInterests);

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    //Initiatives Permanent Listener
    ChildEventListener initiativesListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Initiative initiative = dataSnapshot.getValue(Initiative.class);
            float markerColor = HUE_AZURE;
            Marker aux;
            if (initiative.Estado == 3) {//termino
                return;
            }
            if (markerHashMap.get(dataSnapshot.getKey()) != null) {
                return;
            }
            boolean typeVisibility;
            if (initiative.Tipo.equals("Comida")) {

                if (initiative.Estado == 0) {//aun no inicia
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_foodgraymarker))
                    );
                } else if (initiative.Estado == 1) {//en curso
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_foodgreenmarker))
                    );
                } else {//por terminar
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_foodredmarker))
                    );
                }
                typeVisibility=comidaOn;
                comidaInitiativeIDList.add(dataSnapshot.getKey());
            } else if (initiative.Tipo.equals("Deporte")) {
                if (initiative.Estado == 0) {//aun no inicia
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sportgraymarker))
                    );
                } else if (initiative.Estado == 1) {//en curso
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sportgreenmarker))
                    );
                } else {//por terminar
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sportredmarker))
                    );
                }
                typeVisibility=deporteOn;;
                deporteInitiativeIDList.add(dataSnapshot.getKey());
            } else if (initiative.Tipo.equals("Teatro")) {
                if (initiative.Estado == 0) {//aun no inicia
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_theatergraymarker))
                    );
                } else if (initiative.Estado == 1) {//en curso
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_theatergreenmarker))
                    );
                } else {//por terminar
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_theaterredmarker))
                    );
                }
                typeVisibility=teatroOn;
                teatroInitiativeIDList.add(dataSnapshot.getKey());
            } else {//musica
                if (initiative.Estado == 0) {//aun no inicia
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_musicgraymarker))
                    );
                } else if (initiative.Estado == 1) {//en curso
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_musicgreenmarker))
                    );
                } else {//por terminar
                    aux = initiativesMap.addMarker(new MarkerOptions()
                            .position(new LatLng(initiative.Latitud, initiative.Longitud))
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_musicredmarker))
                    );
                }
                typeVisibility=musicaOn;
                musicaInitiativeIDList.add(dataSnapshot.getKey());
            }
            initiativeHashMap.put(aux.getId(), initiative);
            markerHashMap.put(dataSnapshot.getKey(), aux);
            boolean visibleByTime=true;
            boolean visibleByKeyword=true;
            if(timeFilterMenu.getVisibility() == View.VISIBLE){
                if(initiative.fechaFin<lastTimeFilterStart || initiative.fechaInicio>lastTimeFilterEnd){
                    visibleByTime=false;
                }
                else{
                    visibleByTime=true;
                }
            }
            if(!search.isIconified()){
                if(initiative.Descripcion.contains(search.getQuery().toString()) || initiative.Titulo.contains(search.getQuery().toString())){
                    visibleByKeyword=true;

                }
                else{
                    visibleByKeyword=false;
                }

            }
            keywordVisibilityHashmap.put(dataSnapshot.getKey(), visibleByKeyword);
            timeVisibilityHashmap.put(dataSnapshot.getKey(), visibleByTime);
            aux.setVisible(typeVisibility && visibleByKeyword && visibleByTime);
            //LatLngBounds bounds = initiativesMap.getProjection().getVisibleRegion().latLngBounds;


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Marker aux = (Marker) markerHashMap.get(dataSnapshot.getKey());
            Initiative initiative = dataSnapshot.getValue(Initiative.class);
            if (aux == null) {
                return;
            }
            if (initiative.Estado == 0) {//aun no inicia
                if (initiative.Tipo.equals("Comida")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_foodgraymarker));
                } else if (initiative.Tipo.equals("Deporte")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sportgraymarker));
                } else if (initiative.Tipo.equals("Teatro")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_theatergraymarker));
                } else {//musica
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_musicgraymarker));
                }
            } else if (initiative.Estado == 1) {//en curso
                if (initiative.Tipo.equals("Comida")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_foodgreenmarker));
                } else if (initiative.Tipo.equals("Deporte")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sportgreenmarker));
                } else if (initiative.Tipo.equals("Teatro")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_theatergreenmarker));
                } else {//musica
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_musicgreenmarker));
                }
            } else if (initiative.Estado == 2) {//por terminar
                if (initiative.Tipo.equals("Comida")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_foodredmarker));
                } else if (initiative.Tipo.equals("Deporte")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sportredmarker));
                } else if (initiative.Tipo.equals("Teatro")) {
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_theaterredmarker));
                } else {//musica
                    aux.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_musicredmarker));
                }
            } else if (initiative.Estado == 3) {//terminado
                initiativeHashMap.remove(aux.getId());
                markerHashMap.remove(dataSnapshot.getKey());
                keywordVisibilityHashmap.remove(dataSnapshot.getKey());
                timeVisibilityHashmap.remove(dataSnapshot.getKey());
                aux.remove();
                if (initiative.Tipo.equals("Comida")) {
                    for (int i = 0; i < comidaInitiativeIDList.size(); i++) {
                        if (comidaInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                            comidaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                } else if (initiative.Tipo.equals("Deporte")) {
                    for (int i = 0; i < deporteInitiativeIDList.size(); i++) {
                        if (deporteInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                            deporteInitiativeIDList.remove(i);
                            break;
                        }
                    }
                } else if (initiative.Tipo.equals("Teatro")) {
                    for (int i = 0; i < teatroInitiativeIDList.size(); i++) {
                        if (teatroInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                            teatroInitiativeIDList.remove(i);
                            break;
                        }
                    }
                } else if (initiative.Tipo.equals("Musica")) {
                    for (int i = 0; i < musicaInitiativeIDList.size(); i++) {
                        if (musicaInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                            musicaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }

            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Marker aux = (Marker) markerHashMap.get(dataSnapshot.getKey());
            Initiative initiative = dataSnapshot.getValue(Initiative.class);
            if (aux == null) {
                return;
            }
            initiativeHashMap.remove(aux.getId());
            markerHashMap.remove(dataSnapshot.getKey());
            keywordVisibilityHashmap.remove(dataSnapshot.getKey());
            timeVisibilityHashmap.remove(dataSnapshot.getKey());
            aux.remove();
            if (initiative.Tipo.equals("Comida")) {
                for (int i = 0; i < comidaInitiativeIDList.size(); i++) {
                    if (comidaInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                        comidaInitiativeIDList.remove(i);
                        break;
                    }
                }
            } else if (initiative.Tipo.equals("Deporte")) {
                for (int i = 0; i < deporteInitiativeIDList.size(); i++) {
                    if (deporteInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                        deporteInitiativeIDList.remove(i);
                        break;
                    }
                }
            } else if (initiative.Tipo.equals("Teatro")) {
                for (int i = 0; i < teatroInitiativeIDList.size(); i++) {
                    if (teatroInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
                        teatroInitiativeIDList.remove(i);
                        break;
                    }
                }
            } else if (initiative.Tipo.equals("Musica")) {
                for (int i = 0; i < musicaInitiativeIDList.size(); i++) {
                    if (musicaInitiativeIDList.get(i).equals(dataSnapshot.getKey())) {
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
    GoogleMap.OnCameraIdleListener cameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            updateInitiatives();

        }
    };
    private float mLastPosY;
    //int notificationID = 10;
    private DatabaseReference userInterestsDB;
    private DatabaseReference userDataDB;
    //User Auth Listener
    AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (opened_df == false) {

            }
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                userDataDB = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid());
                userDataDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // for (DataSnapshot child : snapshot.getChildren())
                        // Create a LinearLayout element
                        float rating;
                        int nVotos;
                        if (snapshot.child("rating").getValue() == null) {
                            rating = 0.0f;
                            nVotos = 0;
                            userDataDB.child("Nvotos").setValue(nVotos);
                            userDataDB.child("rating").setValue(rating);

                        } else {
                            rating = Float.parseFloat(snapshot.child("rating").getValue().toString());
                        }
                        userDataDB.child("Email").setValue(currentUser.getEmail());
                        userDataDB.child("ImageURL").setValue(currentUser.getPhotoUrl().toString());
                        userDataDB.child("Name").setValue(currentUser.getDisplayName());
                        rtb.setRating(rating);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                //Add Read interests listener
                userInterestsDB = FirebaseDatabase.getInstance().getReference("Interests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                userInterestsDB.addValueEventListener(userInterestslistener);
                authListenerCounter++;

                //Button visibility login
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                //navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_login).setVisible(false);
                navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(true);
                //navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(true);
                //Menu Header
                txtv_user.setText(currentUser.getDisplayName());
                txtv_mail.setText(currentUser.getEmail());
                linea.setVisibility(View.VISIBLE);

                Picasso.with(getApplicationContext()).load(currentUser.getProviderData().get(0).getPhotoUrl()).transform(new CircleTransform()).into(img_profile);
                //Picasso.with(getApplicationContext()).load(currentUser.getProviderData().get(0).getPhotoUrl()).into(img_profile);
            } else {
                //Button visibility logout
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                //navigationView.getMenu().findItem(R.id.initiates_search).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_login).setVisible(true);
                navigationView.getMenu().findItem(R.id.initiates_initiative).setVisible(false);
                navigationView.getMenu().findItem(R.id.initiates_manage).setVisible(false);
                navigationView.getMenu().findItem(R.id.initiates_settings).setVisible(false);
                //navigationView.getMenu().findItem(R.id.initiates_recent).setVisible(false);
                //Menu Header
                txtv_mail.setText(msg);
                txtv_user.setText("");
                rtb.setRating(0);
                linea.setVisibility(View.GONE);
                img_profile.setImageResource(android.R.color.transparent);
                //Remove Read interests listener
                if (authListenerCounter > 0) {
                    userInterestsDB.removeEventListener(userInterestslistener);
                    authListenerCounter--;
                }
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ConnectivityStatus.isConnected(getApplicationContext())) {
                ((NotificationHelper) getApplication()).Setc(6);
            } else {
            }

        }
    };


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public String getSector(double latitude, double longitude) {
        return Integer.toString((int) (latitude * 50)) + "," + Integer.toString((int) (longitude * 50));
    }

    public void filterByKeyword(String keyword) {
        Iterator<Map.Entry<String, Marker>> it = markerHashMap.entrySet().iterator();
        Initiative initiative;
        while (it.hasNext()) {
            Map.Entry<String, Marker> pair = it.next();
            initiative = (Initiative) initiativeHashMap.get(pair.getValue().getId());
            if (!initiative.Titulo.toLowerCase().contains(keyword.toLowerCase()) && !initiative.Descripcion.toLowerCase().contains(keyword.toLowerCase())) {
                keywordVisibilityHashmap.put(pair.getKey(), false);
                pair.getValue().setVisible(false);
            }

        }
    }

    public void resetFilter() {
        Iterator<Map.Entry<String, Marker>> it = markerHashMap.entrySet().iterator();
        Initiative initiative;
        while (it.hasNext()) {
            Map.Entry<String, Marker> pair = it.next();
            initiative = (Initiative) initiativeHashMap.get(pair.getValue().getId());
            if (!(boolean) keywordVisibilityHashmap.get(pair.getKey())) {
                if (initiative.Tipo.equals("Comida")) {
                    pair.getValue().setVisible(comidaOn && (boolean) timeVisibilityHashmap.get(pair.getKey()));
                } else if (initiative.Tipo.equals("Musica")) {
                    pair.getValue().setVisible(musicaOn && (boolean) timeVisibilityHashmap.get(pair.getKey()));
                } else if (initiative.Tipo.equals("Deporte")) {
                    pair.getValue().setVisible(deporteOn && (boolean) timeVisibilityHashmap.get(pair.getKey()));
                } else if (initiative.Tipo.equals("Teatro")) {
                    pair.getValue().setVisible(teatroOn && (boolean) timeVisibilityHashmap.get(pair.getKey()));
                }
                keywordVisibilityHashmap.put(pair.getKey(), true);
            }


        }

    }

    void removeListeners() {
        initiativeHashMap.clear();
        markerHashMap.clear();
        keywordVisibilityHashmap.clear();
        comidaInitiativeIDList.clear();
        teatroInitiativeIDList.clear();
        deporteInitiativeIDList.clear();
        for (DatabaseReference aux2 : sectorsDB) {
            aux2.removeEventListener(initiativesListener);
        }
        sectors.clear();
        sectorsDB.clear();
    }

    void unloadSector(String sector) {
        Iterator<Map.Entry<String, Marker>> it = markerHashMap.entrySet().iterator();
        String currentSector;
        Initiative initiative;
        Vector<String> keys = new Vector<>();
        Vector<String> markerIds = new Vector<>();
        while (it.hasNext()) {
            Map.Entry<String, Marker> pair = it.next();
            initiative = (Initiative) initiativeHashMap.get(pair.getValue().getId());
            currentSector = getSector(initiative.Latitud, initiative.Longitud);
            if (currentSector.equals(sector)) {
                keys.add(pair.getKey());
                markerIds.add(pair.getValue().getId());
                pair.getValue().remove();
                if (initiative.Tipo.equals("Comida")) {
                    for (int i = 0; i < comidaInitiativeIDList.size(); i++) {
                        if (comidaInitiativeIDList.get(i).equals(pair.getKey())) {
                            comidaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                } else if (initiative.Tipo.equals("Deporte")) {
                    for (int i = 0; i < deporteInitiativeIDList.size(); i++) {
                        if (deporteInitiativeIDList.get(i).equals(pair.getKey())) {
                            deporteInitiativeIDList.remove(i);
                            break;
                        }
                    }
                } else if (initiative.Tipo.equals("Teatro")) {
                    for (int i = 0; i < teatroInitiativeIDList.size(); i++) {
                        if (teatroInitiativeIDList.get(i).equals(pair.getKey())) {
                            teatroInitiativeIDList.remove(i);
                            break;
                        }
                    }
                } else if (initiative.Tipo.equals("Musica")) {
                    for (int i = 0; i < musicaInitiativeIDList.size(); i++) {
                        if (musicaInitiativeIDList.get(i).equals(pair.getKey())) {
                            musicaInitiativeIDList.remove(i);
                            break;
                        }
                    }
                }
            }
        }
        for (String aux : keys) {
            markerHashMap.remove(aux);
            keywordVisibilityHashmap.remove(aux);
            timeVisibilityHashmap.remove(aux);

        }
        for (String aux : markerIds) {
            initiativeHashMap.remove(aux);
        }
    }

    void updateInitiatives() {
        LatLngBounds curScreen = initiativesMap.getProjection()
                .getVisibleRegion().latLngBounds;
        int south = (int) (curScreen.southwest.latitude * 50);
        int north = (int) (curScreen.northeast.latitude * 50);
        int west = (int) (curScreen.southwest.longitude * 50);
        int east = (int) (curScreen.northeast.longitude * 50);
        int southIterator = south;
        int westIterator;
        boolean sectorloaded;
        int sectorSize = sectors.size();
        Vector<String> sectors2 = new Vector<>();
        Vector<DatabaseReference> sectorsDB2 = new Vector<>();
        while (southIterator <= north) {
            westIterator = west;
            while (westIterator <= east) {
                sectorloaded = false;
                String sector = Integer.toString(southIterator) + "," + Integer.toString(westIterator);
                for (int i = 0; i < sectorSize; i++) {
                    if (sectors.get(i).equals(sector)) {
                        sectorloaded = true;
                        sectors2.add(sector);
                        sectorsDB2.add(sectorsDB.get(i));

                    }

                }
                if (!sectorloaded) {
                    sectors2.add(sector);
                    DatabaseReference sectorDB = FirebaseDatabase.getInstance().getReference("Initiatives/" + sector);
                    sectorDB.addChildEventListener(initiativesListener);
                    sectorsDB2.add(sectorDB);
                }
                westIterator++;
            }
            southIterator++;
        }
        boolean del;
        for (int i = 0; i < sectors.size(); i++) {
            del = true;
            for (int j = 0; j < sectors2.size(); j++) {
                if (sectors2.get(j).equals(sectors.get(i))) {
                    del = false;
                }
            }
            if (del) {
                unloadSector(sectors.get(i));
            }
        }
        sectors.clear();
        sectors = sectors2;
        sectorsDB.clear();
        sectorsDB = sectorsDB2;
    }

    void loadInitiatives() {
        LatLngBounds curScreen = initiativesMap.getProjection()
                .getVisibleRegion().latLngBounds;
        int south = (int) (curScreen.southwest.latitude * 50);
        int north = (int) (curScreen.northeast.latitude * 50);
        int west = (int) (curScreen.southwest.longitude * 50);
        int east = (int) (curScreen.northeast.longitude * 50);
        sectors = new Vector<>();
        sectorsDB = new Vector<>();
        int southIterator = south;
        int westIterator;
        while (southIterator <= north) {
            westIterator = west;
            while (westIterator <= east) {
                String sector = Integer.toString(southIterator) + "," + Integer.toString(westIterator);
                sectors.add(sector);
                DatabaseReference sectorDB = FirebaseDatabase.getInstance().getReference("Initiatives/" + sector);
                sectorDB.addChildEventListener(initiativesListener);
                sectorsDB.add(sectorDB);
                westIterator++;
            }
            southIterator++;
        }
    }

    //Toolbar set
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (back_button_active) {
                onBackPressed();
            } else {
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        } else if (item.getItemId() == R.id.toolbar_filter) {
            OvershootInterpolator interpolator;
            interpolator = new OvershootInterpolator(1);
            if (opened_bottom) {
                vista.animate().setInterpolator(interpolator).translationYBy(vista.getMeasuredHeight()).setDuration(600);
                opened_bottom = false;
                toolbar.setNavigationIcon(R.drawable.ic_bottom_menu);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_bottom_menu);
                upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                back_button_active = false;
            } else {
                vista.animate().setInterpolator(interpolator).translationYBy(-vista.getMeasuredHeight()).setDuration(600);
                opened_bottom = true;
                toolbar.setNavigationIcon(R.drawable.ic_back);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
                upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                back_button_active = true;
            }
        } else if (item.getItemId() == R.id.toolbar_ir) {
            showPath(descriptionFragment);
        } else if (item.getItemId() == R.id.time_filter) {
            if (timeFilterMenu.getVisibility() == View.VISIBLE) {
                timeFilterMenu.setVisibility(View.INVISIBLE);
                timeFilterReset();

            } else {
                timeFilterMenu.setVisibility(View.VISIBLE);
                timeFilter(lastTimeFilterStart,lastTimeFilterEnd);
            }

        } else if(item.getItemId()==R.id.toolbar_help){
            this.startActivity(new Intent(this, TutorialActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.toolbar_ir).setVisible(false);
        search = (SearchView) menu.findItem(R.id.keyword_filter).getActionView();
        //search.setIconifiedByDefault(false);
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                resetFilter();

                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                resetFilter();
                if (query.length() != 0) {
                    filterByKeyword(query);
                }
                return false;
            }


            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
        });

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        opened_bottom = false;
        opened_df = false;
        opened_pf = false;
        on_way = false;
        back_button_active = false;
        setContentView(R.layout.activity_initiatives);
        startService(new Intent(getBaseContext(), MyFirebaseInstanceIDService.class));
        startService(new Intent(getBaseContext(), MyFirebaseMessagingService.class));
        NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // Cancelamos la Notificacion que hemos comenzado
        //nm2.cancel(getIntent().getExtras().getInt("notificationID")); //para rescatar id
        nm2.cancelAll();

        final View iniciativaDeportes = findViewById(R.id.botonDeportes);
        final View iniciativaComida = findViewById(R.id.botonComida);
        final View iniciativaTeatro = findViewById(R.id.botonTeatro);
        final View iniciativaMusica = findViewById(R.id.botonMusica);

        initTimeLabels();
        initiativeHashMap = new HashMap();
        markerHashMap = new HashMap();
        keywordVisibilityHashmap = new HashMap();
        timeVisibilityHashmap = new HashMap();
        swipeMarkerList = new Vector<>();
        comidaInitiativeIDList = new Vector<>();
        teatroInitiativeIDList = new Vector<>();
        deporteInitiativeIDList = new Vector<>();
        musicaInitiativeIDList = new Vector<>();
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_bottom_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        TextView Titulo = (TextView) findViewById(R.id.toolbar_title);
        Titulo.setText("");
        Titulo.setTextSize(0);


        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        //PreviewFragment
        previewFragment = (FrameLayout) findViewById(R.id.previewFragment);
        previewFragment.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!opened_df && !on_way) {
                    opened_df = true;
                    mapFragment.getView().setClickable(false);
                    uiSettings.setAllGesturesEnabled(false);
                    toolbar.getMenu().findItem(R.id.keyword_filter).setVisible(false);
                    toolbar.getMenu().findItem(R.id.time_filter).setVisible(false);
                    DescriptionFragment DF = new DescriptionFragment();
                    DF.setArguments(selectedInitiative);
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.descriptionFragment, DF);
                    OvershootInterpolator interpolator;
                    interpolator = new OvershootInterpolator(1);
                    descriptionFragment.animate().setInterpolator(interpolator).translationYBy(-descriptionFragment.getMeasuredHeight()).setDuration(600);
                    trans.commit();
                    TextView Titulo = (TextView) findViewById(R.id.toolbar_title);
                    Titulo.setText(selectedInitiative.getString("Titulo"));
                    Titulo.setTextSize(25);
                    initiativesMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 15));
                    initiativesMap.animateCamera(CameraUpdateFactory.scrollBy(0, 500));
                }
                return true;
            }
        });
        //DescriptionFragment
        descriptionFragment = (FrameLayout) findViewById(R.id.descriptionFragment);

        //Menu
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        iniciativaComida.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (comidaOn) {
                    iniciativaComida.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightPrimary));
                    comidaOn = false;
                    for (String aux : comidaInitiativeIDList) {
                        ((Marker) markerHashMap.get(aux)).setVisible(false);
                    }
                } else {
                    iniciativaComida.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightSecondary));
                    comidaOn = true;
                    for (String aux : comidaInitiativeIDList) {
                        Marker aux4=(Marker) markerHashMap.get(aux);
                        aux4.setVisible((boolean)keywordVisibilityHashmap.get(aux)&&(boolean)timeVisibilityHashmap.get(aux));
                    }
                }
                return false;
            }
        });

        iniciativaDeportes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (deporteOn) {
                    iniciativaDeportes.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightPrimary));
                    deporteOn = false;
                    for (String aux : deporteInitiativeIDList) {
                        ((Marker) markerHashMap.get(aux)).setVisible(false);
                    }
                } else {
                    iniciativaDeportes.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightSecondary));
                    deporteOn = true;
                    for (String aux : deporteInitiativeIDList) {
                        Marker aux4=(Marker) markerHashMap.get(aux);
                        aux4.setVisible((boolean)keywordVisibilityHashmap.get(aux)&&(boolean)timeVisibilityHashmap.get(aux));
                    }
                }
                return false;
            }
        });

        iniciativaTeatro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (teatroOn) {
                    iniciativaTeatro.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightPrimary));
                    teatroOn = false;
                    for (String aux : teatroInitiativeIDList) {
                        ((Marker) markerHashMap.get(aux)).setVisible(false);
                    }
                } else {
                    iniciativaTeatro.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightSecondary));
                    teatroOn = true;
                    for (String aux : teatroInitiativeIDList) {
                        Marker aux4=(Marker) markerHashMap.get(aux);
                        aux4.setVisible((boolean)keywordVisibilityHashmap.get(aux)&&(boolean)timeVisibilityHashmap.get(aux));
                    }
                }
                return false;
            }
        });

        iniciativaMusica.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (musicaOn) {
                    iniciativaMusica.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightPrimary));
                    musicaOn = false;
                    for (String aux : musicaInitiativeIDList) {
                        ((Marker) markerHashMap.get(aux)).setVisible(false);
                    }
                } else {
                    iniciativaMusica.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.textLightSecondary));
                    musicaOn = true;
                    for (String aux : musicaInitiativeIDList) {
                        Marker aux4=(Marker) markerHashMap.get(aux);
                        aux4.setVisible((boolean)keywordVisibilityHashmap.get(aux) && (boolean)timeVisibilityHashmap.get(aux));
                    }
                }
                return false;
            }
        });


        View view = navigationView.getHeaderView(0);


        //User & mail headers

        txtv_user = (TextView) view.findViewById(R.id.initiates_user);
        txtv_mail = (TextView) view.findViewById(R.id.initiates_mail);
        img_profile = (ImageView) view.findViewById(R.id.initiates_img_profile);
        rtb = (RatingBar) view.findViewById(R.id.inRatingMenu);
        linea = view.findViewById(R.id.linea);


        userInterests = new Interests(false, false, false, false, false, true, false);
        vista = findViewById(R.id.bottom_menu);

    }
    public void initTimeLabels(){
        timeLabels=new Vector<>();
        timeLabels.add((TextView) findViewById(R.id.time1));
        timeLabels.add((TextView) findViewById(R.id.time2));
        timeLabels.add((TextView) findViewById(R.id.time3));
        timeLabels.add((TextView) findViewById(R.id.time4));
        timeLabels.add((TextView) findViewById(R.id.time5));
        timeLabels.add((TextView) findViewById(R.id.time6));
        timeLabels.add((TextView) findViewById(R.id.time7));
        timeLabels.add((TextView) findViewById(R.id.time8));
        timeLabels.add((TextView) findViewById(R.id.time9));
        timeLabels.add((TextView) findViewById(R.id.time10));
        timeLabels.add((TextView) findViewById(R.id.time11));
        timeLabels.add((TextView) findViewById(R.id.time12));
        timeLabels.add((TextView) findViewById(R.id.time13));
    }
    public void setTimeLabels(int start){
        int counter=0;
        String text;
        for(TextView timeLabel:timeLabels){
            text=Integer.toString((start+counter)%24)+":00";
            timeLabel.setText(text);
            counter+=2;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //User Auth Listener
        //Read initiatives listener
        Calendar rightNow = Calendar.getInstance();
        currentTime = rightNow.getTimeInMillis();
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        SimpleDateFormat mFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Time = cal.getTimeInMillis();
        initialTime=Time;
        currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        setTimeLabels(currentHour);
        rangeview=(SimpleRangeView) findViewById(rangeView);
        timeFilterMenu = (LinearLayout) findViewById(time_filter_menu);
        lastTimeFilterStart=Time+(currentHour)*60*60*1000;
        lastTimeFilterEnd=Time+(currentHour+(6))*60*60*1000;
        timelineFormatter=new SimpleDateFormat("dd MMM");
        fromDate=(TextView) findViewById(R.id.fromDate);
        toDate=(TextView) findViewById(R.id.toDate);
        fromDate.setText(timelineFormatter.format(Time));
        toDate.setText(timelineFormatter.format(Time+24*60*60*1000));

        /*rangeview.setOnRangeLabelsListener(new SimpleRangeView.OnRangeLabelsListener() {

            @Override
            public String getLabelTextForPosition(@NotNull SimpleRangeView rangeView, int pos, @NotNull SimpleRangeView.State state) {
                if(pos%2==1){
                    return "";
                }
                return String.valueOf((currentHour+(pos))%24);
            }
        });*/
        rangeview.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
            @Override
            public void onRangeChanged(@NotNull SimpleRangeView rangeView, int start, int end) {
                timeFilterReset();
                timeFilter((Time+(currentHour+(start))*60*60*1000),(Time+(currentHour+(end))*60*60*1000));
            }
        });
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }

    public void timeFilter(long start,long end){
        timeFilterReset();
        lastTimeFilterStart=start;
        lastTimeFilterEnd=end;
        Iterator<Map.Entry<String, Marker>> it = markerHashMap.entrySet().iterator();
        Initiative initiative;
        while (it.hasNext()) {
            Map.Entry<String, Marker> pair = it.next();
            initiative = (Initiative) initiativeHashMap.get(pair.getValue().getId());
            if(initiative.fechaFin<start || initiative.fechaInicio>end){
                timeVisibilityHashmap.put(pair.getKey(),false);
                pair.getValue().setVisible(false);
            }

        }
    }
    public void timeFilterReset(){
        Iterator<Map.Entry<String, Marker>> it = markerHashMap.entrySet().iterator();
        Initiative initiative;
        while (it.hasNext()) {
            Map.Entry<String, Marker> pair = it.next();
            initiative = (Initiative) initiativeHashMap.get(pair.getValue().getId());
            if(!(boolean)timeVisibilityHashmap.get(pair.getKey())){
                if(initiative.Tipo.equals("Comida")){
                    pair.getValue().setVisible(comidaOn && (boolean)keywordVisibilityHashmap.get(pair.getKey()));
                }
                else if(initiative.Tipo.equals("Musica")){
                    pair.getValue().setVisible(musicaOn && (boolean)keywordVisibilityHashmap.get(pair.getKey()));
                }
                else if(initiative.Tipo.equals("Deporte")){
                    pair.getValue().setVisible(deporteOn && (boolean)keywordVisibilityHashmap.get(pair.getKey()));
                }
                else if(initiative.Tipo.equals("Teatro")){
                    pair.getValue().setVisible(teatroOn && (boolean)keywordVisibilityHashmap.get(pair.getKey()));
                }
                timeVisibilityHashmap.put(pair.getKey(),true);
            }


        }
    }

    public void prevDay(View view){
        if(Time!=initialTime){
            Time-=24*60*60*1000;
            fromDate.setText(timelineFormatter.format(Time));
            toDate.setText(timelineFormatter.format(Time+24*60*60*1000));
            timeFilter(Time+(currentHour+(rangeview.getStart()))*60*60*1000,Time+(currentHour+(rangeview.getEnd()))*60*60*1000);
        }
    }
    public void nextDay(View view){
        Time+=24*60*60*1000;
        fromDate.setText(timelineFormatter.format(Time));
        toDate.setText(timelineFormatter.format(Time+24*60*60*1000));
        timeFilter(Time+(currentHour+(rangeview.getStart()))*60*60*1000,Time+(currentHour+(rangeview.getEnd()))*60*60*1000);
    }
    public void actualDay(View view){
        Time=initialTime;
        fromDate.setText(timelineFormatter.format(Time));
        toDate.setText(timelineFormatter.format(Time+24*60*60*1000));
        timeFilter(Time+(currentHour+(rangeview.getStart()))*60*60*1000,Time+(currentHour+(rangeview.getEnd()))*60*60*1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        //User Auth Listener
        if (authListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Se necesita permiso de ubicación")
                        .setMessage("Esta aplicación necesita el permiso de ubicación para funcionar correctamente")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(InitiativesActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //initiativesMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void centerMap(View view){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .zoom(15)
                .bearing(0)
                .build();
        initiativesMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker == null) {

            mCurrLocationMarker = initiativesMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_circle))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(location.getLatitude(), location
                                    .getLongitude())));
        }
        mCurrLocationMarker.setZIndex(5);
        animateMarker(mCurrLocationMarker, location);

    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initiativesMap = googleMap;
        start = new LocationGPS(getApplicationContext());
        final LatLng interested;
        initiativesMap.setMinZoomPreference(13);
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            //initiativesMap.setMyLocationEnabled(true);
        }

        interested = new LatLng(start.getLatitud(), start.getLongitud());
        initiativesMap.animateCamera(CameraUpdateFactory.newLatLngZoom(interested, 15));
        uiSettings = initiativesMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        initiativesMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Agrego datos del pin
                if(marker.getId().equals(mCurrLocationMarker.getId()) || opened_df){
                    return false;
                }
                OvershootInterpolator interpolator;
                interpolator = new OvershootInterpolator(1);
                selectedMarker = marker;
                selectedInitiative = new Bundle();
                Initiative initiative = (Initiative) initiativeHashMap.get(marker.getId());
                selectedInitiative.putString("Titulo", initiative.Titulo);
                selectedInitiative.putString("imagen", initiative.image);
                selectedInitiative.putString("Descripcion", initiative.Descripcion);
                selectedInitiative.putString("Nombre", initiative.Nombre);
                selectedInitiative.putString("Direccion", initiative.Direccion);
                selectedInitiative.putString("Estado", Integer.toString(initiative.Estado));
                DateFormat formatter = new SimpleDateFormat("E, d MMM • HH:mm");
                DateFormat formatter1 = new SimpleDateFormat("E, d MMM • HH:mm");
                selectedInitiative.putString("hInicio", formatter.format(new Date(initiative.fechaInicio)));
                selectedInitiative.putString("hFin", formatter1.format(new Date(initiative.fechaFin)));
                selectedInitiative.putString("Uid", initiative.Uid);
                selectedInitiative.putString("Latitud", initiative.Latitud.toString());
                selectedInitiative.putString("Longitud", initiative.Longitud.toString());
                selectedInitiative.putString("ID", initiative.Longitud.toString());
                selectedInitiative.putString("searchViewText", search.getQuery().toString());
                selectedInitiative.putBoolean("searchViewOpened", !search.isIconified());


                //le paso los datos al fragment
                PreviewFragment DF = new PreviewFragment();
                DF.setArguments(selectedInitiative);
                lastMarkerPosition = marker.getPosition();
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                toolbar.setNavigationIcon(R.drawable.ic_back);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
                back_button_active = true;
                upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                trans.replace(R.id.previewFragment, DF);
                if (opened_bottom) {
                    vista.animate().setInterpolator(interpolator).translationYBy(vista.getMeasuredHeight()).setDuration(600);
                    opened_bottom = false;
                }
                if ((previewFragment.getTranslationY() >= previewFragment.getHeight()) && !on_way) {
                    previewFragment.animate().setInterpolator(interpolator).translationYBy(-previewFragment.getMeasuredHeight()).setDuration(600);
                    opened_pf = true;
                }
                trans.commit();
                return false;
            }
        });
        initiativesMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                loadInitiatives();
                initiativesMap.setOnCameraIdleListener(cameraIdleListener);
                lastCameraZoom=initiativesMap.getCameraPosition().zoom;
            }
        });
    }

    public void showPath(View view) {
        if (polylineActive && initiativePath != null) {
            initiativePath.remove();
            polylineActive = false;
        }
        OvershootInterpolator interpolator;
        interpolator = new OvershootInterpolator(1);
        descriptionFragment.animate().setInterpolator(interpolator).translationY(descriptionFragment.getMeasuredHeight()).setDuration(600);
        opened_df = false;
        toolbar.getMenu().findItem(R.id.toolbar_ir).setVisible(false);
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(new LatLng(start.getLatitud(), start.getLongitud()))
                .to(lastMarkerPosition)
                .transportMode(TransportMode.WALKING)
                .language(Language.SPANISH)
                .unit(Unit.METRIC)

                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            uiSettings.setAllGesturesEnabled(true);
                            uiSettings.setMyLocationButtonEnabled(true);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(selectedMarker.getPosition());
                            builder.include(new LatLng(start.getLatitud(), start.getLongitud()));
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            LatLngBounds bounds = builder.build();
                            int padding = 30; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            initiativesMap.animateCamera(cu);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 3, R.color.Primary);
                            initiativePath = initiativesMap.addPolyline(polylineOptions);
                            polylineActive = true;
                            on_way = true;
                            TextView Titulo = (TextView) findViewById(R.id.toolbar_title);
                            Titulo.setText("Camino a: " + selectedInitiative.getString("Titulo"));
                            Titulo.setTextSize(25);
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
        OvershootInterpolator interpolator;
        interpolator = new OvershootInterpolator(1);
        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        int maxY = mdispSize.y;
        float currentPosition;
        int fragment_pos[] = new int[2];
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (polylineActive && initiativePath != null) {
            initiativePath.remove();
            polylineActive = false;
            on_way = false;
            TextView Titulo = (TextView) findViewById(R.id.toolbar_title);
            Titulo.setText("");
            Titulo.setTextSize(0);
            toolbar.getMenu().findItem(R.id.toolbar_ir).setVisible(true);
            initiativesMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 15));
        } else if (opened_df) {//CERRAR DESCRIPTION FRAGMENT
            toolbar.getMenu().findItem(R.id.keyword_filter).setVisible(true);
            toolbar.getMenu().findItem(R.id.time_filter).setVisible(true);
            View df = findViewById(R.id.descriptionFragment);
            df.getLocationOnScreen(fragment_pos);
            if ((df.getHeight() + fragment_pos[1]) == maxY){
                descriptionFragment.animate().setInterpolator(interpolator).translationYBy(descriptionFragment.getMeasuredHeight()).setDuration(600);
                opened_df = false;
                initiativesMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 15));
                TextView Titulo = (TextView) findViewById(R.id.toolbar_title);
                Titulo.setText("");
                Titulo.setTextSize(0);
                uiSettings.setAllGesturesEnabled(true);
                uiSettings.setMyLocationButtonEnabled(true);
                mapFragment.getView().setClickable(true);
            }
        } else if (opened_pf) {
            View pf = findViewById(R.id.previewFragment);
            pf.getLocationOnScreen(fragment_pos);
            if ((pf.getHeight() + fragment_pos[1]) == maxY){
                previewFragment.animate().setInterpolator(interpolator).translationYBy(previewFragment.getMeasuredHeight()).setDuration(600);
                opened_pf = false;
                toolbar.getMenu().findItem(R.id.toolbar_filter).setVisible(true);
                toolbar.getMenu().findItem(R.id.toolbar_ir).setVisible(false);
                toolbar.setNavigationIcon(R.drawable.ic_bottom_menu);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_bottom_menu);
                upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                back_button_active = false;
            }
        } else if (opened_bottom) {
            View ob = findViewById(R.id.bottom_menu);
            ob.getLocationOnScreen(fragment_pos);
            if ((ob.getHeight() + fragment_pos[1]) == maxY){
                vista.animate().setInterpolator(interpolator).translationYBy(vista.getMeasuredHeight()).setDuration(600);
                opened_bottom = false;
                toolbar.setNavigationIcon(R.drawable.ic_bottom_menu);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_bottom_menu);
                upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                back_button_active = false;
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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
        //getApplicationContext().unregisterReceiver(receiver);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.initiates_search:
            //    break;
            case R.id.initiates_login:
                Intent login = new Intent();
                login.setClassName("com.byobdev.kamal", "com.byobdev.kamal.LoginActivity");
                startActivityForResult(login, 0);
                break;
            case R.id.initiates_initiative:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intentMain3 = new Intent(this, LoginActivity.class);
                    this.startActivity(intentMain3);
                    break;
                } else {
                    Intent intentMain2 = new Intent(this, CreateInitiativeActivity.class);
                    this.startActivity(intentMain2);
                    break;
                }
            case R.id.initiates_manage:
                //Mostrar Intereses
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intentMain3 = new Intent(this, LoginActivity.class);
                    this.startActivity(intentMain3);
                    break;
                } else {
                    Intent intentMain2 = new Intent(this, SetInterestsActivity.class);
                    intentMain2.putExtra("userInterests", userInterests);
                    this.startActivity(intentMain2);
                    break;
                }
            case R.id.initiates_settings:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intentMain3 = new Intent(this, LoginActivity.class);
                    this.startActivity(intentMain3);
                    break;
                } else {
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
        //  getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
