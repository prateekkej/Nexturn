package com.nexturn.Activites;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexturn.DatabaseUtil;
import com.nexturn.Fragments.Profile_Fragment;
import com.nexturn.Fragments.SettingsPage;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener {
    public static User_object user_obj;
    public static ImageView user_image;
    private static SupportMapFragment mapFragment;
    final int LOCATION_PERM = 2;
    List<User_location> list_for_added;
    private FirebaseAuth firebaseAuth;                        /*Firebase Pointers*/
    private DatabaseReference databaseReference;               /*Firebase Pointers*/
    private DatabaseReference location_db;                     /*Firebase Pointers*/
    private StorageReference storageReference;                 /*Firebase Pointers*/
    private FirebaseUser currentUser;                          /*Firebase Pointers*/
    private Fragment pro, settings;
    private FragmentManager fm;                                 /*View*/
    private ListView list_drawer;                               /*View*/
    private View nav_head;                                      /*View*/
    private ourTextView name, email;                            /*View*/
    private ActionBarDrawerToggle toggle;                       /*etc*/
    private DrawerLayout drawerLayout;                          /*View*/
    private ArrayAdapter<String> abc;
    private ArrayList<String> list;
    private User_location user_location;
    private Bitmap img;
    private Uri imgURI;
    private GoogleMap googleMap;
    private byte[] imgdecomp;
    private LatLng myLatLng;
    private boolean isProfileON = false;
    private Location myLocation;
    private LocationRequest locationRequest;
    private Marker myMarker;
    private LocationManager locationManager;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Map<String, Object> loc_update;
    private User_location addedValue;
    private GoogleApiClient googleApiClient;
    private LocationSettingsRequest.Builder builder;
    public void location_request() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        new AlertDialog.Builder(HomeActivity.this).setTitle("Location Services not found/enabled").setMessage("Please enable Location Services from Settings to enable full functionality of app.")
                                .setCancelable(false).setPositiveButton("Gotcha", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_PERM + 1);
                            }
                        }).show();
                        break;
                }
            }
        });
    }
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException e) {
        }
    }
    public void onConnectionSuspended(int i) {

    }
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (myLatLng != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
        }
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        googleMap.setTrafficEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return true;
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_home, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                break;
            case 16908332:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
        }
        return true;
    }
    protected void onResume() {
        super.onResume();
        loc_update = new HashMap<String, Object>();
        databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_obj = dataSnapshot.getValue(User_object.class);
                name.setText(user_obj.fname + " " + user_obj.lname);
                email.setText(user_obj.email);
                if (user_obj.imgURL != null) {
                    Glide.with(getApplicationContext()).load(user_obj.imgURL).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    Glide.with(getApplicationContext()).load(user_obj.imgURL).asBitmap().centerCrop().into(new BitmapImageViewTarget(HomeActivity.user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }
                if (myLocation != null) {
                    user_location = new User_location(user_obj.uid, user_obj.fname + " " + user_obj.lname, myLocation.getLatitude(), myLocation.getLongitude(), user_obj.imgURL);
                    location_db.child(user_obj.uid).setValue(user_location);
                    location_db.orderByChild("lat").startAt(myLocation.getLatitude() - 0.04).endAt(myLocation.getLatitude() + 0.04)
                            .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.getValue(User_location.class).uid == user_location.uid) {
                            } else {
                                addedValue = dataSnapshot.getValue(User_location.class);
                                addEntryToMap(addedValue);
                                list_for_added.add(addedValue);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            User_location changedObject = dataSnapshot.getValue(User_location.class);
                            Log.v("ffff", dataSnapshot.toString());
                            if (changedObject.uid == user_location.uid) {
                            } else {
                                User_location temp = new User_location();
                                for (User_location u : list_for_added) {
                                    if (u.uid == changedObject.uid) {
                                        u.lon = changedObject.lon;
                                        u.lat = changedObject.lat;
                                        temp = u;
                                    }
                                }
                                updateMap(temp);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            User_location changedObject = dataSnapshot.getValue(User_location.class);
                            Log.v("ffff", dataSnapshot.toString());
                            if (changedObject.uid == user_location.uid) {
                            } else {
                                User_location temp = new User_location();
                                for (User_location u : list_for_added) {
                                    if (u.uid == changedObject.uid) {
                                        u.lon = changedObject.lon;
                                        u.lat = changedObject.lat;
                                        temp = u;
                                    }
                                }
                                deleteEntry(temp);
                                list_for_added.remove(temp);
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            user_location.lat = location.getLatitude();
                            user_location.lon = location.getLongitude();
                            loc_update.put("lat", user_location.getLat());
                            loc_update.put("lon", user_location.getLong());
                            location_db.child(user_location.uid).updateChildren(loc_update);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    protected void onStop() {
        super.onStop();
        location_db.child(currentUser.getUid()).removeValue();
        googleApiClient.disconnect();
    }
    void deleteEntry(User_location deletedEntry) {
        deletedEntry.marker.remove();

    }
    void updateMap(User_location newLocation) {
        newLocation.marker.setPosition(newLocation.getLatLng());
    }
    void addEntryToMap(User_location u) {
        u.marker = googleMap.addMarker(new MarkerOptions().position(u.getLatLng()).title(u.name));
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        list_for_added = new ArrayList<User_location>();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        location_request();
        firebase_init();
        initialize_views();
        insert_data();
        fragments_initialize();

        fm.beginTransaction().add(R.id.content_frame, mapFragment, "map").commit();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERM);
        } else {
            myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            mapFragment.getMapAsync(this);
        }
        getSupportActionBar().setTitle("Home");
        list_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        if (isProfileON) {
                            if (fm.findFragmentByTag("map") != null) {
                                fm.popBackStack();
                            } else {
                                fm.beginTransaction().replace(R.id.content_frame, mapFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                mapFragment.getMapAsync(HomeActivity.this);
                            }
                            isProfileON = false;
                        }
                        drawerLayout.closeDrawer(list_drawer);
                        break;
                    case 2:
                        if (!isProfileON) {
                            if (fm.findFragmentByTag("profile") != null) {
                                fm.popBackStack();
                            }
                            fm.beginTransaction().replace(R.id.content_frame, pro, "profile").addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                            isProfileON = true;
                        }
                        drawerLayout.closeDrawer(list_drawer);
                        break;
                    case 3:
                        invite_karo();
                        break;
                    case 4:
                        location_db.child(currentUser.getUid()).removeValue();
                        firebaseAuth.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginPage.class));
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }
        });
    }
    void fragments_initialize() {
        fm = getSupportFragmentManager();
        pro = new Profile_Fragment();
        mapFragment = new SupportMapFragment();
        settings = new SettingsPage();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            imgURI = CropImage.getPickImageResultUri(this, data);
            CropImage.activity(imgURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true).setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteArrayOutputStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            imgdecomp = byteArrayOutputStream.toByteArray();
            UploadTask imgupload = storageReference.child("user-images/" + firebaseAuth.getCurrentUser().getUid()).putBytes(imgdecomp);
            imgupload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(HomeActivity.this, "Image Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    user_obj.imgURL = taskSnapshot.getDownloadUrl().toString();
                    databaseReference.child(currentUser.getUid()).setValue(user_obj);
                    Glide.with(HomeActivity.this).load(result.getUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    Glide.with(HomeActivity.this).load(result.getUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(HomeActivity.user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    pro.onResume();

                }
            });
        }
    }
    public void invite_karo() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, "Hey!! Check out this new App!!\n \n" + "link");
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, "Share app to..."));
    }
    void insert_data() {
        list.add("Home");
        list.add("Profile View");
        list.add("Share");
        list.add("Log out");
        list_drawer.addHeaderView(nav_head);
        list_drawer.setAdapter(abc);
        drawerLayout.setDrawerListener(toggle);
    }
    void initialize_views() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Nexturn");
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle("Home");
                invalidateOptionsMenu();
            }
        };
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        list_drawer = (ListView) findViewById(R.id.left_drawer);
        list = new ArrayList<>();
        abc = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        nav_head = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        name = (ourTextView) nav_head.findViewById(R.id.username_nav);
        email = (ourTextView) nav_head.findViewById(R.id.email_nav);
        user_image = (ImageView) nav_head.findViewById(R.id.userimage_nav);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    void firebase_init() {
        databaseReference = DatabaseUtil.getDatabase().getReference("users_info");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        location_db = FirebaseDatabase.getInstance().getReference("users_recent_location");
        location_db.keepSynced(true);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM && (grantResults[0] == -1 || grantResults[1] == -1)) {
            Toast.makeText(getApplicationContext(), permissions.toString(), Toast.LENGTH_SHORT).show();
            AlertDialog ad = new AlertDialog.Builder(this).setTitle("Location Permission Required").setMessage("This app requires Location services" +
                    " to work at its heart.Without Location permissions,we wont be able to help.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setCancelable(false).show();
        }
    }
    public void onLocationChanged(Location location) {
        if (myLocation == null) {
            myLocation = location;
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapFragment.getMapAsync(this);
            this.onResume();
        }
    }
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
class User_location extends User_object {
    public String uid, name, imgURL;
    public double lat, lon;
    public Marker marker;

    User_location() {
        uid = "";
        lat = 0;
        lon = 0;
        name = "";
        imgURL = "";
        marker = null;
    }

    User_location(String a, String b, double c, double d, String e) {
        uid = a;
        name = b;
        lat = c;
        lon = d;
        imgURL = e;
    }

    double getLong() {
        return lon;
    }

    double getLat() {
        return lat;
    }

    LatLng getLatLng() {
        return new LatLng(lat, lon);
    }

    @Override
    public String toString() {
        return name + " " + uid;
    }
}

