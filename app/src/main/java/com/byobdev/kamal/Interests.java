package com.byobdev.kamal;

import java.io.Serializable;

/**
 * Created by carlos on 27-07-17.
 */

public class Interests implements Serializable{
    public boolean Deporte;
    public boolean Comida;
    public boolean Arte;

    Interests(boolean deporte, boolean comida, boolean arte){
        this.Deporte=deporte;
        this.Comida=comida;
        this.Arte=arte;
    }

    Interests(){}
}
