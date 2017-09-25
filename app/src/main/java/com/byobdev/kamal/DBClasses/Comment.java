package com.byobdev.kamal.DBClasses;

import static android.R.attr.name;
import static com.byobdev.kamal.R.string.email;

/**
 * Created by carlos on 20-09-17.
 */

public class Comment {
    public String Uid;
    public String Nombre;
    public String Text;
    public Comment(String Uid, String Nombre,String Text){
        this.Uid=Uid;
        this.Nombre=Nombre;
        this.Text=Text;
    }

    public Comment (){}
}
