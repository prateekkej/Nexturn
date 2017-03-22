package com.nexturn.Activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexturn.Fragments.Profile_Fragment;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public static User_object user_obj;
    public static ImageView user_image;
    private FragmentManager fm;
    private ListView list_drawer;
    private View nav_head;
    private String imgURL;
    private ourTextView name, email;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> abc;
    private ArrayList<String> list;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private Fragment pro, map;
    private Bitmap img;
    private Uri imgURI;
    private byte[] imgdecomp;
    private ByteArrayOutputStream byteArrayOutputStream;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_home, menu);
        return true;
    }

    @Override
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
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_obj = dataSnapshot.child(currentUser.getUid()).getValue(User_object.class);
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users_info");
        initialize_views();
        insert_data();
        fm = getSupportFragmentManager();
        getSupportActionBar().setTitle("Home");
        intialize_fragments();
        list_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        fm.beginTransaction().replace(R.id.content_frame, map).commit();
                    case 2:
                        fm.beginTransaction().replace(R.id.content_frame, pro).commit();
                        drawerLayout.closeDrawer(list_drawer);
                        break;
                    case 4:
                        invite_karo();
                        break;
                    case 5:
                        firebaseAuth.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginPage.class));
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }
        });
    }

    void intialize_fragments() {
        pro = new Profile_Fragment();
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
            img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
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
        list.add("App Settings");
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}

