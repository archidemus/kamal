package com.byobdev.kamal;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;


/**
 * Created by carlos on 26-07-17.
 */

public class Initiative {
    String Titulo;
    String Nombre;
    String Descripcion;
    Double Latitud;
    Double Longitud;
    String Uid;
    String ImageURL;
    String Tipo;
    //SimpleDateFormat FechaInicio;
    //SimpleDateFormat FechaFin;



    public Initiative(String titulo, String nombre, String descripcion, Double latitud, Double longitud, String image,String uid,String tipo){
        this.Titulo = titulo;
        this.Nombre=nombre;
        this.Descripcion=descripcion;
        this.Latitud=latitud;
        this.Longitud=longitud;
        this.ImageURL = image;
        this.Uid=uid;
        this.Tipo=tipo;
        //this.FechaInicio=fechainicio;
        //this.FechaFin=fechafin;
    }
    public Initiative(){

    }
}
