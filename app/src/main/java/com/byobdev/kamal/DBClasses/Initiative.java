package com.byobdev.kamal.DBClasses;

import static android.R.attr.src;

/**
 * Created by carlos on 26-07-17.
 */

public class Initiative {

    public String Titulo;
    public String Nombre;
    public String Descripcion;
    public Double Latitud;
    public Double Longitud;
    public String Uid;
    public String image;
    public String Tipo;
    public String Direccion;
    public String hInicio;
    public String hTermino;
    //SimpleDateFormat FechaInicio;
    //SimpleDateFormat FechaFin;

    public Initiative(String titulo, String nombre, String descripcion, Double latitud, Double longitud, String image,String uid,String tipo, String direccion, String fInicio, String fTermino){
        this.Titulo = titulo;
        this.Nombre=nombre;
        this.Descripcion=descripcion;
        this.Latitud=latitud;
        this.Longitud=longitud;
        this.image = image;
        this.Uid=uid;
        this.Tipo=tipo;
        this.Direccion = direccion;
        this.hInicio = fInicio;
        this.hTermino = fTermino;
        //this.FechaInicio=fechainicio;
        //this.FechaFin=fechafin;
    }
    public Initiative(){ }
}
