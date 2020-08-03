package com.example.diabetestracker.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.diabetestracker.R;
import com.example.diabetestracker.model.Weight;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.utils.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EnterWeight extends AppCompatActivity {
    //Declaring variables
    DatePickerDialog datepicker;
    TimePickerDialog timepicker;
    EditText eweight,edate,etime;
    private AwesomeValidation awesomeValidation;
    Weight weight;
    Preferences utils;
    DatabaseHelper dbHelper;
    Intent i;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_weight);
        i=getIntent();
        getEditTexts();
        weight=new Weight();
        utils=new Preferences();
        dbHelper=new DatabaseHelper(this);
        if(i.hasExtra("weight"))
        {
            updateWeight();
            ImageView img=findViewById(R.id.image);
            //img.setImageResource(R.drawable.weightentry);
        }
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    //back button Onclick
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            builder=new AlertDialog.Builder(this);
            builder.setMessage("Exit without saving?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(i.getIntExtra("flag",0)==1)//if activity started from Mainactivity
                    {
                        Intent i2=new Intent(EnterWeight.this, MainActivity.class);
                        startActivity(i2);
                        finish();
                    }
                    else
                    {
                        Intent i2=new Intent(EnterWeight.this, WeightLog.class);
                        startActivity(i2);
                        finish();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void getEditTexts()
    {
        eweight=findViewById(R.id.weight);
        edate=findViewById(R.id.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        edate.setText(dateFormat.format(new Date()));
        etime=findViewById(R.id.time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        etime.setText(timeFormat.format(new Date()));
        //to disable keyboard popup
        if (Build.VERSION.SDK_INT >= 11) {
            etime.setRawInputType(InputType.TYPE_CLASS_TEXT);
            etime.setTextIsSelectable(true);
            edate.setRawInputType(InputType.TYPE_CLASS_TEXT);
            edate.setTextIsSelectable(true);
        } else {
            etime.setRawInputType(InputType.TYPE_NULL);
            etime.setFocusable(true);
            edate.setRawInputType(InputType.TYPE_NULL);
            edate.setFocusable(true);
        }
    }
    //Date Picker
    public void pickDate(View v){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        datepicker = new DatePickerDialog(EnterWeight.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        datepicker.show();
    }
    //Time Picker
    public void pickTime(View v){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        String am_pm=" ";
        if(cldr.get(Calendar.AM_PM)==Calendar.AM)
            am_pm="am";
        else if(cldr.get(Calendar.AM_PM)==Calendar.PM)
            am_pm="pm";
        // time picker dialog
        timepicker = new TimePickerDialog(EnterWeight.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        String time=sHour+":"+sMinute;
                        SimpleDateFormat  fmt=new SimpleDateFormat("hh:mm");
                        Date date=null;
                        try{
                            date=fmt.parse(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");

                        String formattedTime=fmtOut.format(date);
                        etime.setText(formattedTime);
                    }
                }, hour, minutes, false);
        timepicker.show();
    }
    //validate data
    private void validate()
    {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        if (String.valueOf(eweight.getText()).isEmpty()) {
            awesomeValidation.addValidation(this, R.id.weight, "^[0-9]{1,3}[.]{0,1}[0-9]{0,2}$", R.string.error_weight1);
        } else {
            awesomeValidation.addValidation(this, R.id.weight, "^[0-9]{1,3}[.]{0,1}[0-9]{0,2}$", R.string.error_weight2);
        }
        //validate date
        awesomeValidation.addValidation(this,R.id.date,"^[0-9]{1,2}[/][0-9]{1,2}[/][0-9]{4}$",R.string.error_date2);
        //validate time
        awesomeValidation.addValidation(this,R.id.time,"((1[0-2]|0?[1-9]):([0-5][0-9]) ?([AaPp][Mm]))",R.string.error_time2);
    }
    public void submitWeight(View v)
    {
        validate();
        if(awesomeValidation.validate())
        {
            setWeight();
            boolean flag;
            if(i.hasExtra("weight"))
            {
                weight.setId(i.getIntExtra("id",0));
                flag=dbHelper.updateWeight(weight);
                if(flag)
                {
                    Toast.makeText(this,"Update successful",Toast.LENGTH_LONG).show();
                    Intent ii=new Intent(this, WeightLog.class);
                    startActivity(ii);
                    finish();
                }
                else
                    Toast.makeText(this,"Update Failed",Toast.LENGTH_LONG).show();
            }
            else
            {
                flag= dbHelper.addWeight(weight);
                if(flag)
                {
                    Toast.makeText(this,"Entry successful",Toast.LENGTH_LONG).show();
                    Intent ii=new Intent(this, WeightLog.class);
                    startActivity(ii);
                    finish();
                }
                else
                    Toast.makeText(this,"Entry failed",Toast.LENGTH_LONG).show();
            }

        }
    }
    private void setWeight()
    {
        weight.setWeight(Double.parseDouble(eweight.getText().toString()));
        weight.setDate(edate.getText().toString());
        weight.setTime(etime.getText().toString());
        weight.setEmail(utils.getEmail(this));
    }
    private void updateWeight()
    {
        eweight.setText(String.valueOf(i.getDoubleExtra("weight",0)));
        edate.setText(i.getStringExtra("date"));
        etime.setText(i.getStringExtra("time"));
    }
}