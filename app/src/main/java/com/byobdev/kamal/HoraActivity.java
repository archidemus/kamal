package com.byobdev.kamal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by crono on 15-08-17.
 */

public class HoraActivity extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(R.id.HoraIniciofinal);
        //Display the user changed time on TextView
        if(tv.getText().equals("00:00")){
            tv.setText(String.valueOf(hourOfDay) +":"+ String.valueOf(minute));
        }
        else{
            tv = (TextView) getActivity().findViewById(R.id.HoraFinalfinal);
            tv.setText(String.valueOf(hourOfDay) +":"+ String.valueOf(minute));
        }

        //TextView tv2 = (TextView) getActivity().findViewById(R.id.HoraFinalfinal);
        //tv2.setText(String.valueOf(hourOfDay) +":"+ String.valueOf(minute));

    }

}
