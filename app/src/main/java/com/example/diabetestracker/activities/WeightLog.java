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
import com.example.diabetestracker.custom.CustomWeightList;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.model.Weight;
import com.example.diabetestracker.utils.Preferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class WeightLog extends AppCompatActivity {
    //Declaring variables
    ArrayList<Weight> weightEntries=new ArrayList<>();
    SwipeMenuListView listView;
    DatabaseHelper db;
    Preferences utils;
    Activity activity;
    FloatingActionButton fab;
    AlertDialog.Builder builder;
    Weight clickedWeight;
    String email,id,email1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);
        Intent i=getIntent();
        activity=this;
        //initialize variables
        listView=findViewById(R.id.weightList);
        clickedWeight=new Weight();
        db=new DatabaseHelper(this);
        utils=new Preferences();
        email1=utils.getEmail(this);
        getWeightEntries();
        if(!weightEntries.isEmpty())
            new GetWeightData().execute();
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_options, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  //back button Onclick
        {
            finish();
        }
        else    //clear log onclick
        {
            builder=new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to clear all records?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean flag=db.clearWeightLog(email1);
                    if(flag)
                        Toast.makeText(WeightLog.this,"Log Cleared",Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.clear);
        if(weightEntries.isEmpty())     //if weight log is empty-disable clear log options menu
            item.setEnabled(false);
        else                            //if weight log is not empty-enable clear log options menu
            item.setEnabled(true);
        super.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }
    //floating button Onclick
    public void weightEntry(View v)
    {
        Intent i=new Intent(this,EnterWeight.class);
        startActivity(i);
        finish();
    }
    //Async for listview
    public class GetWeightData extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            return getWeightEntries();
        }
        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);

            CustomWeightList customWeightList=new CustomWeightList(activity,weightEntries);
            listView.setAdapter(customWeightList);
            listView.setMenuCreator(creator);
            //on item swipe
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    id=String.valueOf(weightEntries.get(position).getId());
                    email=weightEntries.get(position).getEmail().trim();
                    switch (index) {
                        case 0:
                            Intent i2=new Intent(WeightLog.this, EnterWeight.class);
                            i2.putExtra("weight",weightEntries.get(position).getWeight());
                            i2.putExtra("date",weightEntries.get(position).getDate());
                            i2.putExtra("time",weightEntries.get(position).getTime());
                            i2.putExtra("id",weightEntries.get(position).getId());
                            startActivity(i2);
                            finish();
                            break;
                        case 1:
                            builder=new AlertDialog.Builder(WeightLog.this);
                            builder.setMessage("Are you sure you want to delete this record?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean flag=db.deleteWeightRecord(email,id);
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
                    Toast.makeText(WeightLog.this,"Swipe left to Edit or Delete",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    protected Void getWeightEntries()
    {
        String email=utils.getEmail(this);
        weightEntries=db.getWeightEntries(email);
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
            openItem.setBackground(R.color.green);
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