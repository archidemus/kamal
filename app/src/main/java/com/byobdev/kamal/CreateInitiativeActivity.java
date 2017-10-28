package com.byobdev.kamal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.byobdev.kamal.DBClasses.Comment;
import com.byobdev.kamal.DBClasses.Initiative;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.widget.EditText;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.security.AccessController.getContext;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CreateInitiativeActivity extends AppCompatActivity {
    EditText titulo;
    EditText description;
    Double latitud;
    Double longitud;
    String imagen;
    Spinner spinner;
    Location mLocation;
    ArrayAdapter<CharSequence> adapter;
    Place place;
    MenuItem check;
    Date fechaPrueba;

    private LocationManager locationManager;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    long dateDiff;
    String date;

    private DatabaseReference mDatabase;
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath = null;
    String direccion;
    int PLACE_PICKER_REQUEST = 1;
    int PICK_IMAGE_REQUEST = 111;
    ImageView imgView;
    String key, key2;
    final Calendar calendar2 = Calendar.getInstance();

    Date dateInits, dateFins;
    Button fechaInicio, fechaTermino, lugarIniciativa;


    String getSector(double latitude, double longitude) {
        return Integer.toString((int) (latitude * 50)) + "," + Integer.toString((int) (longitude * 50));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_config, menu);
        check = menu.findItem(R.id.done);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        check.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    CreateInitiativeActivity.this.createInitiative(item);
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
        setContentView(R.layout.activity_create_initiative);
        titulo = (EditText) findViewById(R.id.titleInput);
        description = (EditText) findViewById(R.id.descriptionInput);
        fechaTermino = (Button) findViewById(R.id.btn_fechaTermino);
        fechaInicio = (Button) findViewById(R.id.btn_fechaInicio);
        lugarIniciativa = (Button) findViewById(R.id.button5);
        imgView = (ImageView) findViewById(R.id.imgView);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (mLocation != null) {
                date = mFormatter.format((new Date(mLocation.getTime())));
                fechaInicio.setText(date);
                fechaTermino.setText(date);

            } else {
                fechaInicio.setText(String.format("%02d/%02d/%d %02d:%02d", calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.MONTH) + 1, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));
                fechaTermino.setText(String.format("%02d/%02d/%d %02d:%02d", calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.MONTH) + 1, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));

            }

        } else {
            fechaInicio.setText(String.format("%02d/%02d/%d %02d:%02d", calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.MONTH) + 1, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));
            fechaTermino.setText(String.format("%02d/%02d/%d %02d:%02d", calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.MONTH) + 1, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));

        }


        //para agregar la lista de tipo de iniciativa
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipo_Iniciativa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives/");
        key = mDatabase.push().getKey();
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    //Listener boton fecha Inicio
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            if (dateDifference(fechaTermino.getText().toString(), date) < 0) {
                fechaInicio.setText(mFormatter.format(date));
                fechaTermino.setText(mFormatter.format(date));

            } else {
                fechaInicio.setText(mFormatter.format(date));
            }

        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    //Listener boton fecha Termino
    private SlideDateTimeListener listener2 = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            if (dateDifference(fechaInicio.getText().toString(), date) >= 0) {
                fechaTermino.setText(fechaInicio.getText().toString());
            } else {
                fechaTermino.setText(mFormatter.format(date));
            }

        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    public void createInitiative(MenuItem menuItem) throws ParseException {
        String fechainicioprueba = fechaInicio.getText().toString();
        setFechaPrueba();

        if( titulo.getText().toString().equals("")){
            titulo.setError( "Título Obligatorio" );

        }else if(latitud == null){
            Toast.makeText(CreateInitiativeActivity.this, "Ubicación Obligatoria", Toast.LENGTH_SHORT).show();
        }else if(description.getText().toString().equals("")){
            description.setError("La descripción es requerida!");
        }else if(dateDifference(fechainicioprueba,fechaPrueba) < 0){
            Toast.makeText(this,"No puede crear una Iniciativa antes de la fecha actual",Toast.LENGTH_LONG).show();
        }else if(dateDifference(fechainicioprueba,mFormatter.parse(fechaTermino.getText().toString())) == 0) {
            Toast.makeText(this, "No puede crear una Iniciativa sin duración", Toast.LENGTH_LONG).show();
        }else{

            String nombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String fechaInit = fechaInicio.getText().toString();
            String fechaFin = fechaTermino.getText().toString();
            try {
                dateInits = mFormatter.parse(fechaInit);
                dateFins = mFormatter.parse(fechaFin);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //String feI = formatter.format(date);
            //String feT = formatterF.format(date1);
            long feI=dateInits.getTime();
            long feT=dateFins.getTime();
            String interest = spinner.getSelectedItem().toString();
            if (interest.equals("Música")){
                interest = "Musica";
            }
            Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,key ,FirebaseAuth.getInstance().getCurrentUser().getUid(),interest, direccion.toString(), feI, feT);
            mDatabase.child(getSector(latitud,longitud)).child(key).setValue(initiative);

            DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("UserInitiatives/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            userInitiatives.child(key).child("Sector").setValue(getSector(latitud,longitud));
            userInitiatives.child(key).child("Descripcion").setValue(description.getText().toString());
            userInitiatives.child(key).child("Titulo").setValue(titulo.getText().toString());
            userInitiatives.child(key).child("image").setValue(key);

            key2=mDatabase.push().getKey();
            DatabaseReference comments = FirebaseDatabase.getInstance().getReference("Comments/");
            Comment comment = new Comment(nombre, key, "Creador","");
            comments.child(key).child(key2).setValue(comment);

            if(filePath==null){
                Toast.makeText(CreateInitiativeActivity.this, "Iniciativa Creada", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                StorageReference childRef = storageRef.child(key);
                //uploading the image
                final UploadTask uploadTask = childRef.putFile(filePath);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(CreateInitiativeActivity.this, "Iniciativa Creada", Toast.LENGTH_SHORT).show();
                        imagen = uploadTask.getSnapshot().getDownloadUrl().toString();
                        finish();
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
                        Toast.makeText(CreateInitiativeActivity.this, "Error en la subida -> " + e, Toast.LENGTH_SHORT).show();
                        imagen = null;
                    }
                });
            }


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
        catch (Exception e) {
        }
    }

    public void escogerImagen(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(400,200)
                    .setFixAspectRatio(true)
                    .setRequestedSize(400,200)
                    .start(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Seleccione imagen"), PICK_IMAGE_REQUEST);
                } else {
                    finish();
                }
            }
        }
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
                lugarIniciativa.setText(direccion);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                imgView.setImageURI(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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

    LocationListener locListener = new LocationListener()
    {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onLocationChanged(Location location)
        {
            // TODO Auto-generated method stub
            mLocation = location;
        }
    };


    private void setFechaPrueba() throws ParseException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // t o handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestSingleUpdate( LocationManager.NETWORK_PROVIDER, locListener, null );
            if (mLocation!= null) {
                date = mFormatter.format((new Date(mLocation.getTime())));
                fechaPrueba = mFormatter.parse(date);
            }
            else{
                fechaPrueba = mFormatter.parse(String.format("%02d/%02d/%d %02d:%02d", calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.MONTH) + 1, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));
            }
        } catch ( SecurityException e ) { e.printStackTrace(); }


    }
}
