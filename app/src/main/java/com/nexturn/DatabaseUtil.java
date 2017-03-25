package com.nexturn;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Prateek on 25-03-2017.
 */

public class DatabaseUtil {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }
}