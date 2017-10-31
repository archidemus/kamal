package com.byobdev.kamal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;
import com.byobdev.kamal.DBClasses.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static android.R.attr.rating;
import static android.R.attr.x;
import static com.facebook.FacebookSdk.getApplicationContext;

public class DescriptionFragment extends Fragment implements ListCommentFragmentActivity.customButtonListener {


    TextView date, creator, title;
    RatingBar rtb2;
    Location loc1, loc2;
    TextView Nombre;
    TextView Descripcion;
    ImageView Image;
    ImageView OrgImage;
    TextView Lugar;
    TextView hInicio;
    TextView hFin;
    String image;
    String orgImage;
    String Uid;
    String Latitud;
    String Longitud;
    EditText Comentario;
    TextView Calificar;
    ListView lista;
    RatingBar rtb;
    String Key;
    String estado;
    ImageButton sendCom;
    Button verComent;
    String[] completarLista;
    String[] keyLista;
    String[] descriptionLista;
    String[] imageLista;
    String[] respuesaLista;
    String[] SectorLista;
    FirebaseAuth firebaseAuth;
    String[] completarListaAux;
    String[] SectorListaAux;
    String[] keyListaAux;
    String[] descriptionListaAux;
    String[] imageListaAux;
    String[] respuesaListaAux;
    int ORG=0;

