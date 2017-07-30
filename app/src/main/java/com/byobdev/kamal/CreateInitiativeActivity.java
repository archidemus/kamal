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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
/**
 * Created by carlos on 26-07-17.
 */

public class CreateInitiativeActivity extends AppCompatActivity{
    EditText titulo;
    EditText description;
    Double latitud;
    Double longitud;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_initiative);
        titulo   = (EditText)findViewById(R.id.titleInput);
        description   = (EditText)findViewById(R.id.descriptionInput);
        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives");
    }

    public void createInitiative(View view){
        String nombre = "Pepito";
        Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.push().setValue(initiative);
        finish();
    }

    public void obtenerGPS(View view){
        LocationGPS gps=new LocationGPS(this);
        latitud = gps.getLatitud();
        longitud = gps.getLongitud();
    }
}
