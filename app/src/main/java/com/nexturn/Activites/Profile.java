package com.nexturn.Activites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private ListView list_drawer;
    private FirebaseUser currentUser;
    private View nav_head;
    private User_object user_obj;
    private ourTextView name, email;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> abc;
    private ArrayList<String> list;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users_info");
        initialize_views();
        insert_data();
        list_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(Profile.this, LoginPage.class));
                    Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    }
}

