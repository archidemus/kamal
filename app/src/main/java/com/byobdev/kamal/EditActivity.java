package com.byobdev.kamal;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byobdev.kamal.AppHelpers.LocationGPS;
import com.byobdev.kamal.DBClasses.Initiative;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by crono on 03-09-17.
 */

public class EditActivity extends AppCompatActivity {
    EditText titulo;
    EditText description;
    TextView hInicio;
    TextView hTermino;
    Double latitud;
    Double longitud;
    String imagen;
    Spinner spinner;
    Button button;
    ArrayAdapter<CharSequence> adapter;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath;
    String direccion;
    int PICK_IMAGE_REQUEST = 111;
    ImageView imgView;
    String key;
    String imageEdit;
    String IDanterior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_initiative);
        titulo   = (EditText)findViewById(R.id.titleEdit);
        Intent i = getIntent();
        titulo.setText(i.getStringExtra("Titulo"));
        description   = (EditText)findViewById(R.id.descriptionEdit);
        description.setText(i.getStringExtra("Descripcion"));
        hTermino = (TextView)findViewById(R.id.HoraFinalfinal);
        hTermino.setText(i.getStringExtra("duracion"));
        hInicio = (TextView)findViewById(R.id.HoraIniciofinal);
        hInicio.setText(i.getStringExtra("hinicio"));

        //para agregar la lista de tipo de iniciativa
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipo_Iniciativa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(i.getStringExtra("Tipo").equals("Teatro")){
            spinner.setSelection(0);
        }else if(i.getStringExtra("Tipo").equals("Musica")){
            spinner.setSelection(1);
        }else if(i.getStringExtra("Tipo").equals("Deporte")){
            spinner.setSelection(2);
        }else{
            spinner.setSelection(3);
        }

        longitud = Double.parseDouble(i.getStringExtra("Longitud"));
        latitud = Double.parseDouble(i.getStringExtra("Latitud"));
        direccion = i.getStringExtra("Direccion");
        imageEdit = i.getStringExtra("Imagen");
        IDanterior = i.getStringExtra("IDanterior");

        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives");
        key=mDatabase.push().getKey();
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando....");

    }

    public void editInitiative(View view){
        if( titulo.getText().toString().equals("")){


            titulo.setError( "El titulo es requerido!" );

        }else if(latitud == null){

            Toast.makeText(EditActivity.this, "La poscion es requerida!", Toast.LENGTH_SHORT).show();
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

            FirebaseDatabase.getInstance().getReference("Initiatives").child(IDanterior).removeValue();
            mDatabase2 = FirebaseDatabase.getInstance().getReference("UserInitiatives").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // for (DataSnapshot child : snapshot.getChildren())
                    // Create a LinearLayout element
                    snapshot.child(IDanterior).getRef().removeValue();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }

            });
            if(imagen != null){
                imageEdit = key;
            }

            Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,imageEdit ,FirebaseAuth.getInstance().getCurrentUser().getUid(),interest, direccion.toString(), feI, feT);
            mDatabase.child(key).setValue(initiative);
            DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("UserInitiatives/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            userInitiatives.child(key).setValue(titulo.getText().toString());
            finish();
            Toast.makeText(EditActivity.this, "Iniciativa editada", Toast.LENGTH_SHORT).show();

        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new HoraActivity();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTimePickerDialog2(View v) {
        DialogFragment newFragment = new HoraActivity();
        newFragment.show(getFragmentManager(), "timePicker2");
    }


    public void obtenerGPS(View view){
        LocationGPS gps=new LocationGPS(this);
        latitud = gps.getLatitud();
        longitud = gps.getLongitud();
        try{
            direccion = gps.getAddress(latitud, longitud);
        }catch (Exception e){

        }

        Toast.makeText(EditActivity.this, "Posicion obtenida", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditActivity.this, "Subida Exitosa", Toast.LENGTH_SHORT).show();
                    imagen = uploadTask.getSnapshot().getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(EditActivity.this, "Error en la subida -> " + e, Toast.LENGTH_SHORT).show();
                    imagen = null;
                }
            });
        }
        else {
            Toast.makeText(EditActivity.this, "Error en la subida", Toast.LENGTH_SHORT).show();
        }
    }


}
