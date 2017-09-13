package com.byobdev.kamal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import static android.R.attr.data;

/**
 * Created by crono on 03-09-17.
 */

public class ListActivity extends AppCompatActivity implements customButtonListener {

    private DatabaseReference mDatabase;
    protected static final int DIALOG_REMOVE_CALC = 1;
    protected static final int DIALOG_REMOVE_PERSON = 2;
    MenuItem delete;
    MenuItem edit;
    String[] completarLista;
    String[] SectorLista;
    String[] keyLista;
    String[] descriptionLista;
    String[] imageLista;
    ListView lista;
    int position;
    int prevPosition=-1;
    //Toolbar set
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_config, menu);
        delete=menu.findItem(R.id.delete);
        edit=menu.findItem(R.id.edit);
        delete.setVisible(false);
        edit.setVisible(false);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
                alert.setTitle("Alerta!");
                alert.setMessage("Estas seguro de que quieres eliminar esta iniciativa?");
                alert.setPositiveButton("SI", new Dialog.OnClickListener() {
                    private DatabaseReference mDatabase2;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Initiatives").child(SectorLista[position]).child(keyLista[position]).removeValue();
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
                return true;
            }
        });
        edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editar(keyLista[position], SectorLista[position]);
                return true;
            }
        });

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_initiative);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfig);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis Iniciativas");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
        Intent i=getIntent();
        lista = (ListView) findViewById(R.id.mobile_list);
        mDatabase = FirebaseDatabase.getInstance().getReference("UserInitiatives").child(i.getStringExtra("UserID"));
        final int[] k = {0};
        k[0] = 0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;
                    k[0] = 1;

                }
                completarLista = new String[t];
                keyLista = new String[t];
                SectorLista = new String[t];
                descriptionLista = new String[t];
                imageLista = new String[t];
                t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    completarLista[t] = child.child("Titulo").getValue().toString();
                    SectorLista[t] = child.child("Sector").getValue().toString();
                    keyLista[t] = child.getKey().toString();
                    descriptionLista[t] = child.child("Descripcion").getValue().toString();
                    imageLista[t] = child.child("image").getValue().toString();
                    t++;

                }
                if(k[0] ==0){
                    TextView noHay = new TextView(ListActivity.this);
                    noHay.setText("Usted no tiene iniciativas creadas");
                    noHay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    ll.addView(noHay);
                }else{
                    com.byobdev.kamal.ListAdapter adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarLista);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListAdapter(ListActivity.this, dataItems,keyLista, SectorLista,descriptionLista,imageLista,lista);
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


    public void editar(String nombre, final String Sector){

        final Intent intentMain2 = new Intent(this, EditActivity.class);
        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives").child(Sector).child(nombre);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               // for (DataSnapshot child : snapshot.getChildren())
                    // Create a LinearLayout element

                    intentMain2.putExtra("Descripcion",snapshot.child("Descripcion").getValue().toString());
                    intentMain2.putExtra("Titulo",snapshot.child("Titulo").getValue().toString());
                    intentMain2.putExtra("hinicio",snapshot.child("fechaInicio").getValue().toString());
                    intentMain2.putExtra("duracion",snapshot.child("fechaFin").getValue().toString());
                    intentMain2.putExtra("Tipo",snapshot.child("Tipo").getValue().toString());
                    intentMain2.putExtra("Latitud",snapshot.child("Latitud").getValue().toString());
                    intentMain2.putExtra("Longitud",snapshot.child("Longitud").getValue().toString());
                    intentMain2.putExtra("Direccion",snapshot.child("Direccion").getValue().toString());
                    intentMain2.putExtra("Imagen",snapshot.child("image").getValue().toString());
                    intentMain2.putExtra("IDanterior",snapshot.getKey());
                    intentMain2.putExtra("Sector",Sector);

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
    public void getPosition1234(int position) {
        if(prevPosition!=-1){
            lista.getChildAt(prevPosition).setBackgroundResource(0);
        }
        this.position=position;
        this.prevPosition=this.position;
        lista.getChildAt(position).setBackgroundResource(R.color.gray_holo_light);
        edit.setVisible(true);
        delete.setVisible(true);


    }



}
