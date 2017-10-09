package com.byobdev.kamal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    TextView Nombre;
    TextView Descripcion;
    ImageView Image;
    TextView Lugar;
    TextView hInicio;
    TextView hFin;
    String image;
    EditText Comentario;
    Button Editar;
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
    String[] respuesaLista;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    FirebaseAuth.AuthStateListener authListener  = new FirebaseAuth.AuthStateListener(){
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            currentUser  = firebaseAuth.getCurrentUser();

        }
    };

    public RatingBar.OnRatingBarChangeListener ListenerRating = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            rtb.setRating(v);
            final float rait = v;
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

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }

            });
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
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+getArguments().getString("Uid"));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // for (DataSnapshot child : snapshot.getChildren())
                // Create a LinearLayout element
                rtb.setRating(Float.parseFloat(snapshot.child("rating").getValue().toString()));
                rtb.setOnRatingBarChangeListener(ListenerRating);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

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
                    respuesaLista = new String[3];
                }else if(t==2){
                    completarLista = new String[2];
                    keyLista = new String[2];
                    SectorLista = new String[2];
                    descriptionLista = new String[2];
                    imageLista = new String[2];
                    respuesaLista = new String[2];

                }else if(t==1){
                    completarLista = new String[1];
                    keyLista = new String[1];
                    SectorLista = new String[1];
                    descriptionLista = new String[1];
                    imageLista = new String[1];
                    respuesaLista = new String[1];
                }
                int r = t;
                t=0;
                if(r > 0){
                    for (final DataSnapshot child : snapshot.getChildren()) {
                        // Create a LinearLayout element
                        if(t==3){
                            break;
                        }
                        if(child.child("Comentario").getValue().toString().equals("Creador")){

                        }else{
                            respuesaLista[t] = child.child("Respuesta").getValue().toString();
                            completarLista[t] = child.child("Nombre").getValue().toString();
                            keyLista[t] = child.getKey().toString();
                            descriptionLista[t] = child.child("Comentario").getValue().toString();
                            imageLista[t] = child.child("Image").getValue().toString();
                            t++;

                        }



                    }
                    com.byobdev.kamal.ListCommentFragmentActivity adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarLista);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListCommentFragmentActivity(getActivity(), dataItems,keyLista,descriptionLista,imageLista,lista, respuesaLista);
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
            Picasso.with(this.getContext())
                    .load(url)
                    .error(R.drawable.kamal_logo)
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
        Toast.makeText(getApplicationContext(), "Consulta enviada", Toast.LENGTH_LONG).show();

    }

    public void VerComentarios(){
        final Intent intentMain2 = new Intent(getActivity(), VerCommentsActivity.class);
       // mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives").child(Sector).child(nombre);
        intentMain2.putExtra("IDIniciativa",getArguments().getString("imagen"));
        intentMain2.putExtra("Titulo",getArguments().getString("Titulo"));
        getActivity().startActivity(intentMain2);
    }

}
