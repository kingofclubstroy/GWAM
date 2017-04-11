package com.example.my.facebookauth.Activities;

import java.util.Calendar;
import java.util.GregorianCalendar;

//import android.app.FragmentTransaction;
//import android.os.Bundle;
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.app.TimePickerDialog;
//import android.text.format.DateFormat;
//import android.view.Menu;
//import android.view.View;
//import android.widget.DatePicker;
//import android.widget.TextView;
//import android.widget.TimePicker;
//
//
//import com.example.my.facebookauth.R;
//
////// TODO: 2017-01-12 make this work for api 16
//
//public class TimePicker extends Activity { // implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
//
//
////public class TimePicker extends Activity {
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.timepicker);
//        }
//
//
//        public void showTimePickerDialog(View v) {
//            DialogFragment newFragment = new TimePickerFragment();
//            newFragment.show(getFragmentManager(), "timePicker");
//        }
//
//        public void showDatePickerDialog(View v) {
//            DialogFragment newFragment = new DatePickerFragment();
//            newFragment.show(getFragmentManager(), "datePicker");
//        }
//
//        public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
//
//            @Override
//            public Dialog onCreateDialog(Bundle savedInstanceState) {
//                // Use the current time as the default values for the picker
//                final Calendar c = Calendar.getInstance();
//                int hour = c.get(Calendar.HOUR_OF_DAY);
//                int minute = c.get(Calendar.MINUTE);
//
//                // Create a new instance of TimePickerDialog and return it
//                return new TimePickerDialog(getActivity(), this, hour, minute,
//                        DateFormat.is24HourFormat(getActivity()));
//            }
//
//            @Override
//            public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
//
//            }
//        }
//
//        public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//            @Override
//            public Dialog onCreateDialog(Bundle savedInstanceState) {
//                // Use the current date as the default date in the picker
//                final Calendar c = Calendar.getInstance();
//                int year = c.get(Calendar.YEAR);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DAY_OF_MONTH);
//
//                // Create a new instance of DatePickerDialog and return it
//                return new DatePickerDialog(getActivity(), this, year, month, day);
//            }
//
//            @Override
//            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//
//            }
//        }
//    }
//    TextView mDateTextView;
//    android.text.format.DateFormat df;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.timepicker);
//        df = new android.text.format.DateFormat();
//        mDateTextView = (TextView) findViewById(R.id.date);
//        mDateTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                DialogFragment newFragment = new DatePickerDialogFragment(TimePicker.this);
//                newFragment.show(ft, "date_dialog");
//            }
//        });
//    }
//
//
//    public void showTimePickerDialog(View v) {
//        DialogFragment newFragment = new TimePickerFragment();
//        newFragment.show(getFragmentManager(), "timePicker");
//    }
//
//    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getFragmentManager(), "datePicker");
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int monthOfYear,
//                          int dayOfMonth) {
//        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
//        mDateTextView.setText(df.format(cal.getTime()));
//    }
//
//    @Override
//    public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
//
//    }
//
//    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//
//            // Create a new instance of TimePickerDialog and return it
//            return new TimePickerDialog(getActivity(), this, hour, minute,
//                    DateFormat.is24HourFormat(getActivity()));
//        }
//
//        @Override
//        public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
//
//        }
//
//
//    }
//
//    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//            // Do something with the date chosen by the user
//        }
//    }

