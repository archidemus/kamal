package com.byobdev.kamal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byobdev.kamal.DBClasses.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by crono on 08-10-17.
 */

public class VerCommentsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    protected static final int DIALOG_REMOVE_CALC = 1;
    protected static final int DIALOG_REMOVE_PERSON = 2;
    String[] completarLista;
    String[] SectorLista;
    String[] keyLista;
    String[] descriptionLista;
    String[] imageLista;
    ListView lista;
    String Key;
    ImageButton sendCom;
    EditText Comentario;
    int position;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfigComments);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        Intent i=getIntent();
        getSupportActionBar().setTitle(i.getStringExtra("Titulo"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.verComments);
        FirebaseAuth.AuthStateListener authListener  = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser  = firebaseAuth.getCurrentUser();

            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        sendCom = (ImageButton) findViewById(R.id.VerSendComment);
        sendCom.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                SendComment();
            }
        });
        Comentario = (EditText) findViewById(R.id.VerCommentInput);
        lista = (ListView) findViewById(R.id.verCommentList);
        mDatabase = FirebaseDatabase.getInstance().getReference("Comments").child(i.getStringExtra("IDIniciativa"));
        final int[] k = {0};
        k[0] = 0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;
                    if(t>1){
                        k[0] = 1;
                    }

                }
                completarLista = new String[t];
                keyLista = new String[t];
                descriptionLista = new String[t];
                imageLista = new String[t];
                t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    if(child.child("Comentario").getValue().toString().equals("Creador")){

                    }else{
                        completarLista[t] = child.child("Nombre").getValue().toString();
                        keyLista[t] = child.getKey().toString();
                        descriptionLista[t] = child.child("Comentario").getValue().toString();
                        imageLista[t] = child.child("Image").getValue().toString();
                        t++;
                    }

                }
                if(k[0] ==0){
                    TextView noHay = new TextView(VerCommentsActivity.this);
                    noHay.setText("No existen consultas sobre esta iniciativa");
                    noHay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    ll.addView(noHay);
                }else{
                    com.byobdev.kamal.ListVerCommentActivity adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarLista);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListVerCommentActivity(VerCommentsActivity.this, dataItems,keyLista,descriptionLista,imageLista,lista);
                    lista.setAdapter(adapter);
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });



    }

    public void SendComment(){

        mDatabase = FirebaseDatabase.getInstance().getReference("Comments/");
        Key=mDatabase.push().getKey();

        DatabaseReference comments = FirebaseDatabase.getInstance().getReference("Comments/");
        Comment comment = new Comment(currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), Comentario.getText().toString());
        comments.child(getIntent().getStringExtra("IDIniciativa")).child(Key).setValue(comment);
        Comentario.setText("");
        Toast.makeText(getApplicationContext(), "Consulta enviada", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }

}
