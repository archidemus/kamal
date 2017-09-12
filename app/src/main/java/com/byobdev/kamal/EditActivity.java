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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by crono on 03-09-17.
 */

public class EditActivity extends AppCompatActivity {
    EditText titulo;
    EditText description;
    Double latitud;
    Double longitud;
    String imagen;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath;
    String direccion;
    int PLACE_PICKER_REQUEST = 1;
    int PICK_IMAGE_REQUEST = 111;
    ImageView imgView;
    String key;
    String imageEdit;
    String IDanterior;
    Date dateInits, dateFins;
    TextView fechaInicio;
    TextView fechaTermino;
    long dateDiff;

    String getSector(double latitude, double longitude){
        return Integer.toString((int)(latitude*50))+","+Integer.toString((int)(longitude*50));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_initiative);
        titulo   = (EditText)findViewById(R.id.titleEdit);
        Intent i = getIntent();
        titulo.setText(i.getStringExtra("Titulo"));
        description   = (EditText)findViewById(R.id.descriptionEdit);
        description.setText(i.getStringExtra("Descripcion"));

        fechaTermino = (TextView)findViewById(R.id.txt_fecha_termino_vista);
        String dateF2 = mFormatter.format(new Date(Long.parseLong(i.getStringExtra("duracion"))));
        fechaTermino.setText(dateF2);
        fechaInicio = (TextView)findViewById(R.id.txt_fecha_inicio_vista);
        String dateI2 = mFormatter.format(new Date(Long.parseLong(i.getStringExtra("hinicio"))));
        fechaInicio.setText(dateI2);

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
        key=IDanterior;
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando....");

    }

    //Listener boton fecha Inicio
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            if(dateDifference(fechaTermino.getText().toString(),date) < 0){
                fechaInicio.setText(mFormatter.format(date));
                fechaTermino.setText(mFormatter.format(date));

            }
            else {
                fechaInicio.setText(mFormatter.format(date));
            }
            Toast.makeText(EditActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(EditActivity.this,
                    "Ha cancelado la selección", Toast.LENGTH_SHORT).show();
        }
    };

    //Listener boton fecha Termino
    private SlideDateTimeListener listener2 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            if(dateDifference(fechaInicio.getText().toString(),date) >=0){
                fechaTermino.setText(fechaInicio.getText().toString());
            }
            else{
                fechaTermino.setText(mFormatter.format(date));
            }

            Toast.makeText(EditActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(EditActivity.this,
                    "Ha cancelado la selección", Toast.LENGTH_SHORT).show();
        }
    };

    public void editInitiative(View view){
        if( titulo.getText().toString().equals("")){


            titulo.setError( "Título Obligatorio!" );

        }else if(latitud == null){

            Toast.makeText(EditActivity.this, "Ubicación Obligatoria!", Toast.LENGTH_SHORT).show();
        }else if(description.getText().toString().equals("")){

            description.setError("La descripción es requerida!");

        }
        else{
            String nombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


            String fechaInit = fechaInicio.getText().toString();
            String fechaFin = fechaTermino.getText().toString();
            try {
                dateInits = mFormatter.parse(fechaInit);
                dateFins = mFormatter.parse(fechaFin);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long feI=dateInits.getTime();
            long feT=dateFins.getTime();
            String interest = spinner.getSelectedItem().toString();
            if (interest.equals("Música")){
                interest = "Musica";
            }
            Intent i = getIntent();
            FirebaseDatabase.getInstance().getReference("Initiatives").child(i.getStringExtra("Sector")).child(IDanterior).removeValue();
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
            mDatabase.child(getSector(latitud,longitud)).child(key).setValue(initiative);
            DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("UserInitiatives/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            userInitiatives.child(key).child("Sector").setValue(getSector(latitud,longitud));
            userInitiatives.child(key).child("Titulo").setValue(titulo.getText().toString());
            finish();
            Toast.makeText(EditActivity.this, "Iniciativa editada", Toast.LENGTH_SHORT).show();

        }
    }

    public void showDatePickerDialog(View v) {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(new Date())
                //.setMaxDate(maxDate)
                .setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    public void showDatePickerDialog2(View v) {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener2)
                .setInitialDate(new Date())
                .setMinDate(new Date())
                //.setMaxDate(maxDate)
                .setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();

    }


    public void obtenerGPS(View view){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception e){

        }
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

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                latitud=place.getLatLng().latitude;
                longitud=place.getLatLng().longitude;
                direccion=place.getAddress().toString();
            }
        }

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

    private long dateDifference(String fecha, Date date){
        try {
            dateInits = mFormatter.parse(fecha);
            dateFins = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateDiff = dateInits.getTime() - dateFins.getTime();
        return dateDiff;
    }


}
