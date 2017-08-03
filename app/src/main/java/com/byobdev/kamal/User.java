package com.byobdev.kamal;

/**
 * Created by carlos on 31-07-17.
 */

public class User {
    public String Name;
    public String Email;
    public String ImageURL;
    public String NotificationToken;
    public User(String name, String email,String imageurl,String notificationToken){
        this.Name=name;
        this.Email=email;
        this.ImageURL=imageurl;
        this.NotificationToken=notificationToken;
    }

    public User (){}
}
