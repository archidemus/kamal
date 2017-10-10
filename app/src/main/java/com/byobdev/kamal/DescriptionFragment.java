package com.byobdev.kamal;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.byobdev.kamal.DBClasses.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.String.valueOf;

public class DescriptionFragment extends Fragment {

    TextView Titulo;
    Button btnRate;
    TextView date, creator, title;
    RatingBar rtb2;

    TextView Nombre;
    TextView Descripcion;
    ImageView Image;
    TextView Lugar;
    TextView hInicio;
    TextView hFin;
    String image;
    EditText Comentario;
    Button Calificar;
    ListView lista;
    RatingBar rtb;
    String Key;
    ImageButton sendCom;
    Button verComent;
    String[] completarLista;
    String[] SectorLista;
    String[] keyLista;
    String[] descriptionLista;
    String[] imageLista;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    FirebaseAuth.AuthStateListener authListener  = new FirebaseAuth.AuthStateListener(){
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            currentUser  = firebaseAuth.getCurrentUser();

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);
        ImageView i = (ImageView)rootView.findViewById(R.id.inImage);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        //Titulo = (TextView) getView().findViewById(R.id.inTitle);
        //Titulo.setText(getArguments().getString("Titulo"));
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
        sendCom.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                SendComment();
            }
        });
        verComent = (Button) getView().findViewById(R.id.VerComentario);
        verComent.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                VerComentarios();
            }
        });
        Comentario = (EditText) getView().findViewById(R.id.CommentInput);
        Image = (ImageView) getView().findViewById(R.id.inImage);
        image = getArguments().getString("imagen");
        rtb = (RatingBar) getView().findViewById(R.id.inRating);
        Calificar = (Button) getView().findViewById(R.id.btn_Rating);
        Calificar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ratingSend();
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // for (DataSnapshot child : snapshot.getChildren())
                // Create a LinearLayout element
                rtb.setRating(Float.parseFloat(snapshot.child("rating").getValue().toString()));

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
                    if (mDatabase.toString().equals(currentUser.getUid())) {
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
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;

                }
                if(t>=3){
                    completarLista = new String[3];
                    keyLista = new String[3];
                    SectorLista = new String[3];
                    descriptionLista = new String[3];
                    imageLista = new String[3];
                }else if(t==2){
                    completarLista = new String[2];
                    keyLista = new String[2];
                    SectorLista = new String[2];
                    descriptionLista = new String[2];
                    imageLista = new String[2];

                }else if(t==1){
                    completarLista = new String[1];
                    keyLista = new String[1];
                    SectorLista = new String[1];
                    descriptionLista = new String[1];
                    imageLista = new String[1];
                }
                int r = t;
                t=0;
                if(r > 0){
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        // Create a LinearLayout element
                        if(child.child("Comentario").getValue().toString().equals("Creador")){

                        }else{
                            completarLista[t] = child.child("Nombre").getValue().toString();
                            keyLista[t] = child.getKey().toString();
                            descriptionLista[t] = child.child("Comentario").getValue().toString();
                            imageLista[t] = child.child("Image").getValue().toString();
                            t++;
                            if(t==r){
                                break;
                            }
                        }



                    }
                    com.byobdev.kamal.ListCommentFragmentActivity adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarLista);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListCommentFragmentActivity(getActivity(), dataItems,keyLista,descriptionLista,imageLista,lista);
                    lista.setAdapter(adapter);
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
                    .error(R.drawable.kamal_logo)
                    .into(Image);
        }
    }

    public void SendComment(){

        mDatabase = FirebaseDatabase.getInstance().getReference("Comments/");
        Key=mDatabase.push().getKey();

        DatabaseReference comments = FirebaseDatabase.getInstance().getReference("Comments/");
        Comment comment = new Comment(currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), Comentario.getText().toString());
        comments.child(getArguments().getString("imagen")).child(Key).setValue(comment);
        Comentario.setText("");
        Toast.makeText(getApplicationContext(), "Consulta enviada", Toast.LENGTH_LONG).show();

    }

    public void VerComentarios(){
        final Intent intentMain2 = new Intent(getActivity(), VerCommentsActivity.class);
       // mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives").child(Sector).child(nombre);
        intentMain2.putExtra("IDIniciativa",getArguments().getString("imagen"));
        intentMain2.putExtra("Titulo",getArguments().getString("Titulo"));
        getActivity().startActivity(intentMain2);
    }

    public void ratingSend(){
        /*final float rait = rtb.getRating();
        final DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // for (DataSnapshot child : snapshot.getChildren())
                // Create a LinearLayout element
                int nVotos = Integer.parseInt(snapshot.child("Nvotos").getValue().toString());
                int nVotos2 = nVotos+1;
                userInitiatives.child("rating").setValue(((Float.parseFloat(snapshot.child("rating").getValue().toString())*nVotos)+rait)/nVotos2);
                userInitiatives.child("Nvotos").setValue(nVotos2);

                Toast.makeText(getActivity(),"Calificación realizada",Toast.LENGTH_LONG).show();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });*/

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
        btnRate = (Button) dialog.findViewById(R.id.btn_Rate);
        date = (TextView) dialog.findViewById(R.id.rate_date);
        title = (TextView) dialog.findViewById(R.id.rate_title);
        creator = (TextView) dialog.findViewById(R.id.rate_creator);

        creator.setText("Por ".concat(Nombre.getText().toString()));
        title.setText(getArguments().getString("Titulo"));
        date.setText(hInicio.getText().toString().concat("\n").concat(hFin.getText().toString()));
        String imagen = getArguments().getString("imagen");

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


        btnRate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                rateThis();
            }
        });
        builder.show();


    }
    public void rateThis(){

            final DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    // for (DataSnapshot child : snapshot.getChildren())
                    // Create a LinearLayout element
                    final float rait = rtb2.getRating();
                    int nVotos = Integer.parseInt(snapshot.child("Nvotos").getValue().toString());
                    int nVotos2 = nVotos+1;
                    userInitiatives.child("rating").setValue(((Float.parseFloat(snapshot.child("rating").getValue().toString())*nVotos)+rait)/nVotos2);
                    userInitiatives.child("Nvotos").setValue(nVotos2);

                    Toast.makeText(getActivity(),"Calificación realizada",Toast.LENGTH_LONG).show();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }

            });

    }
}
