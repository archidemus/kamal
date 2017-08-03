package com.byobdev.kamal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by carlos on 26-07-17.
 */

public class SetInterestsActivity extends AppCompatActivity{
    CheckBox comida;
    CheckBox deporte;
    CheckBox arte;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_interests);
        comida = (CheckBox) findViewById(R.id.checkboxComida);
        deporte = (CheckBox) findViewById(R.id.checkboxDeporte);
        arte = (CheckBox) findViewById(R.id.checkboxArte);
        mDatabase = FirebaseDatabase.getInstance().getReference("Interests");
        Intent i=getIntent();
        Interests interests2=(Interests)i.getSerializableExtra("userInterests");

        if(interests2.Deporte){
            deporte.setChecked(true);
        }
        if(interests2.Arte){
            arte.setChecked(true);
        }
        if(interests2.Comida){
            comida.setChecked(true);
        }
    }

    public void SaveInterests(View view){
        Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),arte.isChecked());
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        finish();
    }
}