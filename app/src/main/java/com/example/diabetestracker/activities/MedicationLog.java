package com.example.diabetestracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    //Location
    FusedLocationProviderClient client;
    Double lat=0.0,lang=0.0;
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
        //location
        client = LocationServices.getFusedLocationProviderClient(this);
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
        else if (item.getItemId()== R.id.dlt)
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
        else if(item.getItemId()==R.id.pharm)//pharmacy option menu clicked
        {
            if (ActivityCompat.checkSelfPermission(MedicationLog.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(MedicationLog.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
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
        getMenuInflater().inflate(R.menu.medlog_options, menu);
        return true;
    }
    //Get Current Location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location!=null)
                {
                    lat=location.getLatitude();
                    lang=location.getLongitude();
                    // Search for restaurants nearby
                    Uri gmmIntentUri = Uri.parse("geo:"+String.valueOf(lat)+","+String.valueOf(lang)+"?q=pharmacies");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==44)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLocation();
            }
        }
    }
}