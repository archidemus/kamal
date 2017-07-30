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
    String Nombre;
    String Descripcion;
    Double Latitud;
    Double Longitud;
    String Uid;
    //SimpleDateFormat FechaInicio;
    //SimpleDateFormat FechaFin;



    public Initiative(String nombre, String descripcion, Double latitud, Double longitud, String uid){
        this.Nombre=nombre;
        this.Descripcion=descripcion;
        this.Latitud=latitud;
        this.Longitud=longitud;
        this.Uid=uid;
        //this.FechaInicio=fechainicio;
        //this.FechaFin=fechafin;
    }
    public Initiative(){

    }
}
