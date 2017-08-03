package com.byobdev.kamal;

import android.net.Uri;

import java.net.URL;


/**
 * Created by carlos on 31-07-17.
 */

public class User {
    String Name;
    String Email;
    String ImageURL;
    String notificationToken;
    public User (String name, String email,String imageurl){
        this.Name=name;
        this.Email=email;
        this.ImageURL=imageurl;
    }
    public Uri getImageUri(){
        return Uri.parse(ImageURL);
    }
    public User (){

    }
}
