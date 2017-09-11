package com.byobdev.kamal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

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
    RadioButton r500, r3km, r10km;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_interests);
        comida = (CheckBox) findViewById(R.id.checkboxComida);
        deporte = (CheckBox) findViewById(R.id.checkboxDeporte);
        musica = (CheckBox) findViewById(R.id.checkboxMusica);
        teatro = (CheckBox) findViewById(R.id.checkboxTeatro);
        r500 = (RadioButton) findViewById(R.id.radioButton);
        r3km = (RadioButton) findViewById(R.id.radioButton2);
        r10km = (RadioButton) findViewById(R.id.radioButton3);


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
        if(interests2.radio500m){
            r500.setChecked(true);
        }
        if(interests2.radio3km){
            r3km.setChecked(true);
        }
        if(interests2.radio10km){
            r10km.setChecked(true);
        }
    }

    public void SaveInterests(View view){
        Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),teatro.isChecked(),musica.isChecked(), r500.isChecked(), r3km.isChecked(), r10km.isChecked());
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        finish();
    }
    public void onRadioButtonClicked(View view) {

        // Is the button now checked?

        boolean checked = ((RadioButton) view).isChecked();

        // hacemos un case con lo que ocurre cada vez que pulsemos un botón

        switch(view.getId()) {
            case R.id.radioButton:
                if (checked)
                    r3km.setChecked(false);
                    r10km.setChecked(false);
                    break;
            case R.id.radioButton2:
                if (checked)
                    r500.setChecked(false);
                    r10km.setChecked(false);
                    break;
            case R.id.radioButton3:
                if (checked)
                    r3km.setChecked(false);
                    r500.setChecked(false);
                    break;

        }
    }
}