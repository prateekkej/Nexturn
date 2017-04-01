package com.nexturn;

/**
 * Created by Prateek on 21-03-2017.
 */

public class User_object {

    public String fname, lname, email, gender, signedUpUsing, dob, mobile, aadhar, location, uid, imgURL, fblink;
    public int fb, gg, em;

    public User_object() {
        fb = 0;
        gg = 0;
        em = 0;
        fname = "First Name";
        lname = "Last Name";
        uid = "ID";
        email = "Email Address";
        gender = "Gender";
        dob = "DD/MM/YYYY";
        mobile = "Mobile";
        aadhar = "XXXX/XXXX/XXXX";
        imgURL = " ";
        location = "State";
        signedUpUsing = "E";
        fblink = null;
    }

    public User_object(String uid1, String fname1, String lname1, String email1, String gender1, String dob1, String mobile1, String aadhar1, String location1, String imgUR, String signUpMethod, String fbl) {
        fname = fname1;
        lname = lname1;
        uid = uid1;
        email = email1;
        gender = gender1;
        dob = dob1;
        mobile = mobile1;
        aadhar = aadhar1;
        imgURL = imgUR;
        location = location1;
        signedUpUsing = signUpMethod;
        if (signedUpUsing.equals("G")) {
            gg = 1;
        } else if (signedUpUsing.equals("E")) {
            em = 1;
        } else if (signedUpUsing.equals("F")) {
            fb = 1;
            fblink = fbl;
        }
    }

    public User_object(User_object obj) {
        fname = obj.fname;
        lname = obj.lname;
        uid = obj.uid;
        email = obj.email;
        gender = obj.gender;
        dob = obj.dob;
        mobile = obj.mobile;
        aadhar = obj.aadhar;
        imgURL = obj.imgURL;
        location = obj.location;
        signedUpUsing = obj.signedUpUsing;
        if (signedUpUsing.equals("G")) {
            gg = 1;
        } else if (signedUpUsing.equals("E")) {
            em = 1;
        } else if (signedUpUsing.equals("F")) {
            fb = 1;
            fblink = obj.fblink;
        }
    }
    @Override
    public String toString() {
        return fname + lname + email + gender + dob + mobile + aadhar + location + uid;
    }
}
