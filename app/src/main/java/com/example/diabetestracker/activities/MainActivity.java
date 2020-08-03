package com.example.diabetestracker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.diabetestracker.R;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.fragment.HomeFragment;
import com.example.diabetestracker.fragment.ProfileFragment;
import com.example.diabetestracker.fragment.ViewPagerAdapter;
import com.example.diabetestracker.model.User;
import com.example.diabetestracker.utils.Preferences;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    //Declaring Variables
    Toolbar tb;
    FloatingActionButton fab1, fab2, fab3;
    User user;
    Preferences utils;
    DatabaseHelper dbHelper;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set Tab
        tb = findViewById(R.id.title);
        setSupportActionBar(tb);
        //initialize variables
        Intent i=getIntent();
        utils=new Preferences();
        dbHelper=new DatabaseHelper(this);
        user=new User();
        //get User Details
        getUserDetails();
        //ViewPager
        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPage(viewPager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //OnClickListeners for floating buttons
        fab1 = findViewById(R.id.item1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EnterSugar.class);
                i.putExtra("flag",1);//indicates activity is starting from home
                startActivity(i);
            }
        });

        fab2 = findViewById(R.id.item2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EnterWeight.class);
                i.putExtra("flag",1);//indicates activity is starting from home
                startActivity(i);
            }
        });

        fab3 = findViewById(R.id.item3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EnterMedication.class);
                i.putExtra("flag",1);//indicates activity is starting from home
                startActivity(i);
            }
        });
    }
    //take details for profile fragment
    private void getUserDetails()
    {
        email=utils.getEmail(this);
        user=dbHelper.getUser(email);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reminder:
                Intent i = new Intent(this, Reminder.class);
                startActivity(i);
                break;

            case R.id.dlt: //Delete all records
                AlertDialog.Builder builder;
                builder=new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete all records?");
                builder.setCancelable(false);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.clearSugarLog(email);
                        dbHelper.clearWeightLog(email);
                        dbHelper.clearMedLog(email);
                            Toast.makeText(MainActivity.this,"All Records Deleted",Toast.LENGTH_SHORT).show();
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

            case R.id.logout:
                utils.saveEmail("",this);
                if(utils.getEmail(this)==null||utils.getEmail(this)=="")
                {
                    Intent i2=new Intent(this,Welcome.class);
                    startActivity(i2);
                    finish();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPage(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment(), "HOME");
        viewPagerAdapter.addFragment(new ProfileFragment(user.getName(),user.getEmail(),user.getDOB(),user.getGender(),user.getPassword()), "MY PROFILE");
        viewPager.setAdapter(viewPagerAdapter);
    }

}