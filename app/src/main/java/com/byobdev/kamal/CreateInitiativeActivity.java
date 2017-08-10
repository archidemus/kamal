package com.byobdev.kamal;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import com.byobdev.kamal.DBClasses.Initiative;
import com.byobdev.kamal.AppHelpers.LocationGPS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.ProgressDialog;
import android.net.Uri;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.widget.EditText;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateInitiativeActivity extends AppCompatActivity{
    EditText titulo;
    EditText description;
    EditText hInicio;
    EditText hTermino;
    Double latitud;
    Double longitud;
    String imagen;
    Spinner spinner;
    Button button;
    ArrayAdapter<CharSequence> adapter;

    private DatabaseReference mDatabase;
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath;
    String direccion;
    int PICK_IMAGE_REQUEST = 111;
    ImageView imgView;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_initiative);
        titulo   = (EditText)findViewById(R.id.titleInput);
        description   = (EditText)findViewById(R.id.descriptionInput);
        hInicio = (EditText)findViewById(R.id.hInicio);
        hTermino = (EditText)findViewById(R.id.hTermino);

        //para agregar la lista de tipo de iniciativa
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipo_Iniciativa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives");
        key=mDatabase.push().getKey();
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando...");

    }

    public void createInitiative(View view){
        if( titulo.getText().toString().equals("")){


            titulo.setError( "El titulo es requerido!" );

        }else if(latitud == null){

            Toast.makeText(CreateInitiativeActivity.this, "La poscion es requerida!", Toast.LENGTH_SHORT).show();
        }else if(description.getText().toString().equals("")){

            description.setError("La descripcion es requerida!");

        }
        else{
            String nombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            DateFormat formatter = new SimpleDateFormat("HH:mm");
            DateFormat formatterF = new SimpleDateFormat("HH:mm");
            Date date =null;
            Date date1 = null;
            try{
                date = formatter.parse(hInicio.getText().toString());
                 date1= formatter.parse(hTermino.getText().toString());
            }catch (Exception e){

            }


            String feI = formatter.format(date);
            String feT = formatterF.format(date1);
            String interest = spinner.getSelectedItem().toString();
            if (interest.equals("Música")){
                interest = "Musica";
            }
            Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,key ,FirebaseAuth.getInstance().getCurrentUser().getUid(),interest, direccion.toString(), feI, feT);
            mDatabase.child(key).setValue(initiative);
            finish();
        }

    }



    public void obtenerGPS(View view){
        LocationGPS gps=new LocationGPS(this);
        latitud = gps.getLatitud();
        longitud = gps.getLongitud();
        try{
            direccion = gps.getAddress(latitud, longitud);
        }catch (Exception e){

        }

        Toast.makeText(CreateInitiativeActivity.this, "Posicion obtenida", Toast.LENGTH_SHORT).show();
    }

    public void escogerImagen(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Seleccione imagen"), PICK_IMAGE_REQUEST);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(filePath != null) {
            pd.show();

            StorageReference childRef = storageRef.child(key);

            //uploading the image
            final UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(CreateInitiativeActivity.this, "Subida Exitosa", Toast.LENGTH_SHORT).show();
                    imagen = uploadTask.getSnapshot().getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(CreateInitiativeActivity.this, "Error en la subida -> " + e, Toast.LENGTH_SHORT).show();
                    imagen = null;
                }
            });
        }
        else {
            Toast.makeText(CreateInitiativeActivity.this, "Error en la subida", Toast.LENGTH_SHORT).show();
        }
    }



}
