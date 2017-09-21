package com.byobdev.kamal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    Button Editar;
    RatingBar rtb;
    private DatabaseReference mDatabase;
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







}
