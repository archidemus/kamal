package com.byobdev.kamal.DBClasses;

/**
 * Created by carlos on 31-07-17.
 */

public class User {
    public String Name;
    public String Email;
    public String ImageURL;
    public String NotificationToken;
    public float rating;
    public int Nvotos;
    public User(String name, String email,String imageurl,String notificationToken,float rating, int Nvotos){
        this.Name=name;
        this.Email=email;
        this.ImageURL=imageurl;
        this.NotificationToken=notificationToken;
        this.rating = rating;
        this.Nvotos = Nvotos;
    }

    public User (){}
}
