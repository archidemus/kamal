package com.byobdev.kamal;


import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.byobdev.kamal.DBClasses.Initiative;
import com.byobdev.kamal.AppHelpers.LocationGPS;
import com.facebook.places.PlaceManager;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateInitiativeActivity extends AppCompatActivity{
    EditText titulo;
    EditText description;
    TextView hInicio, hTermino;
    Double latitud;
    Double longitud;
    String imagen;
    Spinner spinner;
    Button button;
    ArrayAdapter<CharSequence> adapter;
    Place place;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath;
    String direccion;
    int PLACE_PICKER_REQUEST = 1;
    int PICK_IMAGE_REQUEST = 111;
    ImageView imgView;
    String key;
    final Calendar calendar = Calendar.getInstance();
    Date dateInits, dateFins;
    TextView fechaInicio;
    TextView fechaTermino;

    String getSector(double latitude, double longitude){
        return Integer.toString((int)(latitude*100))+","+Integer.toString((int)(longitude*100));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_initiative);
        titulo   = (EditText)findViewById(R.id.titleInput);
        description   = (EditText)findViewById(R.id.descriptionInput);
        hTermino = (TextView)findViewById(R.id.HoraFinalfinal);
        hInicio = (TextView)findViewById(R.id.HoraIniciofinal);
        fechaTermino = (TextView)findViewById(R.id.txt_fecha_termino_vista);
        fechaInicio = (TextView)findViewById(R.id.txt_fecha_inicio_vista);


        hInicio.setText(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        hTermino.setText(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        fechaInicio.setText(String.format("%02d/%02d/%d",calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)));
        fechaTermino.setText(String.format("%02d/%02d/%d",calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)));

        //para agregar la lista de tipo de iniciativa
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipo_Iniciativa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives/");
        key=mDatabase.push().getKey();
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando...");

    }

    public void createInitiative(View view){
        if( titulo.getText().toString().equals("")){


            titulo.setError( "Título Obligatorio" );

        }else if(latitud == null){

            Toast.makeText(CreateInitiativeActivity.this, "Ubicación Obligatoria", Toast.LENGTH_SHORT).show();
        }else if(description.getText().toString().equals("")){

            description.setError("La descripción es requerida!");

        }
        else{
            String nombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            DateFormat formatter = new SimpleDateFormat("HH:mm");
            DateFormat formatterF = new SimpleDateFormat("HH:mm");
            Date date =null;
            Date date1 = null;
            try{
                date = formatter.parse(hInicio.getText().toString());
                 date1= formatterF.parse(hTermino.getText().toString());
            }catch (Exception e){

            }

            String fechaInit = fechaInicio.getText().toString();
            String fechaFin = fechaTermino.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                dateInits = simpleDateFormat.parse(fechaInit);
                dateFins = simpleDateFormat.parse(fechaFin);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //String feI = formatter.format(date);
            //String feT = formatterF.format(date1);
            long feI=dateInits.getTime()+date.getTime();
            long feT=dateFins.getTime()+date1.getTime();
            String interest = spinner.getSelectedItem().toString();
            if (interest.equals("Música")){
                interest = "Musica";
            }
            Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,key ,FirebaseAuth.getInstance().getCurrentUser().getUid(),interest, direccion.toString(), feI, feT);
            mDatabase.child(getSector(latitud,longitud)).child(key).setValue(initiative);
            DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("UserInitiatives/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            userInitiatives.child(key).child("Sector").setValue(getSector(latitud,longitud));
            userInitiatives.child(key).child("Titulo").setValue(titulo.getText().toString());
            finish();
        }

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = HoraActivity.newInstance(v.getId());

        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTimePickerDialog2(View v) {

            DialogFragment newFragment = HoraActivity.newInstance(v.getId());
            newFragment.show(getFragmentManager(), "timePicker2");

    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = FechaActivity.newInstance(v.getId());

        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showDatePickerDialog2(View v) {
       /* if (fInicio.getText().toString().equals("--/--/--")){
            Toast.makeText(this,"No ha seleccionado una fecha de inicio",Toast.LENGTH_LONG).show();
        }*/
            DialogFragment newFragment = FechaActivity.newInstance(v.getId());
            newFragment.show(getFragmentManager(), "datePicker2");

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



}
