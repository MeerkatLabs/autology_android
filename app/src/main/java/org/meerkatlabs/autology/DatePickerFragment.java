package org.meerkatlabs.autology;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rerobins on 11/22/17.
 */

public class DatePickerFragment extends AppCompatDialogFragment {

    private static final String CURRENT_DATE_BUNDLE_KEY = "currentDate";

    public static DatePickerFragment createInstance (@NonNull Calendar currentDate) {

        DatePickerFragment fragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putLong(CURRENT_DATE_BUNDLE_KEY, currentDate.getTime().getTime());

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        Bundle arguments = getArguments();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(arguments.getLong(CURRENT_DATE_BUNDLE_KEY)));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = null;
        try {
            listener = (DatePickerDialog.OnDateSetListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("RER", "Activity must implement DatePickerDialog.OnDateSetListener");
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);

    }

}
