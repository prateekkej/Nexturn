package com.nexturn;

/**
 * Created by Prateek on 21-03-2017.
 */

public class User_object {

    public String fname, lname, email, gender, dob, mobile, aadhar, location, uid;

    public User_object() {
    }

    public User_object(String uid1, String fname1, String lname1, String email1, String gender1, String dob1, String mobile1, String aadhar1, String location1) {
        fname = fname1;
        lname = lname1;
        uid = uid1;
        email = email1;
        gender = gender1;
        dob = dob1;
        mobile = mobile1;
        aadhar = aadhar1;
        location = location1;
    }

    @Override
    public String toString() {
        return fname + lname + email + gender + dob + mobile + aadhar + location + uid;
    }
}
