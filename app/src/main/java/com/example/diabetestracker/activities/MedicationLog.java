package com.example.diabetestracker.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.diabetestracker.R;
import com.example.diabetestracker.custom.CustomMedicationList;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.model.Medication;
import com.example.diabetestracker.utils.Preferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class MedicationLog extends AppCompatActivity {
    //Declare variables
    ArrayList<Medication> medEntries = new ArrayList<>();
    SwipeMenuListView listView;
    DatabaseHelper db;
    Preferences utils;
    Activity activity;
    Medication med;
    String id,email,email1;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_log);
        Intent i = getIntent();
        activity = this;
        listView = findViewById(R.id.medList);
        med=new Medication();
        utils = new Preferences();
        db = new DatabaseHelper(this);
        getMedEntries();
        if(!medEntries.isEmpty())
            new MedicationLog.GetMedData().execute();
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    //back button Onclick
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else
        {
            builder=new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to clear all records?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean flag=db.clearMedLog(email1);
                    if(flag)
                        Toast.makeText(MedicationLog.this,"Log Cleared",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
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
    //floating button - onclick
    public void medEntry(View v)
    {
        Intent i2=new Intent(MedicationLog.this, EnterMedication.class);
        startActivity(i2);
        finish();
    }

    //Async for Listview
    public class GetMedData extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return getMedEntries();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            CustomMedicationList customMedList = new CustomMedicationList(activity, medEntries);
            listView.setAdapter(customMedList);
            registerForContextMenu(listView);
            listView.setMenuCreator(creator);
            //on item swipe
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:
                            Intent i2=new Intent(MedicationLog.this,EnterMedication.class);
                            i2.putExtra("med",medEntries.get(position).getMedication());
                            i2.putExtra("dosage",medEntries.get(position).getDosage());
                            i2.putExtra("unit",medEntries.get(position).getUnit());
                            i2.putExtra("date",medEntries.get(position).getDate());
                            i2.putExtra("time",medEntries.get(position).getTime());
                            i2.putExtra("id",medEntries.get(position).getId());
                            startActivity(i2);
                            finish();
                            break;
                        case 1:
                            id=String.valueOf(medEntries.get(position).getId());
                            email=medEntries.get(position).getEmail().trim();
                            builder=new AlertDialog.Builder(MedicationLog.this);
                            builder.setMessage("Are you sure you want to delete this record?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean flag=db.deleteMedRecord(email,id);
                                    if(flag)
                                    {
                                        Toast.makeText(getApplicationContext(), "Record Deleted ", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());
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

                            break;
                    }
                    return false;
                }
            });
            //on item click
            listView.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MedicationLog.this,"Swipe left to Edit or Delete",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //get all medication entries from database
    protected Void getMedEntries() {
        email1 = utils.getEmail(MedicationLog.this);
        medEntries = db.getMedicEntries(email1);
        return null;
    }
    //Swipemenu options
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(Color.rgb(0x3a, 0xf4,
                    0x2a)));
            // set item width
            openItem.setWidth(170);
            // set item title
            openItem.setTitle("Edit");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFe,
                    0x00, 0x00)));
            // set item width
            deleteItem.setWidth(170);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_baseline_delete_24);
            // add to menu
            menu.addMenuItem(deleteItem);
            //hello
        }
    };
    //options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_options, menu);
        return true;
    }
}