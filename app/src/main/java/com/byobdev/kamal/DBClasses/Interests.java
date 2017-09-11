package com.byobdev.kamal.DBClasses;

import java.io.Serializable;

/**
 * Created by carlos on 27-07-17.
 */

public class Interests implements Serializable{
    public boolean Deporte;
    public boolean Comida;
    public boolean Musica;
    public boolean Teatro;
    public boolean radio500m;
    public boolean radio3km;
    public boolean radio10km;


    public Interests(boolean deporte, boolean comida, boolean teatro, boolean musica, boolean r500, boolean r3km, boolean r10km){
        this.Deporte=deporte;
        this.Comida=comida;
        this.Musica=musica;
        this.Teatro=teatro;
        this.radio500m = r500;
        this.radio3km = r3km;
        this.radio10km = r10km;
    }

    Interests(){ }
}
