package com.byobdev.kamal;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.byobdev.kamal.R.id.imgViewEdit;

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
    Place place;
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
    long dateDiff;
    Button fInicio, fTermino,lugar;
    final Calendar calendar2 = Calendar.getInstance();
    String url;
    MenuItem check;

    String getSector(double latitude, double longitude){
        return Integer.toString((int)(latitude*50))+","+Integer.toString((int)(longitude*50));
    }


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
                try {
                    EditActivity.this.editInitiative(item);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_initiative);
        titulo   = (EditText)findViewById(R.id.titleInput_edit);
        Intent i = getIntent();
        titulo.setText(i.getStringExtra("Titulo"));
        description   = (EditText)findViewById(R.id.descriptionInput_edit);
        description.setText(i.getStringExtra("Descripcion"));

        fInicio = (Button)findViewById(R.id.btn_fechaInicio_edit);
        fTermino = (Button)findViewById(R.id.btn_fechaTermino_edit);
        String dateF2 = mFormatter.format(new Date(Long.parseLong(i.getStringExtra("duracion"))));
        fTermino.setText(dateF2);
        String dateI2 = mFormatter.format(new Date(Long.parseLong(i.getStringExtra("hinicio"))));
        fInicio.setText(dateI2);
        url = "https://firebasestorage.googleapis.com/v0/b/prime-boulevard-168121.appspot.com/o/Images%2F"+i.getStringExtra("Imagen")+"?alt=media";
        imgView = (ImageView)findViewById(imgViewEdit);
        Picasso.with(this).load(url)
                .error(R.drawable.kamal_logo).resize(100,100).into(imgView);


        lugar = (Button)findViewById(R.id.btn_place_edit);
        lugar.setText(i.getStringExtra("Direccion"));

        //para agregar la lista de tipo de iniciativa
        spinner = (Spinner) findViewById(R.id.spinner_edit);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
    }

    //Listener boton fecha Inicio
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            if(dateDifference(fTermino.getText().toString(),date) < 0){
                fInicio.setText(mFormatter.format(date));
                fTermino.setText(mFormatter.format(date));
            }
            else {
                fInicio.setText(mFormatter.format(date));
            }
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
            if(dateDifference(fInicio.getText().toString(),date) >=0){
                fTermino.setText(fInicio.getText().toString());
            }
            else{
                fTermino.setText(mFormatter.format(date));
            }
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(EditActivity.this,
                    "Ha cancelado la selección", Toast.LENGTH_SHORT).show();
        }
    };

    public void editInitiative(MenuItem menuItem) throws ParseException {
        Date fechaPrueba = mFormatter.parse(String.format("%02d/%02d/%d %02d:%02d",calendar2.get(Calendar.DAY_OF_MONTH),calendar2.get(Calendar.MONTH)+1,calendar2.get(Calendar.YEAR),calendar2.get(Calendar.HOUR_OF_DAY),calendar2.get(Calendar.MINUTE)));
        String fechainicioprueba = fInicio.getText().toString();

        if( titulo.getText().toString().equals("")){


            titulo.setError( "Título Obligatorio!" );

        }else if(latitud == null){

            Toast.makeText(EditActivity.this, "Ubicación Obligatoria!", Toast.LENGTH_SHORT).show();
        }else if(description.getText().toString().equals("")){

            description.setError("La descripción es requerida!");

        }else if(dateDifference(fechainicioprueba,fechaPrueba) <= 0){
            Toast.makeText(this,"La fecha de inicio tiene que ser mayor a la actual",Toast.LENGTH_LONG).show();
        }else if(dateDifference(fechainicioprueba,mFormatter.parse(fTermino.getText().toString())) == 0) {
            Toast.makeText(this, "No puede crear una Iniciativa sin duración", Toast.LENGTH_LONG).show();
        }else{
            String nombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


            String fechaInit = fInicio.getText().toString();
            String fechaFin = fTermino.getText().toString();
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

            if(imagen != null){
                imageEdit = key;
            }

            Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,imageEdit ,FirebaseAuth.getInstance().getCurrentUser().getUid(),interest, direccion.toString(), feI, feT);
            mDatabase.child(getSector(latitud,longitud)).child(key).setValue(initiative);
            DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("UserInitiatives/"+FirebaseAuth.getInstance().getCurrentUser().getUid());

            userInitiatives.child(key).child("Sector").setValue(getSector(latitud,longitud));
            userInitiatives.child(key).child("Descripcion").setValue(description.getText().toString());
            userInitiatives.child(key).child("Titulo").setValue(titulo.getText().toString());
            userInitiatives.child(key).child("image").setValue(key);
            finish();
            Toast.makeText(EditActivity.this, "Iniciativa editada", Toast.LENGTH_SHORT).show();

        }
    }

    public void showDatePickerDialog(View v) throws ParseException {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(mFormatter.parse(String.format("%02d/%02d/%d %02d:%02d",calendar2.get(Calendar.DAY_OF_MONTH),calendar2.get(Calendar.MONTH)+1,calendar2.get(Calendar.YEAR),calendar2.get(Calendar.HOUR_OF_DAY),calendar2.get(Calendar.MINUTE))))
                //.setMaxDate(maxDate)
                .setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    public void showDatePickerDialog2(View v) throws ParseException {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener2)
                .setInitialDate(new Date())
                .setMinDate(mFormatter.parse(String.format("%02d/%02d/%d %02d:%02d",calendar2.get(Calendar.DAY_OF_MONTH),calendar2.get(Calendar.MONTH)+1,calendar2.get(Calendar.YEAR),calendar2.get(Calendar.HOUR_OF_DAY),calendar2.get(Calendar.MINUTE))))
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
                place = PlacePicker.getPlace(this,data);
                latitud=place.getLatLng().latitude;
                longitud=place.getLatLng().longitude;
                direccion=place.getAddress().toString();
                lugar.setText(direccion);
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

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
                        try {
                            //getting image from gallery

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                            //Setting image to ImageView
                            //Picasso.with(this).load(filePath).fit().error(R.drawable.kamal_logo).into(imgView);
                            imgView.setImageBitmap(bitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
