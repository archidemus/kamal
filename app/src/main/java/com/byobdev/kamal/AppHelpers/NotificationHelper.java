package com.byobdev.kamal.AppHelpers;

import android.app.Application;

/**
 * Created by Teikkenn on 9/9/2017.
 */

public class NotificationHelper extends Application {
    private int c=0;
    private long time=0;

    public int cvalue(){
        return this.c;
    }
    public void NuevoMensaje(){
        this.c++;
    }
    public void Setc(int d){
        this.c = d;
    }
    public void ColapsoMensaje(){
        this.c=0;
    }
    public void AddTime(long a){
        this.time=time+a;
    }
    public void ResetTime(){
        this.time =0;
    }
}
