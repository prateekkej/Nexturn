package com.nexturn.Activites;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nexturn.R;

public class SplashActivity extends AppCompatActivity {
    final int PERM_ALL = 99;
    String firstBoot;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("mySettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("firstboot")) {
            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_LONG).show();
            editor.putString("firstboot", "N");
            editor.commit();
        } else {
            firebaseAuth = FirebaseAuth.getInstance();
            setContentView(R.layout.activity_splash);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERM_ALL);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_ALL && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (firebaseAuth.getCurrentUser() == null) {
                        finish();
                        startActivity(new Intent(SplashActivity.this, LoginPage.class));
                    } else {
                        finish();
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    }
                }
            }, 1000);
        }
    }
}
