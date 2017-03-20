package com.nexturn.Activites;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.PopupMenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.nexturn.ModifiedViews.DatePickerFrag;
import com.nexturn.R;

import java.security.Permission;

public class Registration extends AppCompatActivity {
    final int STORAGE_PERM = 1;
    final int LOCATION_PERM = 2;
    Spinner state_list;
    ImageView userimage;
    PopupMenu pop;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.registration);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERM);

        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location me = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Toast.makeText(this, me.toString(), Toast.LENGTH_SHORT).show();
        }
        state_list = (Spinner) findViewById(R.id.state_list);
        state_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void setState_list(View v) {
        DatePickerFrag df = new DatePickerFrag();
        df.show(getSupportFragmentManager(), "datepicker");
    }

    public void setUserImage(View v) {
        pop = new PopupMenu(this, v);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERM);
        } else {
            pop.getMenuInflater().inflate(R.menu.image_upload, pop.getMenu());
            pop.show();
            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent im = new Intent();
                    im.setType("image/*");
                    im.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(im, 1);
                    return true;
                }
            });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pop.getMenuInflater().inflate(R.menu.image_upload, pop.getMenu());
                    pop.show();
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent im = new Intent();
                            im.setType("image/*");
                            im.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(im, 1);
                            return true;
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(this, "Permission was denied for STORAGE .\nCannot access media .", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case LOCATION_PERM:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location me = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Toast.makeText(this, me.toString(), Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                else {

                    Toast.makeText(this, "Permission was denied for Location .\nCannot access Location .", Toast.LENGTH_LONG).show();
                }

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            Uri imgUri = data.getData();
            Toast.makeText(getApplicationContext(), data.getData().toString(), Toast.LENGTH_LONG).show();
            userimage = (ImageView) findViewById(R.id.userImage);
            Glide.with(getApplicationContext()).load(imgUri).asBitmap().centerCrop().into(new BitmapImageViewTarget(userimage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    userimage.setImageDrawable(circularBitmapDrawable);
                    userimage.setMaxWidth(100);
                    userimage.setMaxHeight(100);
                }
            });

        }
    }
}
