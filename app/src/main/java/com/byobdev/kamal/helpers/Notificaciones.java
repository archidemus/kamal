package com.byobdev.kamal.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import com.byobdev.kamal.InitiativesActivity;
import com.byobdev.kamal.R;

/**
 * Created by crono on 27-07-17.
 */

public class Notificaciones extends AppCompatActivity{

    public void notificacion(View view){
        NotificationCompat.Builder notificacion = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setLargeIcon((((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.kamal_logo)).getBitmap()))
                .setContentTitle("Iniciativa de interes cercana")
                .setContentText("Apreta aqui para ir a la iniciativa")
                .setTicker("Iniciativa cercana");

        Intent inotificacion = new Intent(this, InitiativesActivity.class);
        PendingIntent intentePendiente = PendingIntent.getActivity(this,0,inotificacion,0);

        notificacion.setContentIntent(intentePendiente);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //El 10 es la id, se puede poner cualquiera
        nm.notify(10,notificacion.build());


    }
    //Esta no es necesario, pero podria ser util
    public void BorrarTodasNotificaciones(View v){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }


}
