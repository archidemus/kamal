package com.byobdev.kamal;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by famfrit on 04-09-17.
 */

public class FechaActivity extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int mNum, day, month, year;
    TextView fechaInicio;
    TextView fechaTermino;
    String fechaInit;
    String fechaFin;
    public Date dateInits, dateFins;
    long dateDiff;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String fechaSet;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static FechaActivity newInstance(int num) {
        FechaActivity fechaActivity = new FechaActivity();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        fechaActivity.setArguments(args);
        return fechaActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
        fechaInicio = (TextView)getActivity().findViewById(R.id.txt_fecha_inicio_vista);
        fechaTermino = (TextView)getActivity().findViewById(R.id.txt_fecha_termino_vista);

    }



    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        return new DatePickerDialog(getActivity(), this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        fechaSet = String.format("%02d/%02d/%d",day,month+1,year);
        fechaInit = fechaInicio.getText().toString();
        fechaFin = fechaTermino.getText().toString();

        if(mNum == R.id.btn_fechaInicio){
            if(dateDifference(fechaFin,day,month,year) < 0){
                fechaInicio.setText(fechaSet);
                fechaTermino.setText(fechaSet);
            }
            else {
                fechaInicio.setText(fechaSet);
            }
        }
        else if(mNum == R.id.btn_fechaTermino){
            if(dateDifference(fechaInit,day,month,year) >=0){
                fechaTermino.setText(fechaInicio.getText().toString());
            }
            else{
                fechaTermino.setText(fechaSet);
            }
        }
    }

    private long dateDifference(String fecha, int day, int month, int year){
        try {
            dateInits = simpleDateFormat.parse(fecha);
            dateFins = simpleDateFormat.parse(String.format("%02d/%02d/%d",day,month+1,year));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateDiff = dateInits.getTime() - dateFins.getTime();
        return dateDiff;
    }
}