    boolean rated=false;
    float rating=0f;
    int position;
    int prevPosition=-1;
    boolean selected=false, organizador = false;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            currentUser = firebaseAuth.getCurrentUser();

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);
        ImageView i = (ImageView) rootView.findViewById(R.id.inImage);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        //Titulo = (TextView) getView().findViewById(R.id.inTitle);
        //Titulo.setText(getArguments().getString("Titulo"));
        OrgImage = (ImageView) getView().findViewById(R.id.profile_image);
        Nombre = (TextView) getView().findViewById(R.id.inOrganizer);
        Nombre.setText(getArguments().getString("Nombre"));
        Descripcion = (TextView) getView().findViewById(R.id.inShortDesc);
        Descripcion.setText(getArguments().getString("Descripcion"));
        Lugar = (TextView) getView().findViewById(R.id.inPlace);
        Lugar.setText(getArguments().getString("Direccion"));
        hInicio = (TextView) getView().findViewById(R.id.hI);
        hInicio.setText(getArguments().getString("hInicio"));
        hFin = (TextView) getView().findViewById(R.id.hT);
        hFin.setText(getArguments().getString("hFin"));
        lista = (ListView) getView().findViewById(R.id.comment_list);
        sendCom = (ImageButton) getView().findViewById(R.id.SendComment);
        sendCom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendComment();
            }
        });
        verComent = (Button) getView().findViewById(R.id.VerComentario);
        verComent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                VerComentarios();
            }
        });
        Comentario = (EditText) getView().findViewById(R.id.CommentInput);
        Image = (ImageView) getView().findViewById(R.id.inImage);
        image = getArguments().getString("imagen");
        rtb = (RatingBar) getView().findViewById(R.id.inRating);
        Calificar = (TextView) getView().findViewById(R.id.btn_Rating);
        Calificar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ratingSend();
            }
        });
        estado = getArguments().getString("Estado");

        Latitud = getArguments().getString("Latitud");
        Longitud = getArguments().getString("Longitud");
        Uid = getArguments().getString("Uid");

        loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(Latitud));
        loc1.setLongitude(Double.parseDouble(Longitud));
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        loc2 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        final double distanceInMeters = loc1.distanceTo(loc2);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // for (DataSnapshot child : snapshot.getChildren())
                // Create a LinearLayout element
                rtb.setRating(Float.parseFloat(snapshot.child("rating").getValue().toString()));

                orgImage = snapshot.child("ImageURL").getValue().toString();
                if (orgImage.equals("")){
                    if(OrgImage.getVisibility() == View.VISIBLE){
                        OrgImage.setVisibility(View.GONE);
                    }
                }else{
                    if(OrgImage.getVisibility() == View.GONE){
                        OrgImage.setVisibility(View.VISIBLE);
                    }
                    Picasso.with(DescriptionFragment.this.getContext())
                            .load(orgImage)
                            .error(R.drawable.kamal_not_found)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(OrgImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });
        rtb.setIsIndicator(true);

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    if (Uid.equals(currentUser.getUid())) {
                        Calificar.setVisibility(View.INVISIBLE);
                    }
                    else  if(estado.equals("0")){
                        Calificar.setVisibility(View.INVISIBLE);
                    }
                    else if(distanceInMeters > 500){
                        Calificar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Calificar.setVisibility(View.INVISIBLE);
                }
            }
        };



        FirebaseAuth.getInstance().addAuthStateListener(authListener);

        /*DatabaseReference opData = FirebaseDatabase.getInstance().getReference("Initiatives").child(getArguments().getString("Estado"));
        if(!opData.toString().equals("0")){
            Calificar.setVisibility(View.INVISIBLE);
        }*/

        mDatabase = FirebaseDatabase.getInstance().getReference("Comments").child(getArguments().getString("imagen"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int t=0;
                int aux=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;

                }
                if(t>=3){
                    aux =3;
                    SectorListaAux = new String[3];
                    completarListaAux = new String[3];
                    keyListaAux = new String[3];
                    descriptionListaAux = new String[3];
                    imageListaAux = new String[3];
                    respuesaListaAux = new String[3];
                }else if(t==2){
                    aux =2;
                    SectorListaAux = new String[2];
                    completarListaAux = new String[2];
                    keyListaAux = new String[2];
                    descriptionListaAux = new String[2];
                    imageListaAux = new String[2];
                    respuesaListaAux = new String[2];
                }else if(t==1){
                    aux =1;
                    SectorListaAux = new String[1];
                    completarListaAux = new String[1];
                    keyListaAux = new String[1];
                    descriptionListaAux = new String[1];
                    imageListaAux = new String[1];
                    respuesaListaAux = new String[1];
                }
                SectorLista = new String[t-1];
                completarLista = new String[t-1];
                keyLista = new String[t-1];
                descriptionLista = new String[t-1];
                imageLista = new String[t-1];
                respuesaLista = new String[t-1];
                int r = t;
                t=0;
                if(r > 0){
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        // Create a LinearLayout element
                        if(t==r){
                            break;
                        }
                        if(child.child("Comentario").getValue().toString().equals("Creador")){
                            if(currentUser != null){
                                if(currentUser.getDisplayName().equals(child.child("Nombre").getValue().toString())){
                                    organizador = true;
                                    ORG =1;
                                }
                            }
                        }else{
                            SectorLista[t] = child.getKey();
                            respuesaLista[t] = child.child("Respuesta").getValue().toString();
                            completarLista[t] = child.child("Nombre").getValue().toString();
                            keyLista[t] = child.getKey().toString();
                            descriptionLista[t] = child.child("Comentario").getValue().toString();
                            imageLista[t] = child.child("Image").getValue().toString();
                            t++;

                        }



                    }
                    for(int i=0;i<aux;i++){
                        if(t==0){
                            break;
                        }
                        SectorListaAux[i] = SectorLista[t-1];
                        completarListaAux[i] = completarLista[t-1];
                        keyListaAux[i] = keyLista[t-1];
                        descriptionListaAux[i] = descriptionLista[t-1];
                        imageListaAux[i] = imageLista[t-1];
                        respuesaListaAux[i] =respuesaLista[t-1];
                        t--;
                    }

                    if(currentUser == null){
                        sendCom.setVisibility(View.GONE);
                        Comentario.setVisibility(View.GONE);
                    }
                    else{
                        sendCom.setVisibility(View.VISIBLE);
                        Comentario.setVisibility(View.VISIBLE);
                    }
                    if(r==1){

                    }
                    else if(r== 2){
                        lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
                    }
                    else if(r== 3){
                        lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
                    }else{
                        lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics());
                    }
                    com.byobdev.kamal.ListCommentFragmentActivity adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarListaAux);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListCommentFragmentActivity(getActivity(), dataItems,keyListaAux,descriptionListaAux,imageListaAux,lista, respuesaListaAux, ORG);
                    adapter.setCustomButtonListner(DescriptionFragment.this);
                    lista.setAdapter(adapter);
                }
                if(r<=4){
                    verComent.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

        if (image.equals("")){
            if(Image.getVisibility() == View.VISIBLE){
                Image.setVisibility(View.GONE);
            }
        }else{
            if(Image.getVisibility() == View.GONE){
                Image.setVisibility(View.VISIBLE);
            }
            String url = "https://firebasestorage.googleapis.com/v0/b/prime-boulevard-168121.appspot.com/o/Images%2F"+getArguments().getString("imagen")+"?alt=media";
            Picasso.with(this.getActivity())
                    .load(url)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.kamal_not_found)
                    .into(Image);
        }
    }

    public void SendComment(){

        mDatabase = FirebaseDatabase.getInstance().getReference("Comments/");
        Key=mDatabase.push().getKey();

        DatabaseReference comments = FirebaseDatabase.getInstance().getReference("Comments/");
        Comment comment = new Comment(currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), Comentario.getText().toString(), "");
        comments.child(getArguments().getString("imagen")).child(Key).setValue(comment);
        Comentario.setText("");
        Toast.makeText(getApplicationContext(), "Comentario enviado", Toast.LENGTH_LONG).show();
        mDatabase = FirebaseDatabase.getInstance().getReference("Comments").child(getArguments().getString("imagen"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int t=0;
                int aux=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;

                }
                if(t>=3){
                    aux =3;
                    SectorListaAux = new String[3];
                    completarListaAux = new String[3];
                    keyListaAux = new String[3];
                    descriptionListaAux = new String[3];
                    imageListaAux = new String[3];
                    respuesaListaAux = new String[3];
                }else if(t==2){
                    aux =2;
                    SectorListaAux = new String[2];
                    completarListaAux = new String[2];
                    keyListaAux = new String[2];
                    descriptionListaAux = new String[2];
                    imageListaAux = new String[2];
                    respuesaListaAux = new String[2];
                }else if(t==1){
                    aux =1;
                    SectorListaAux = new String[1];
                    completarListaAux = new String[1];
                    keyListaAux = new String[1];
                    descriptionListaAux = new String[1];
                    imageListaAux = new String[1];
                    respuesaListaAux = new String[1];
                }
                SectorLista = new String[t-1];
                completarLista = new String[t-1];
                keyLista = new String[t-1];
                descriptionLista = new String[t-1];
                imageLista = new String[t-1];
                respuesaLista = new String[t-1];
                int r = t;
                t=0;
                if(r > 0){
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        // Create a LinearLayout element
                        if(child.child("Comentario").getValue().toString().equals("Creador")){

                        }else{
                            SectorLista[t] = child.getKey();
                            respuesaLista[t] = child.child("Respuesta").getValue().toString();
                            completarLista[t] = child.child("Nombre").getValue().toString();
                            keyLista[t] = child.getKey().toString();
                            descriptionLista[t] = child.child("Comentario").getValue().toString();
                            imageLista[t] = child.child("Image").getValue().toString();
                            t++;

                        }



                    }
                    if(currentUser == null){
                        sendCom.setVisibility(View.GONE);
                        Comentario.setVisibility(View.GONE);
                    }
                    else{
                        sendCom.setVisibility(View.VISIBLE);
                        Comentario.setVisibility(View.VISIBLE);
                    }
                    for(int i=0;i<aux;i++){
                        if(t==0){
                            break;
                        }
                        SectorListaAux[i] = SectorLista[t-1];
                        completarListaAux[i] = completarLista[t-1];
                        keyListaAux[i] = keyLista[t-1];
                        descriptionListaAux[i] = descriptionLista[t-1];
                        imageListaAux[i] = imageLista[t-1];
                        respuesaListaAux[i] =respuesaLista[t-1];
                        t--;
                    }
                    if(r==1){

                    }
                    else if(r== 2){
                        lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
                    }
                    else if(r== 3){
                        lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
                    }else{
                        lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics());
                    }
                    com.byobdev.kamal.ListCommentFragmentActivity adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarListaAux);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListCommentFragmentActivity(getActivity(), dataItems,keyListaAux,descriptionListaAux,imageListaAux,lista, respuesaListaAux, ORG);
                    adapter.setCustomButtonListner(DescriptionFragment.this);
                    lista.setAdapter(adapter);
                }
                if(r>4){
                    verComent.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });


    }

    public void VerComentarios(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
        final Intent intentMain2 = new Intent(getApplicationContext(), VerCommentsActivity.class);
       // mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives").child(Sector).child(nombre);
        intentMain2.putExtra("IDIniciativa",getArguments().getString("imagen"));
        intentMain2.putExtra("Titulo",getArguments().getString("Titulo"));
        startActivity(intentMain2);
    }

    public void ratingSend(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialog = inflater.inflate(R.layout.rating_dialog, null);
        builder.setView(dialog);
        builder.setCancelable(true); //No idea if this works

        ImageView imagen2 =(ImageView) dialog.findViewById(R.id.img_rate);
        rtb2 = (RatingBar) dialog.findViewById(R.id.ratingBar);
       // btnRate = (Button) dialog.findViewById(R.id.btn_Rate);
        date = (TextView) dialog.findViewById(R.id.rate_date);
        title = (TextView) dialog.findViewById(R.id.rate_title);
        creator = (TextView) dialog.findViewById(R.id.rate_creator);

        creator.setText("Por ".concat(Nombre.getText().toString()));
        title.setText(getArguments().getString("Titulo"));
        date.setText(hInicio.getText().toString().concat("\n").concat(hFin.getText().toString()));
        String imagen = getArguments().getString("imagen");

        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Rating/"+ getArguments().getString("imagen")+"/"+currentUser.getUid());
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    rating=Float.parseFloat(dataSnapshot.getValue().toString());
                    rtb2.setRating(rating);
                    rated=true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Calificar", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                rateThis();
            }
        });

        if (imagen.equals("")){
            if(imagen2.getVisibility() == View.VISIBLE){
                imagen2.setVisibility(View.GONE);
            }
        }else{
            if(imagen2.getVisibility() == View.GONE){
                imagen2.setVisibility(View.VISIBLE);
            }
            String url = "https://firebasestorage.googleapis.com/v0/b/prime-boulevard-168121.appspot.com/o/Images%2F"+getArguments().getString("imagen")+"?alt=media";
            Picasso.with(this.getActivity())
                    .load(url)
                    .error(R.drawable.kamal_logo)
                    .into(imagen2);
        }


        /*btnRate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                rateThis();

            }
        });*/
        AlertDialog dialogo = builder.create();
        dialogo.show();
        Button b = dialogo.getButton(DialogInterface.BUTTON_NEGATIVE);
        b.setTextColor(getResources().getColor(R.color.Primary));
        Button b1 = dialogo.getButton(DialogInterface.BUTTON_POSITIVE);
        b1.setTextColor(getResources().getColor(R.color.Primary));



    }
    public void rateThis(){

            final DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    // for (DataSnapshot child : snapshot.getChildren())
                    // Create a LinearLayout element
                    if(rated){
                        final float rait = rtb2.getRating();
                        int nVotos = Integer.parseInt(snapshot.child("Nvotos").getValue().toString());
                        userInitiatives.child("rating").setValue(((Float.parseFloat(snapshot.child("rating").getValue().toString())*nVotos)-rating+rait)/nVotos);
                        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Rating/"+ getArguments().getString("imagen")+"/"+currentUser.getUid());
                        mDatabase2.setValue(rait);
                    }
                    else{
                        final float rait = rtb2.getRating();
                        int nVotos = Integer.parseInt(snapshot.child("Nvotos").getValue().toString());
                        int nVotos2 = nVotos+1;
                        userInitiatives.child("rating").setValue(((Float.parseFloat(snapshot.child("rating").getValue().toString())*nVotos)+rait)/nVotos2);
                        userInitiatives.child("Nvotos").setValue(nVotos2);
                        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Rating/"+ getArguments().getString("imagen")+"/"+currentUser.getUid());
                        mDatabase2.setValue(rait);
                    }

                    Toast.makeText(getActivity(),"Calificación realizada",Toast.LENGTH_LONG).show();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }

            });


    }
    @Override
    public void setRespuesta(int position) {
        Responder1(position);
    }

    public void Responder1(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        alert.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        alert.setTitle("Respuesta");
        alert.setMessage("Escribe una respuesta a la consulta");
        alert.setPositiveButton("Aceptar", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference Respuesta = FirebaseDatabase.getInstance().getReference("Comments/"+getArguments().getString("imagen")+"/"+SectorListaAux[position]);
                Respuesta.child("Respuesta").setValue(edt.getText().toString());
                dialog.dismiss();
                mDatabase = FirebaseDatabase.getInstance().getReference("Comments").child(getArguments().getString("imagen"));
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int t=0;
                        int aux=0;
                        for (final DataSnapshot child : snapshot.getChildren()) {
                            // Create a LinearLayout element
                            t++;

                        }
                        if(t>=3){
                            aux =3;
                            SectorListaAux = new String[3];
                            completarListaAux = new String[3];
                            keyListaAux = new String[3];
                            descriptionListaAux = new String[3];
                            imageListaAux = new String[3];
                            respuesaListaAux = new String[3];
                        }else if(t==2){
                            aux =2;
                            SectorListaAux = new String[2];
                            completarListaAux = new String[2];
                            keyListaAux = new String[2];
                            descriptionListaAux = new String[2];
                            imageListaAux = new String[2];
                            respuesaListaAux = new String[2];
                        }else if(t==1){
                            aux =1;
                            SectorListaAux = new String[1];
                            completarListaAux = new String[1];
                            keyListaAux = new String[1];
                            descriptionListaAux = new String[1];
                            imageListaAux = new String[1];
                            respuesaListaAux = new String[1];
                        }
                        SectorLista = new String[t-1];
                        completarLista = new String[t-1];
                        keyLista = new String[t-1];
                        descriptionLista = new String[t-1];
                        imageLista = new String[t-1];
                        respuesaLista = new String[t-1];
                        int r = t;
                        t=0;
                        if(r > 0){
                            for (final DataSnapshot child : snapshot.getChildren()) {
                                // Create a LinearLayout element
                                if(child.child("Comentario").getValue().toString().equals("Creador")){

                                }else{
                                    SectorLista[t] = child.getKey();
                                    respuesaLista[t] = child.child("Respuesta").getValue().toString();
                                    completarLista[t] = child.child("Nombre").getValue().toString();
                                    keyLista[t] = child.getKey().toString();
                                    descriptionLista[t] = child.child("Comentario").getValue().toString();
                                    imageLista[t] = child.child("Image").getValue().toString();
                                    t++;

                                }



                            }
                            if(currentUser == null){
                                sendCom.setVisibility(View.GONE);
                                Comentario.setVisibility(View.GONE);
                            }
                            else{
                                sendCom.setVisibility(View.VISIBLE);
                                Comentario.setVisibility(View.VISIBLE);
                            }
                            for(int i=0;i<aux;i++){
                                if(t==0){
                                    break;
                                }
                                SectorListaAux[i] = SectorLista[t-1];
                                completarListaAux[i] = completarLista[t-1];
                                keyListaAux[i] = keyLista[t-1];
                                descriptionListaAux[i] = descriptionLista[t-1];
                                imageListaAux[i] = imageLista[t-1];
                                respuesaListaAux[i] =respuesaLista[t-1];
                                t--;
                            }
                            if(r==1){

                            }
                            else if(r== 2){
                                lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
                            }
                            else if(r== 3){
                                lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
                            }else{
                                lista.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics());
                            }
                            com.byobdev.kamal.ListCommentFragmentActivity adapter;
                            ArrayList<String> dataItems = new ArrayList<String>();
                            List<String> dataTemp = Arrays.asList(completarListaAux);
                            dataItems.addAll(dataTemp);
                            adapter = new com.byobdev.kamal.ListCommentFragmentActivity(getActivity(), dataItems,keyListaAux,descriptionListaAux,imageListaAux,lista, respuesaListaAux, ORG);
                            adapter.setCustomButtonListner(DescriptionFragment.this);
                            lista.setAdapter(adapter);
                        }
                        if(r>4){
                            verComent.setVisibility(View.VISIBLE);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }

                });

            }
        });
        alert.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();
    }


}
