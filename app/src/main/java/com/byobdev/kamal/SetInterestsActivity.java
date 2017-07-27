package com.byobdev.kamal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.byobdev.kamal.helpers.LocationGPS;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import static android.R.attr.duration;

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
        /*mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {//NO SE LEER DATOS NO SE LEER DATOS NO SE LEER DATOS NO SE LEER DATOS NO SE LEER DATOS NO SE LEER DATOS
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Interests interests2=dataSnapshot.getValue(Interests.class);
                if(interests2.Arte){
                    finish();
                    arte.setChecked(true);
                }
                if(interests2.Comida){
                    finish();
                    comida.setChecked(true);
                }
                if(interests2.Deporte){
                    finish();
                    deporte.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });*/

    }

    public void SaveInterests(View view){
        Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),arte.isChecked());
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        finish();
    }
}