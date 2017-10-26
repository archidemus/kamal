package com.byobdev.kamal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
/**
 * Created by nano on 7/22/17.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start home activity
        if (Build.VERSION.SDK_INT < 23) {
            startActivity(new Intent(SplashActivity.this, InitiativesActivity.class));
            finish();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {

                if (getSharedPreferences("com.byobdev.kamal", MODE_PRIVATE).getBoolean("firstrun", true)) {
                    startActivity(new Intent(this, TutorialActivity.class));
                    getSharedPreferences("com.byobdev.kamal", MODE_PRIVATE).edit().putBoolean("firstrun", false).apply();
                } else
                    startActivity(new Intent(SplashActivity.this, InitiativesActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(SplashActivity.this, InitiativesActivity.class));
                    finish();
                } else {
                    finish();
                }
            }
        }
    }
}