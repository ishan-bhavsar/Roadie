package com.example.gptsi.roadie.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gptsi.roadie.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Time {

    Context mContext;
    View mView;
    TextView calendar, time;
    ImageView calendar_img,time_img;
    ImageButton edit;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Time(Context context, View view) {

        this.mContext = context;
        this.mView = view;

        calendar = (TextView) mView.findViewById(R.id.time_cal);
        time = (TextView) mView.findViewById(R.id.time_time);
        calendar_img = (ImageView) mView.findViewById(R.id.time_cal_image);
        time_img = (ImageView) mView.findViewById(R.id.time_time_image);
        edit = (ImageButton) mView.findViewById(R.id.time_edit);
        toolbar = (Toolbar) mView.findViewById(R.id.appbar);
        toolbar.setTitle("Time");
//TODO        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Time.this,Add_Request.class));
//                finish();
//            }
//        });

        edit.setColorFilter(Color.WHITE);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar_img.setVisibility(View.VISIBLE);
                time_img.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
            }
        });

        sharedPreferences = mContext.getSharedPreferences("AddRequest", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.contains("time")) {
            calendar.setText(sharedPreferences.getString("date",""));
            calendar_img.setVisibility(View.GONE);
            calendar.setVisibility(View.VISIBLE);
            time.setText(sharedPreferences.getString("time",""));
            time_img.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
        }

        Calendar c = Calendar.getInstance();

        final DatePickerDialog datePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.setText("" + dayOfMonth + " / " + (month + 1) + " / " + year);
                calendar_img.setVisibility(View.GONE);
                calendar.setVisibility(View.VISIBLE);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        calendar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        final TimePickerDialog timePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.setText("" + hourOfDay + " : " + minute);
                time_img.setVisibility(View.GONE);
                time.setVisibility(View.VISIBLE);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);

        time_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });

    }

    public Boolean submit() {
        if(time.getText().toString().equals("Time") || calendar.getText().toString().equals("Day")) {
            Toast.makeText(mContext, "Fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {

            editor.putString("time", time.getText().toString());
            editor.putString("date", calendar.getText().toString());
            editor.commit();
            return true;
        }
    }
}
