package com.byobdev.kamal;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.byobdev.kamal.DBClasses.Interests;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by carlos on 26-07-17.
 */

public class SetInterestsActivity extends AppCompatActivity{
    CheckBox comida;
    CheckBox deporte;
    CheckBox musica;
    CheckBox teatro;
    TextView Sesion;
    MenuItem check;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    private DatabaseReference mDatabase;


    //Toolbar set
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_config, menu);
        check = menu.findItem(R.id.done);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        check.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SetInterestsActivity.this.SaveInterests(item);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_interests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfig);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Configuración");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        comida = (CheckBox) findViewById(R.id.checkboxComida);
        deporte = (CheckBox) findViewById(R.id.checkboxDeporte);
        musica = (CheckBox) findViewById(R.id.checkboxMusica);
        teatro = (CheckBox) findViewById(R.id.checkboxTeatro);
        spinner = (Spinner) findViewById(R.id.spinnerConfig);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Distancia_Notificacion, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Sesion = (TextView) findViewById(R.id.Sesion);
        Sesion.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //perform your action here
                new AlertDialog.Builder(SetInterestsActivity.this)
                        .setTitle("Confirmacion de cierre de sesion")
                        .setMessage("Seguro que quieres cerrar sesion?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.unlink(user.getProviderId());
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                finish();


                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


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
            spinner.setSelection(0);
        }
        if(interests2.radio3km){
            spinner.setSelection(1);
        }
        if(interests2.radio10km){
            spinner.setSelection(2);
        }
    }

    public void SaveInterests(MenuItem item){
        String interest = spinner.getSelectedItem().toString();
        if(interest.equals("500 metros")){
            Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),teatro.isChecked(),musica.isChecked(), true, false, false);
            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        }else if(interest.equals("3 kilómetros")){
            Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),teatro.isChecked(),musica.isChecked(), false, true, false);
            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        }else{
            Interests interests=new Interests(deporte.isChecked(),comida.isChecked(),teatro.isChecked(),musica.isChecked(), false, false, true);
            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(interests);
        }


        finish();
    }

}