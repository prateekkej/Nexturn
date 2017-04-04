package com.nexturn;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Prateek on 27-03-2017.
 */

public class User_location {
    public String uid, name, imgURL, phone, fblink, goingTo;
    public double lat, lon;
    public Marker marker;
    public boolean phoneVisible;
    public int em, gg, fb;

    public User_location() {
        uid = "";
        lat = 0;
        lon = 0;
        name = "";
        imgURL = "";
        marker = null;
        phone = null;
        goingTo = null;
    }

    public User_location(User_location obj) {
        uid = obj.uid;
        name = obj.name;
        lat = obj.lat;
        lon = obj.lon;
        imgURL = obj.imgURL;
        phone = obj.phone;
        fblink = obj.fblink;
        goingTo = obj.goingTo;
        phoneVisible = obj.phoneVisible;
        this.em = obj.em;
        this.fb = obj.fb;
        this.gg = obj.gg;
    }

    public User_location(String a, String b, double c, double d, String e, String f, String g, boolean h, int em, int gg, int fb) {
        uid = a;
        goingTo = null;
        name = b;
        lat = c;
        lon = d;
        imgURL = e;
        phone = f;
        if (g != null) {
            fblink = g;
        }
        phoneVisible = h;
        this.em = em;
        this.fb = fb;
        this.gg = gg;
    }

    public void copyAll(User_location obj) {
        this.uid = obj.uid;
        this.name = obj.name;
        this.lat = obj.lat;
        this.lon = obj.lon;
        this.imgURL = obj.imgURL;
        this.phone = obj.phone;
        this.fblink = obj.fblink;
        this.goingTo = obj.goingTo;
        this.phoneVisible = obj.phoneVisible;
        this.em = obj.em;
        this.fb = obj.fb;
        this.gg = obj.gg;

    }

    public double getLong() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getUid() {
        return uid;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lon);
    }

    @Override
    public String toString() {
        return name + " " + goingTo;
    }
}
