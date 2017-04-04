package com.nexturn.ModifiedViews;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.nexturn.Activites.Registration;
import com.nexturn.Fragments.Profile_Fragment;
import com.nexturn.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Prateek on 19-03-2017.
 */

public class DatePickerFrag extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    boolean registered;
    private String d, m, y;
    private Calendar c;

    public void setCaller(boolean register) {
        registered = register;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), month, c.get(Calendar.DAY_OF_MONTH));
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        month = month + 1;
        d = String.valueOf(day);
        m = String.valueOf(month);
        y = String.valueOf(year);
        if (year < c.get(Calendar.YEAR)) {
            if (registered) {
                Registration.dob.setText(d + "/" + m + "/" + y);
            } else {
                Profile_Fragment.dob_edit.setText(d + "/" + m + "/" + y);
            }
        } else if (year == c.get(Calendar.YEAR)) {
            if (month < (c.get(Calendar.MONTH) + 1)) {
                if (registered) {
                    Registration.dob.setText(d + "/" + m + "/" + y);
                } else {
                    Profile_Fragment.dob_edit.setText(d + "/" + m + "/" + y);
                }
            } else if (month == (c.get(Calendar.MONTH) + 1)) {
                if (day <= c.get(Calendar.DAY_OF_MONTH)) {
                    if (registered) {
                        Registration.dob.setText(d + "/" + m + "/" + y);
                    } else {
                        Profile_Fragment.dob_edit.setText(d + "/" + m + "/" + y);
                    }
                } else {
                    new AlertDialog.Builder(getContext()).setMessage("Date can't be set in Future/Present.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            } else {
                new AlertDialog.Builder(getContext()).setMessage("Date can't be set in Future/Present.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        } else {
            new AlertDialog.Builder(getContext()).setMessage("Date can't be set in Future/Present.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }
}
