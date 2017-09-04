package com.byobdev.kamal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by crono on 03-09-17.
 */

public class ListActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_initiative);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
        Intent i=getIntent();
        mDatabase = FirebaseDatabase.getInstance().getReference("UserInitiatives").child(i.getStringExtra("UserID"));
        final int[] k = {0};
        k[0] = 0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    final Button rowButton = new Button(ListActivity.this);
                    rowButton.setText(child.getValue().toString());
                    k[0] = 1;
                    rowButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Code here executes on main thread after user presses button
                            editar(child.getKey().toString());
                        }
                    });

                    ll.addView(rowButton);



                }
                if(k[0] ==0){
                    ll.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    TextView noHay = new TextView(ListActivity.this);
                    noHay.setText("Usted no tiene iniciativas creadas");
                    noHay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    ll.addView(noHay);
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

}
