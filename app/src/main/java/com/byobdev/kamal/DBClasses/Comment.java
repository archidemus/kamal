package com.byobdev.kamal.DBClasses;

/**
 * Created by crono on 06-10-17.
 */

public class Comment {

    public String Nombre;
    public String Image;
    public String Comentario;
    public String Respuesta;


    public Comment(String Nombre, String Image, String Comentario, String Respuesta){
        this.Nombre = Nombre;
        this.Image = Image;
        this.Comentario = Comentario;
        this.Respuesta = Respuesta;
    }

    public Comment(){ }

}
