package com.byobdev.kamal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by crono on 15-08-17.
 */

public class HoraActivity extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    int mNum;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    Date dateInits, dateFins;
    long dateDiff;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static HoraActivity newInstance(int num) {
        HoraActivity horaActivity = new HoraActivity();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        horaActivity.setArguments(args);


        return horaActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
    }

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
                    TextView tv2 = (TextView) getActivity().findViewById(R.id.HoraFinalfinal);
                    //Display the user changed time on TextView
                    if (mNum == R.id.HoraInicio) {
                        //24hrs format
                        tv.setText(String.format("%02d:%02d", hourOfDay, minute));

                    } else {
                        if (mNum == R.id.button3) {
                            //24hrs format
                            tv2.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }

                    //TextView tv2 = (TextView) getActivity().findViewById(R.id.HoraFinalfinal);
                    //tv2.setText(String.valueOf(hourOfDay) +":"+ String.valueOf(minute));

                }

    private long timeDifference(String hora, int hour, int minute){
        try {
            dateInits = simpleDateFormat.parse(hora);
            dateFins = simpleDateFormat.parse(String.format("%02d:%02d",hour,minute));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateDiff = dateInits.getTime() - dateFins.getTime();
        return dateDiff;
    }

}
