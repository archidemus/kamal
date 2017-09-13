package com.byobdev.kamal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

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
