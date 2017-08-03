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
    String NotificationToken;
    public User(String name, String email,String imageurl,String notificationToken){
        this.Name=name;
        this.Email=email;
        this.ImageURL=imageurl;
        this.NotificationToken=notificationToken;

    }
    public User (){

    }
}
