package com.nexturn.Activites;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.support.v7.app.ActionBar;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexturn.DatabaseUtil;
import com.nexturn.Fragments.Profile_Fragment;
import com.nexturn.Fragments.Promesride;
import com.nexturn.Fragments.SettingsPage;
import com.nexturn.ModifiedViews.User_details_card;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_location;
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

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static User_object user_obj;
    public static ImageView nav_user_image;
    public static boolean profilePicChanged;
    public static DatabaseReference location_db;/*Firebase Pointers*/
    public static StorageReference storageReference;/*Firebase Pointers*/
    public static DatabaseReference messagesReference;
    public static FirebaseUser currentUser;                          /*Firebase Pointers*/
    public static ActionBar actionBar;
    public static String goingto;
    public static User_location user_location;
    private static SupportMapFragment mapFragment;
    private static java.util.concurrent.CopyOnWriteArrayList<User_location> list_for_added;
    final int LOCATION_PERM = 2;
    public Bitmap img;
    public byte[] imgdecomp;
    View v;
    AlertDialog goingTo;
    private FirebaseAuth firebaseAuth;                        /*Firebase Pointers*/
    private DatabaseReference databaseReference;               /*Firebase Pointers*/
    private Fragment pro;
    private FragmentManager fm;                                 /*View*/
    private ListView list_drawer;                               /*View*/
    private View nav_head;                                      /*View*/
    private ourTextView name, email;                            /*View*/
    private ActionBarDrawerToggle toggle;                       /*etc*/
    private DrawerLayout drawerLayout;                          /*View*/
    private ArrayAdapter<String> abc;
    private ArrayList<String> list;
    private AlertDialog about;
    private Uri imgURI;
    private GoogleMap googleMap;
    private LatLng myLatLng;
    private boolean isProfileON = false;
    private Location myLocation;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private ByteArrayOutputStream byteArrayOutputStream, baos;
    private Map<String, Object> loc_update;
    private User_location addedValue, removedValue, changedValue;
    private User_details_card details_card;
    private GoogleApiClient googleApiClient;
    private LocationSettingsRequest.Builder builder;
    private ChildEventListener mapListener;
    public void location_request() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY | LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                        if (!locationSettingsStates.isLocationUsable()) {
                        new AlertDialog.Builder(HomeActivity.this).setTitle("Location Services not found/enabled").setMessage("Please enable Location Services from Settings to enable full functionality of app.")
                                .setCancelable(false).setPositiveButton("Gotcha", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_PERM + 1);
                            }
                        }).show();
                        }
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
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (User_location u : list_for_added) {
                    Log.d("Marker ID:", u.marker.getId() + marker.getId());
                    if (u.marker.getId().equals(marker.getId())) {
                        details_card = new User_details_card();
                        details_card.setUserObject(u);
                        details_card.show(fm, u.name);
                        break;
                    }
                }
                return true;
            }
        });
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (myLatLng != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
                }
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
                list_for_added.clear();
                location_db.child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Removed your Location from Server.", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error in communication.\nYour recent location will automatically be removed after 10 minutes.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                finish();
                break;
            case 16908332:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.goingButton:
                Button cancel = (Button) v.findViewById(R.id.cancelGoingTo);
                Button update = (Button) v.findViewById(R.id.updateGoingTo);
                Button reset = (Button) v.findViewById(R.id.resetGoingTo);
                goingTo.show();
                SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                        getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        Log.i("", "Place: " + place.getName());
                        goingto = place.getAddress().toString();

                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i("", "An error occurred: " + status);
                    }
                });
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> goingto_update = new HashMap<String, Object>();
                        goingto_update.put("goingTo", goingto);
                        location_db.child(currentUser.getUid()).updateChildren(goingto_update).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), goingto, Toast.LENGTH_SHORT).show();
                                goingTo.dismiss();
                            }
                        });
                    }
                });
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> goingto_update = new HashMap<String, Object>();
                        goingto_update.put("goingTo", "");
                        location_db.child(currentUser.getUid()).updateChildren(goingto_update).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Reset Successful", Toast.LENGTH_LONG).show();
                                goingTo.dismiss();
                            }
                        });
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goingTo.dismiss();
                    }
                });


                break;
        }
        return true;
    }
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_obj = dataSnapshot.getValue(User_object.class);
                if (user_obj != null) {
                    name.setText(user_obj.fname + " " + user_obj.lname);
                    email.setText(user_obj.email);
                    if (user_obj.imgURL != null) {
                        Glide.with(getApplicationContext()).load(user_obj.imgURL).asBitmap().centerCrop().into(new BitmapImageViewTarget(nav_user_image) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                nav_user_image.setImageDrawable(circularBitmapDrawable);
                            }
                        });

                    }
                    Log.v("LLLL", user_obj.toString());
                    mapKaKaam();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
    void deleteEntry(User_location deletedEntry) {
        if (deletedEntry.marker != null) {
            deletedEntry.marker.remove();
        }
    }
    void updateMap(User_location newLocation) {
        if (newLocation.marker != null) {
            newLocation.marker.setPosition(newLocation.getLatLng());
        }
    }
    void addEntryToMap(User_location u) {
        u.marker = googleMap.addMarker(new MarkerOptions().position(u.getLatLng()).title(u.goingTo));
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profilePicChanged = false;
        list_for_added = new java.util.concurrent.CopyOnWriteArrayList<User_location>();
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
        View x = getLayoutInflater().inflate(R.layout.about_layout, null);
        about = new AlertDialog.Builder(this).setView(x).create();
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
                        about.show();
                        break;
                    case 5:
                        signOut();
                        break;
                }
            }
        });

    }
    void fragments_initialize() {
        fm = getSupportFragmentManager();
        pro = new Promesride();
        mapFragment = new SupportMapFragment();
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
            if (result != null) {
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
                    Map<String, Object> updateImage = new HashMap<String, Object>();
                    updateImage.put("imgURL", user_obj.imgURL);
                    databaseReference.child(currentUser.getUid()).updateChildren(updateImage);
                    profilePicChanged = true;
                    Glide.with(HomeActivity.this).load(imgdecomp).asBitmap().centerCrop().into(new BitmapImageViewTarget(nav_user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            nav_user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    Glide.with(HomeActivity.this).load(imgdecomp).asBitmap().centerCrop().into(new BitmapImageViewTarget(Profile_Fragment.user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            Profile_Fragment.user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Profile_Fragment.user_image.setImageResource(R.drawable.ic_person_outline_black_24dp);
                    nav_user_image.setImageResource(R.drawable.ic_person_outline_black_24dp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Image Upload failed. Try Again", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });
            }
        }
    }
    public void invite_karo() {
        if (myLocation != null) {
        Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, "Hey!! \n" + user_obj.fname + " " + user_obj.lname + " location is :" +
                    "http://maps.google.com/maps?daddr=" + user_location.getLat() + "," + user_location.getLong());
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, "Share app to..."));
        }
    }
    void insert_data() {
        list.add("Home");
        list.add("Profile View");
        list.add("Share my location");
        list.add("About");
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
                invalidateOptionsMenu();
            }
        };
        v = getLayoutInflater().inflate(R.layout.goingto_layout, null);
        goingTo = new AlertDialog.Builder(this).setView(v).create();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        list_drawer = (ListView) findViewById(R.id.left_drawer);
        list = new ArrayList<>();
        abc = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        nav_head = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        name = (ourTextView) nav_head.findViewById(R.id.username_nav);
        email = (ourTextView) nav_head.findViewById(R.id.email_nav);
        nav_user_image = (ImageView) nav_head.findViewById(R.id.userimage_nav);
        actionBar = getSupportActionBar();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Home");

    }
    void firebase_init() {
        databaseReference = DatabaseUtil.getDatabase().getReference("users_info");
        messagesReference = DatabaseUtil.getDatabase().getReference("users_messages");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        location_db = FirebaseDatabase.getInstance().getReference("users_recent_location");
        location_db.keepSynced(true);
        define_maplis();
    }
    void define_maplis() {
        mapListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addedValue = dataSnapshot.getValue(User_location.class);
                if (addedValue.uid.equals(currentUser.getUid())) {
                    Log.d("added", addedValue.uid + "  " + currentUser.getUid());
                } else if (addedValue.uid.equals("")) {
                    Log.d("del", "Orphan Deleted.");
                    location_db.child(dataSnapshot.getKey()).removeValue();
                } else {
                    Log.d("addedNew", addedValue.uid + " N/O " + currentUser.getUid());
                    User_location temp = new User_location(addedValue);
                    addEntryToMap(temp);
                    list_for_added.add(temp);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                changedValue = dataSnapshot.getValue(User_location.class);
                if (changedValue.getUid().equals(currentUser.getUid())) {
                    Log.d("Changed:", changedValue.toString());
                } else {
                    for (User_location u : list_for_added) {
                        if (u.getUid().equals(changedValue.getUid())) {
                            Log.d("ChangedLast:", changedValue.toString());
                            u.copyAll(changedValue);
                            updateMap(u);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removedValue = dataSnapshot.getValue(User_location.class);
                if (removedValue.uid.equals(currentUser.getUid())) {
                    Log.d("Removed:", removedValue.toString());
                } else {
                    for (User_location u : list_for_added) {
                        if (u.uid.equals(removedValue.uid)) {
                            Log.d("RemovedLast:", removedValue.toString());
                            deleteEntry(u);
                            list_for_added.remove(u);
                        }
                    }

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }
    void updaterforSearchArea(Location location) {
        location_db.orderByChild("lat").startAt(location.getLatitude() - 0.04).endAt(location.getLatitude() + 0.04)
                .addChildEventListener(mapListener);
    }
    void mapKaKaam() {
        loc_update = new HashMap<String, Object>();
        if (myLocation == null) {
            Log.v("fff", "Location is null");
        }
        if (myLocation != null && user_obj != null) {
            user_location = new User_location(user_obj.uid, user_obj.fname + " " + user_obj.lname,
                    myLocation.getLatitude(), myLocation.getLongitude(), user_obj.imgURL, user_obj.mobile, user_obj.fblink, user_obj.phoneVisible, user_obj.em, user_obj.gg, user_obj.fb);
            location_db.child(user_obj.uid).setValue(user_location);
            updaterforSearchArea(myLocation);
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    user_location.lat = location.getLatitude();
                    user_location.lon = location.getLongitude();
                    loc_update.put("lat", user_location.getLat());
                    loc_update.put("lon", user_location.getLong());
                    location_db.child(currentUser.getUid()).updateChildren(loc_update);
                }
            });
        }
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
            mapKaKaam();
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Please use Exit button to exit.", Toast.LENGTH_LONG).show();
        if (isProfileON) {
            pro.onResume();
        }
    }
    void signOut() {//DO NOT MODIFY THIS SEQUENCE EVER.
        location_db.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
        LoginManager.getInstance().logOut();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginPage.class));
    }
}

