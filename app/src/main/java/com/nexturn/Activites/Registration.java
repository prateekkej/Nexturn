package com.nexturn.Activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
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
import com.nexturn.ModifiedViews.DatePickerFrag;
import com.nexturn.R;

public class Registration extends AppCompatActivity {
    Spinner state_list;
    ImageView userimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
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
        PopupMenu pop = new PopupMenu(this, v);
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
                }
            });

        }
    }
}
