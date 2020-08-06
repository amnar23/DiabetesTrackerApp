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
import com.example.diabetestracker.custom.CustomSugarList;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.model.Sugar;
import com.example.diabetestracker.utils.Preferences;

import java.util.ArrayList;

public class SugarLog extends AppCompatActivity {
    //Declare variables
    ArrayList<Sugar> sugarEntries = new ArrayList<>();
    SwipeMenuListView listView;
    DatabaseHelper db;
    Preferences utils;
    Activity activity;
    Sugar sugar;
    String id,email,email1;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar_log);
        Intent i = getIntent();
        //initialize variables
        activity = this;
        listView = findViewById(R.id.sugarList);
        sugar=new Sugar();
        utils = new Preferences();
        db = new DatabaseHelper(this);
        email1=utils.getEmail(this);
        //get sugar entries from database
        getSugarEntries();
        if(!sugarEntries.isEmpty())
            new GetSugarData().execute();
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    //options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sugarlog_options, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  //back button Onclick
        {
            finish();
        }
        else if(item.getItemId()==R.id.clear)   //clear sugar log - options menu
        {
            builder=new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to clear all records?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean flag=db.clearSugarLog(email1);
                    if(flag)
                        Toast.makeText(SugarLog.this,"Log Cleared",Toast.LENGTH_SHORT).show();
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
        else if(item.getItemId()==R.id.level)
        {
            Intent i = new Intent(this, TargetLevels.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.warning)
        {
            Intent i2 = new Intent(this, SugarWarning.class);
            startActivity(i2);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.clear);
        if(sugarEntries.isEmpty())     //if sugar log is empty-disable clear log options menu
            item.setEnabled(false);
        else                            //if sugar log is not empty-enable clear log options menu
            item.setEnabled(true);
        super.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }
    //floating button - onclick
    public void sugarEntry(View view)
    {
        Intent i2=new Intent(SugarLog.this, EnterSugar.class);
        startActivity(i2);
        finish();
    }
    //Async for Listview
    public class GetSugarData extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return getSugarEntries();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            CustomSugarList customSugarList = new CustomSugarList(activity, sugarEntries);
            listView.setAdapter(customSugarList);
            listView.setMenuCreator(creator);
            //on item swipe
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0: //edit this record
                            Intent i2=new Intent(SugarLog.this, EnterSugar.class);
                            i2.putExtra("conc",sugarEntries.get(position).getConcentration());
                            i2.putExtra("measured",sugarEntries.get(position).getMeasured());
                            i2.putExtra("date",sugarEntries.get(position).getDate());
                            i2.putExtra("time",sugarEntries.get(position).getTime());
                            i2.putExtra("id",sugarEntries.get(position).getId());
                            startActivity(i2);
                            finish();
                            break;
                        case 1:     //delete this record
                            id=String.valueOf(sugarEntries.get(position).getId());
                            email=sugarEntries.get(position).getEmail().trim();
                            builder=new AlertDialog.Builder(SugarLog.this);
                            builder.setMessage("Are you sure you want to delete this record?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean flag=db.deleteSugarRecord(email,id);
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
                        //add alert dialogue
                    }
                    return false;
                }
            });
            //on item click
            listView.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(SugarLog.this,"Swipe left to Edit or Delete",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //get all sugar entries from database
    protected Void getSugarEntries()
    {
        String email=utils.getEmail(this);
        sugarEntries=db.getSugarEntries(email);
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
        }
    };

}