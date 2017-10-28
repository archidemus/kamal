package com.byobdev.kamal;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.string.no;
import static com.byobdev.kamal.R.id.imgViewEdit;
import static com.byobdev.kamal.R.id.none;

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
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath;
    String direccion;
    int PLACE_PICKER_REQUEST = 1;
    ImageView imgView;
    String key;
    String imageEdit;
    String IDanterior;
    Date dateInits, dateFins;
    long dateDiff;
    Button fInicio, fTermino, lugar, imagenEdit;
    final Calendar calendar2 = Calendar.getInstance();
    String url;
    MenuItem check;
    String estado;
    Date fechaPrueba = null;
    String date;
    private LocationManager locationManager;
    Location mLocation;

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
        titulo = (EditText) findViewById(R.id.titleInput_edit);
        Intent i = getIntent();
        titulo.setText(i.getStringExtra("Titulo"));
        description = (EditText) findViewById(R.id.descriptionInput_edit);
        description.setText(i.getStringExtra("Descripcion"));

        fInicio = (Button) findViewById(R.id.btn_fechaInicio_edit);
        fTermino = (Button) findViewById(R.id.btn_fechaTermino_edit);
        String dateF2 = mFormatter.format(new Date(Long.parseLong(i.getStringExtra("duracion"))));
        fTermino.setText(dateF2);
        String dateI2 = mFormatter.format(new Date(Long.parseLong(i.getStringExtra("hinicio"))));
        fInicio.setText(dateI2);
        url = "https://firebasestorage.googleapis.com/v0/b/prime-boulevard-168121.appspot.com/o/Images%2F" + i.getStringExtra("Imagen") + "?alt=media";
        imgView = (ImageView) findViewById(R.id.imgViewEdit);
        Picasso.with(this).load(url)
                .error(R.drawable.kamal_logo)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imgView);

        imagenEdit = (Button) findViewById(R.id.elegirImagenEdit);

        lugar = (Button) findViewById(R.id.btn_place_edit);
        lugar.setText(i.getStringExtra("Direccion"));
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        //para agregar la lista de tipo de iniciativa
        spinner = (Spinner) findViewById(R.id.spinner_edit);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipo_Iniciativa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (i.getStringExtra("Tipo").equals("Teatro")) {
            spinner.setSelection(0);
        } else if (i.getStringExtra("Tipo").equals("Musica")) {
            spinner.setSelection(1);
        } else if (i.getStringExtra("Tipo").equals("Deporte")) {
            spinner.setSelection(2);
        } else {
            spinner.setSelection(3);
        }

        longitud = Double.parseDouble(i.getStringExtra("Longitud"));
        latitud = Double.parseDouble(i.getStringExtra("Latitud"));
        direccion = i.getStringExtra("Direccion");
        imageEdit = i.getStringExtra("Imagen");
        IDanterior = i.getStringExtra("IDanterior");

        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives");
        key = IDanterior;
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando....");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);

        estado = i.getStringExtra("Estado");

        if (estado.equals("1") || estado.equals("2")) {
            titulo.setEnabled(false);
            titulo.setInputType(InputType.TYPE_NULL);
            lugar.setEnabled(false);
            spinner.setEnabled(false);
            fInicio.setEnabled(false);
            imagenEdit.setEnabled(false);
            if (estado.equals("2")){
                fTermino.setEnabled(false);
            }
        }
    }

    //Listener boton fecha Inicio
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            if (dateDifference(fTermino.getText().toString(), date) < 0) {
                fInicio.setText(mFormatter.format(date));
                fTermino.setText(mFormatter.format(date));
            } else {
                fInicio.setText(mFormatter.format(date));
            }
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
            Toast.makeText(EditActivity.this,
                    "Ha cancelado la selección", Toast.LENGTH_SHORT).show();
        }
    };

    //Listener boton fecha Termino
    private SlideDateTimeListener listener2 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            if (dateDifference(fInicio.getText().toString(), date) >= 0) {
                fTermino.setText(fInicio.getText().toString());
            } else {
                fTermino.setText(mFormatter.format(date));
            }
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
            Toast.makeText(EditActivity.this,
                    "Ha cancelado la selección", Toast.LENGTH_SHORT).show();
        }
    };

    public void editInitiative(MenuItem menuItem) throws ParseException {
        String fechainicioprueba = fInicio.getText().toString();


        setFechaPrueba();


        if( titulo.getText().toString().equals("")){
            titulo.setError( "Título Obligatorio!" );

        }else if(latitud == null){

            Toast.makeText(EditActivity.this, "Ubicación Obligatoria!", Toast.LENGTH_SHORT).show();
        }else if(description.getText().toString().equals("")){

            description.setError("La descripción es requerida!");

        }else if((estado.equals("0") || estado.equals("3")) && (dateDifference(fechainicioprueba,fechaPrueba) <= 0)){  //Veo si está agendada o terminada

                Toast.makeText(this, "La fecha de inicio tiene que ser mayor a la actual", Toast.LENGTH_LONG).show();

        }else if(dateDifference(fechainicioprueba,mFormatter.parse(fTermino.getText().toString())) == 0) {
            Toast.makeText(this, "No puede crear una Iniciativa sin duración", Toast.LENGTH_LONG).show();
        }else if(lugar.getText().toString().isEmpty()){
            Toast.makeText(this, "Se necesita de una dirección", Toast.LENGTH_LONG).show();
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




            Initiative initiative = new Initiative(titulo.getText().toString(), nombre, description.getText().toString(), latitud, longitud, imageEdit, FirebaseAuth.getInstance().getCurrentUser().getUid(), interest, direccion.toString(), feI, feT, setEstado(estado));
            mDatabase.child(getSector(latitud,longitud)).child(key).setValue(initiative);
            DatabaseReference userInitiatives = FirebaseDatabase.getInstance().getReference("UserInitiatives/"+FirebaseAuth.getInstance().getCurrentUser().getUid());

            userInitiatives.child(key).child("Sector").setValue(getSector(latitud,longitud));
            userInitiatives.child(key).child("Descripcion").setValue(description.getText().toString());
            userInitiatives.child(key).child("Titulo").setValue(titulo.getText().toString());
            userInitiatives.child(key).child("image").setValue(key);

            if(filePath==null){
                Toast.makeText(EditActivity.this, "Iniciativa editada", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                StorageReference childRef = storageRef.child(key);
                //uploading the image
                final UploadTask uploadTask = childRef.putFile(filePath);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(EditActivity.this, "Iniciativa editada", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditActivity.this, "Error en la subida -> " + e, Toast.LENGTH_SHORT).show();
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
        catch (Exception e){

        }
    }

    public void escogerImagen(View v){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locListener, null);
            if (mLocation != null) {
                date = mFormatter.format((new Date(mLocation.getTime())));
                fechaPrueba = mFormatter.parse(date);

            } else {
                fechaPrueba = mFormatter.parse(String.format("%02d/%02d/%d %02d:%02d", calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.MONTH) + 1, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE)));
            }
        }catch ( SecurityException e ) { e.printStackTrace(); }


    }

    private int setEstado(String estado){
        if (estado.equals("1") || estado.equals("2")){
            return Integer.parseInt(estado);
        }
        else {
            return 0;
        }
    }

}
