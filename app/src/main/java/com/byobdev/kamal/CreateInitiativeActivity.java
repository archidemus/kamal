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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.ProgressDialog;
import android.net.Uri;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;


public class CreateInitiativeActivity extends AppCompatActivity{
    EditText titulo;
    EditText description;
    Double latitud;
    Double longitud;
    String imagen;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStoragebase = FirebaseStorage.getInstance();
    StorageReference storageRef = mStoragebase.getReferenceFromUrl("gs://prime-boulevard-168121.appspot.com/Images");
    ProgressDialog pd;
    Uri filePath;
    int PICK_IMAGE_REQUEST = 111;
    ImageView imgView;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_initiative);
        titulo   = (EditText)findViewById(R.id.titleInput);
        description   = (EditText)findViewById(R.id.descriptionInput);
        mDatabase = FirebaseDatabase.getInstance().getReference("Initiatives");
        key=mDatabase.push().getKey();
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
    }

    public void createInitiative(View view){
        String nombre = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Initiative initiative=new Initiative(titulo.getText().toString(), nombre, description.getText().toString(),latitud,longitud,key ,FirebaseAuth.getInstance().getCurrentUser().getUid(),"Arte");
        mDatabase.child(key).setValue(initiative);
        finish();
    }

    public void obtenerGPS(View view){
        LocationGPS gps=new LocationGPS(this);
        latitud = gps.getLatitud();
        longitud = gps.getLongitud();
        Toast.makeText(CreateInitiativeActivity.this, "Posicion obtenida", Toast.LENGTH_SHORT).show();
    }

    public void escogerImagen(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }
    public void subirImagen(View v){
        if(filePath != null) {
            pd.show();

            StorageReference childRef = storageRef.child(key);

            //uploading the image
            final UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(CreateInitiativeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    imagen = uploadTask.getSnapshot().getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(CreateInitiativeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    imagen = null;
                }
            });
        }
        else {
            Toast.makeText(CreateInitiativeActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }

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
    }

}
