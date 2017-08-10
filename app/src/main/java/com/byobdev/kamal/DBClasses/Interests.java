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


    public Interests(boolean deporte, boolean comida, boolean teatro, boolean musica){
        this.Deporte=deporte;
        this.Comida=comida;
        this.Musica=musica;
        this.Teatro=teatro;
    }

    Interests(){ }
}
