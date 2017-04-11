package com.example.my.facebookauth.Activities;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;




/**
 * Created by Owner on 2017-01-12.
 */

public class scrollViewActivity extends AppCompatActivity {

    private TextView doubleText, singleText;

    SimpleDateFormat simpleDateFormat;
    SingleDateAndTimePickerDialog.Builder singleBuilder;
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doublepicker);
        singleText = (TextView) findViewById(R.id.singleText);
        doubleText = (TextView) findViewById(R.id.doubleText);

        final Calendar calendar = Calendar.getInstance();
        final Date minDate = calendar.getTime();


        //final Date maxDate = calendar.getTime();


        singleBuilder = new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()

                .backgroundColor(Color.WHITE)
                .mainColor(Color.GREEN)
                //.minDateRange(minDate)

                .minutesStep(5)
                //.mustBeOnFuture()
                //.minDateRange(minDate)
                //.maxDateRange(maxDate)
                .title("Simple")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        singleText.setText(simpleDateFormat.format(date));
                        Log.e("UTC", "" + date.getTime());
                        //Log.e("UTC NOW", "" + date.now());
                        //// TODO: 2017-01-19 might have to get timezone offset, multiply by 6000 and subtract from this time 
                    }
                });

        doubleBuilder = new DoubleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()

                //.mainColor(Color.GREEN)
                //.minutesStep(15)
                //.mustBeOnFuture()

                //.minDateRange(minDate)
                //.maxDateRange(maxDate)

                //.title("Double")
                .tab0Text("Depart")
                .tab1Text("Return")
                .listener(new DoubleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(List<Date> dates) {
                        final StringBuilder stringBuilder = new StringBuilder();
                        for (Date date : dates) {
                            stringBuilder.append(simpleDateFormat.format(date)).append("\n");
                        }
                        doubleText.setText(stringBuilder.toString());
                    }
                });



        this.simpleDateFormat = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());

        singleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                singleBuilder.display();
            }
        });

        doubleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doubleBuilder.display();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (singleBuilder != null)
            singleBuilder.close();
        if (doubleBuilder != null)
            doubleBuilder.close();
    }

}

