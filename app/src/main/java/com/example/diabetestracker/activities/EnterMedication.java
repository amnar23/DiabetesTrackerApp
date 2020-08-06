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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.diabetestracker.R;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.model.Medication;
import com.example.diabetestracker.utils.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EnterMedication extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
    //Declaring variables
    TimePickerDialog timepicker;
    DatePickerDialog datepicker;
    EditText emedication,edosage,edate,etime;
    Medication med;
    private AwesomeValidation awesomeValidation;
    Preferences utils;
    DatabaseHelper dbHelper;
    Intent i;
    Spinner spinner;
    AlertDialog.Builder builder;
    ArrayAdapter<CharSequence> adapter;
    String unit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_medication);
        i=getIntent();
        //spinner
        spinner = findViewById(R.id.spinner1);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.unit, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //Initializing variables
        med= new Medication();
        utils=new Preferences();
        dbHelper=new DatabaseHelper(this);
        getEditTexts();
        //If activity is started to Edit data
        if(i.hasExtra("med"))
        {
            updateMed();
            ImageView img=findViewById(R.id.image);
            img.setImageResource(R.drawable.edit_entry);
        }
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)//back button Onclick
        {
            showAlert();
        }
        return super.onOptionsItemSelected(item);
    }
    //Back button click
    @Override
    public void onBackPressed()
    {
        showAlert();
    }
    //Show alert dialogue box
    private void showAlert()
    {
        builder=new AlertDialog.Builder(this);
        if(i.hasExtra("med"))
            builder.setMessage("Exit without changing?");
        else
            builder.setMessage("Exit without saving?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(i.getIntExtra("flag",0)==1)//if activity started from Mainactivity
                {
                    Intent i2=new Intent(EnterMedication.this, MainActivity.class);
                    startActivity(i2);
                    finish();
                }
                else
                {
                    Intent i2=new Intent(EnterMedication.this, MedicationLog.class);
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
    //Spinner - OnItemSelectedListener methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        med.setUnit(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
    //get all Edit texts
    private void getEditTexts()
    {
        emedication=findViewById(R.id.medication);
        edate=findViewById(R.id.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        edate.setText(dateFormat.format(new Date()));
        etime=findViewById(R.id.time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        etime.setText(timeFormat.format(new Date()));
        edosage=findViewById(R.id.dosage);
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
        datepicker = new DatePickerDialog(EnterMedication.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month,day;
                        month=String.valueOf(monthOfYear+1);
                        day=String.valueOf(dayOfMonth);
                        if(monthOfYear < 10){
                            month = "0" + month;
                        }
                        if(dayOfMonth < 10){
                            day  = "0" + day ;
                        }
                        edate.setText(year + "/" + month + "/" + day);
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
        timepicker = new TimePickerDialog(EnterMedication.this,
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
        //validate Medicine name
        if(String.valueOf(emedication.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.medication,"^[A-Za-z0-9 ]+[\\w .-]*",R.string.error_med);
        }
        //validate dosgae
        if(String.valueOf(edosage.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.dosage,"^[0-9]{1,}$",R.string.error_dos);
        }
        //validate date
        awesomeValidation.addValidation(this,R.id.date,"^[0-9]{4}[/][0-9]{1,2}[/][0-9]{1,2}$",R.string.error_date2);
        //validate time
        awesomeValidation.addValidation(this,R.id.time,"((1[0-2]|0?[1-9]):([0-5][0-9]) ?([AaPp][Mm]))",R.string.error_time2);

    }
    //set medication object after validation
    private void setMedication()
    {
        med.setMedication(emedication.getText().toString().trim());
        med.setDate(edate.getText().toString().trim());
        med.setTime(etime.getText().toString().trim());
        med.setEmail(utils.getEmail(this));
        med.setDosage(Double.parseDouble(edosage.getText().toString()));
    }
    //submit button - Onclick
    public void submitMedication(View v)
    {
        validate();
        if(awesomeValidation.validate())
        {
            setMedication();        //set data in Medicine object
            boolean flag;
            if(i.hasExtra("med"))       //if data is to be updated
            {
                med.setId(i.getIntExtra("id",0));
               flag=dbHelper.updateMed(med);
                if(flag)        //if update is successful
                {
                    Toast.makeText(this,"Update successful",Toast.LENGTH_LONG).show();
                    Intent ii=new Intent(this,MedicationLog.class);
                    startActivity(ii);
                    finish();
                }else
                {
                    Toast.makeText(this,"Update failed",Toast.LENGTH_LONG).show();
                }

            }
            else        //if new entry is made in Medications
            {
               flag =dbHelper.addMedication(med);
                if(flag)        //if new entry is successful
                {
                    Toast.makeText(this,"Entry successful",Toast.LENGTH_LONG).show();
                    Intent ii=new Intent(this, MedicationLog.class);
                    startActivity(ii);
                    finish();
                }else
                {
                    Toast.makeText(this,"Entry failed",Toast.LENGTH_LONG).show();
                }
            }


        }
    }
    //get data from intent to update
    private void updateMed()
    {
        emedication.setText(i.getStringExtra("med"));
        edosage.setText(String.valueOf(i.getDoubleExtra("dosage",0.0)));
        int position=adapter.getPosition(i.getStringExtra("unit"));
        spinner.setSelection(position);
        edate.setText(i.getStringExtra("date"));
        etime.setText(i.getStringExtra("time"));
    }
}