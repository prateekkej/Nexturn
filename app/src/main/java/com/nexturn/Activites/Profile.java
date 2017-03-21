package com.nexturn.Activites;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexturn.Fragments.Profile_view;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    public static User_object user_obj;
    final int STORAGE_PERM = 1;
    FragmentManager fm;
    private ListView list_drawer;
    private View nav_head;
    private String imgURL;
    private ourTextView name, email;
    private ActionBarDrawerToggle toggle;
    private ImageView user_image;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> abc;
    private ArrayList<String> list;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private PopupMenu pop;
    private byte[] imgdecomp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users_info");
        initialize_views();
        insert_data();
        fm = getSupportFragmentManager();
        list_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        Fragment pro = new Profile_view();
                        fm.beginTransaction().replace(R.id.content_frame, pro).commit();
                        setTitle("Profile");
                        drawerLayout.closeDrawer(list_drawer);
                        break;
                    case 5:
                        firebaseAuth.signOut();
                        startActivity(new Intent(Profile.this, LoginPage.class));
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();
                        finish();
                        break;


                }
            }
        });
    }

    public void uploadimage(View v) {
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
    void insert_data() {
        list.add("Profile");
        list.add("App Settings");
        list.add("Refer a friend");
        list.add("Share");
        list.add("Log out");
        list_drawer.addHeaderView(nav_head);
        list_drawer.setAdapter(abc);
        drawerLayout.setDrawerListener(toggle);
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
                            user_image.setMaxWidth(100);
                            user_image.setMaxHeight(100);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri imgUri = data.getData();
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(imgUri, "image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 300);
                    cropIntent.putExtra("outputY", 300);
                    cropIntent.putExtra("return-data", true);
                    startActivityForResult(cropIntent, 22);
                    break;
                case 22:
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    imgdecomp = byteArrayOutputStream.toByteArray();
                    user_image = (ImageView) findViewById(R.id.userimage_nav);
                    Glide.with(getApplicationContext()).load(byteArrayOutputStream.toByteArray()).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                            user_image.setMaxWidth(100);
                            user_image.setMaxHeight(100);
                        }
                    });

                    if (imgdecomp != null) {
                        UploadTask imgUpload = storageReference.child("user-images/" + currentUser.getUid()).putBytes(imgdecomp);
                        imgUpload.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Image Upload Failed.\nYou can upload it later!!", Toast.LENGTH_LONG).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imgURL = taskSnapshot.getDownloadUrl().toString();
                                user_obj.imgURL = imgURL;
                                databaseReference.child(currentUser.getUid()).setValue(user_obj);
                            }
                        });
                    }
                    break;
            }
        }
    }
    void initialize_views() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        list_drawer = (ListView) findViewById(R.id.left_drawer);
        list = new ArrayList<>();
        abc = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        nav_head = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        name = (ourTextView) nav_head.findViewById(R.id.username_nav);
        email = (ourTextView) nav_head.findViewById(R.id.email_nav);
        user_image = (ImageView) nav_head.findViewById(R.id.userimage_nav);

    }
}

