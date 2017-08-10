package com.byobdev.kamal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.byobdev.kamal.DBClasses.Interests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by carlos on 26-07-17.
 */

public class SetInterestsActivity extends AppCompatActivity{
    CheckBox comida;
    CheckBox deporte;
    CheckBox musica;
    CheckBox teatro;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_interests);
        comida = (CheckBox) findViewById(R.id.checkboxComida);
        deporte = (CheckBox) findViewById(R.id.checkboxDeporte);
        musica = (CheckBox) findViewById(R.id.checkboxMusica);
        teatro = (CheckBox) findViewById(R.id.checkboxTeatro);
        mDatabase = FirebaseDatabase.getInstance().getReference("Interests");
        Intent i=getIntent();
        Interests interests2=(Interests)i.getSerializableExtra("userInterests");

        if(interests2.Deporte){
            deporte.setChecked(true);
        }
        if(interests2.Musica){
            musica.setChecked(true);
        }
        if(interests2.Comida){
            comida.setChecked(true);
        }
        if(interests2.Teatro){
            teatro.setChecked(true);
        }
    }

    public void SaveInterests(View view){
        Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),teatro.isChecked(),musica.isChecked());
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        finish();
    }
}