package com.nexturn.ModifiedViews;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.nexturn.R;

import java.util.Calendar;

/**
 * Created by Prateek on 19-03-2017.
 */

public class DatePickerFrag extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), month, c.get(Calendar.DAY_OF_MONTH));
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        month++;
        TextView tv1 = (TextView) getActivity().findViewById(R.id.dobenter);
        tv1.setText(day + "/" + month + "/" + year);
    }
}
