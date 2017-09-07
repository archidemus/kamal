package com.byobdev.kamal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.byobdev.kamal.ListAdapter.customButtonListener;

/**
 * Created by crono on 03-09-17.
 */

public class ListActivity extends AppCompatActivity implements customButtonListener {

    private DatabaseReference mDatabase;
    protected static final int DIALOG_REMOVE_CALC = 1;
    protected static final int DIALOG_REMOVE_PERSON = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_initiative);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
        Intent i=getIntent();
        final ListView lista = (ListView) findViewById(R.id.mobile_list);

        mDatabase = FirebaseDatabase.getInstance().getReference("UserInitiatives").child(i.getStringExtra("UserID"));
        final int[] k = {0};
        k[0] = 0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final String[] completarLista;
                final String[] keyLista;
                int t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;
                    k[0] = 1;

                }
                completarLista = new String[t];
                keyLista = new String[t];
                t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    completarLista[t] = child.getValue().toString();
                    keyLista[t] = child.getKey().toString();
                    t++;

                }
                if(k[0] ==0){
                    ll.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    TextView noHay = new TextView(ListActivity.this);
                    noHay.setText("Usted no tiene iniciativas creadas");
                    noHay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    ll.addView(noHay);
                }else{
                    com.byobdev.kamal.ListAdapter adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarLista);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListAdapter(ListActivity.this, dataItems,keyLista);
                    adapter.setCustomButtonListner(ListActivity.this);
                    lista.setAdapter(adapter);
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });



    }

    public void editar(String nombre){

        final Intent intentMain2 = new Intent(this, EditActivity.class);
        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives").child(nombre);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               // for (DataSnapshot child : snapshot.getChildren())
                    // Create a LinearLayout element

                    intentMain2.putExtra("Descripcion",snapshot.child("Descripcion").getValue().toString());
                    intentMain2.putExtra("Titulo",snapshot.child("Titulo").getValue().toString());
                    intentMain2.putExtra("hinicio",snapshot.child("hInicio").getValue().toString());
                    intentMain2.putExtra("duracion",snapshot.child("hTermino").getValue().toString());
                    intentMain2.putExtra("Tipo",snapshot.child("Tipo").getValue().toString());
                    intentMain2.putExtra("Latitud",snapshot.child("Latitud").getValue().toString());
                    intentMain2.putExtra("Longitud",snapshot.child("Longitud").getValue().toString());
                    intentMain2.putExtra("Direccion",snapshot.child("Direccion").getValue().toString());
                    intentMain2.putExtra("Imagen",snapshot.child("image").getValue().toString());
                    intentMain2.putExtra("IDanterior",snapshot.getKey());

                ListActivity.this.startActivity(intentMain2);
                finish();



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

    }
    @Override
    public void onButtonClickListner(final int position, final String[] keyLista) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
        alert.setTitle("Alerta!");
        alert.setMessage("Estas seguro de que quieres eliminar esta iniciativa?");
        alert.setPositiveButton("SI", new Dialog.OnClickListener() {
            private DatabaseReference mDatabase2;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference("Initiatives").child(keyLista[position]).removeValue();
                mDatabase2 = FirebaseDatabase.getInstance().getReference("UserInitiatives").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // for (DataSnapshot child : snapshot.getChildren())
                        // Create a LinearLayout element
                        snapshot.child(keyLista[position]).getRef().removeValue();
                        finish();
                        startActivity(getIntent());

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }

                });
                dialog.dismiss();


            }
        });
        alert.setNegativeButton("NO", new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();


    }
    @Override
    public void onButtonClickListner2(int position, String[] keyLista) {
        editar(keyLista[position]);

    }



}
