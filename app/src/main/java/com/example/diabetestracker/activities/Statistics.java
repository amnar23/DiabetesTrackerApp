package com.example.diabetestracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.diabetestracker.R;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.model.Sugar;
import com.example.diabetestracker.utils.Preferences;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {
    //Declaring variables
    TextView tfasting,tafterB,tbeforeL,tafterL,tbeforeD,tafterD,trandom,tall;
    TextView twfasting,twafterB,twbeforeL,twafterL,twbeforeD,twafterD,twrandom,twall;
    Preferences utils;
    String email;
    ArrayList<Sugar> sugarEntries,sugarWeekEntries;
    DatabaseHelper dbHelper;
    int sum1=0,sum2=0,sum3=0,sum4=0,sum5=0,sum6=0,sum7=0,sum8=0;
    double fastingavg,afterBavg,beforeLavg,afterLavg,beforeDavg,afterDavg,randomavg,allavg;
    int count1=0,count2=0,count3=0,count4=0,count5=0,count6=0,count7=0,count8=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Intent i=getIntent();
        //initializing variables
        getEditTexts();
        utils=new Preferences();
        email=utils.getEmail(this);
        sugarEntries=new ArrayList<>();
        sugarWeekEntries=new ArrayList<>();
        dbHelper=new DatabaseHelper(this);
        getSugarEntries();
        getSugarWeekEntries();
        calculateMonthlyAverage();
        setAvgValues();
        calculateWeeklyAverage();
        setWeekAvgValues();

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)//back button Onclick
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //get EditTexts
    private void getEditTexts()
    {
        tfasting=findViewById(R.id.fasting);
        tafterB=findViewById(R.id.afterB);
        tbeforeL=findViewById(R.id.beforeL);
        tafterL=findViewById(R.id.afterL);
        tbeforeD=findViewById(R.id.beforeD);
        tafterD=findViewById(R.id.afterD);
        trandom=findViewById(R.id.random);
        tall=findViewById(R.id.all);

        twfasting=findViewById(R.id.wfasting);
        twafterB=findViewById(R.id.wafterB);
        twbeforeL=findViewById(R.id.wbeforeL);
        twafterL=findViewById(R.id.wafterL);
        twbeforeD=findViewById(R.id.wbeforeD);
        twafterD=findViewById(R.id.wafterD);
        twrandom=findViewById(R.id.wrandom);
        twall=findViewById(R.id.wall);
    }
    private void getSugarEntries(){
        sugarEntries=dbHelper.getLastMonthEntries(email);
        Log.d("Count",String.valueOf(sugarEntries.size()));
    }
    private void getSugarWeekEntries(){
        sugarWeekEntries=dbHelper.getLastWeekEntries(email);
        Log.d("Count",String.valueOf(sugarEntries.size()));
    }
    private void calculateMonthlyAverage()
    {
        for(int i=0;i<sugarEntries.size();i++)
        {
            if(sugarEntries.get(i).getMeasured().equals("Fasting"))
                calculateFastingSum(sugarEntries.get(i).getConcentration());
            else if(sugarEntries.get(i).getMeasured().equals("After Breakfast"))
                calculateAterBSum(sugarEntries.get(i).getConcentration());
            else if(sugarEntries.get(i).getMeasured().equals("Before Lunch"))
                calculateBeforeLSum(sugarEntries.get(i).getConcentration());
            else if(sugarEntries.get(i).getMeasured().equals("After Lunch"))
                calculateAfterLSum(sugarEntries.get(i).getConcentration());
            else if(sugarEntries.get(i).getMeasured().equals("Before Dinner"))
                calculateBeforeDSum(sugarEntries.get(i).getConcentration());
            else if(sugarEntries.get(i).getMeasured().equals("After Dinner"))
                calculateAfterDSum(sugarEntries.get(i).getConcentration());
            else if(sugarEntries.get(i).getMeasured().equals("Random"))
                calculateRandomSum(sugarEntries.get(i).getConcentration());
            calculateAllSum(sugarEntries.get(i).getConcentration());
        }
        if(count1>0)
            fastingavg=sum1/count1;
        if(count2>0)
            afterBavg=sum2/count2;
        if(count3>0)
            beforeLavg=sum3/count3;
        if(count4>0)
            afterLavg=sum4/count4;
        if(count5>0)
            beforeDavg=sum5/count5;
        if(count6>0)
            afterDavg=sum6/count6;
        if(count7>0)
            randomavg=sum7/count7;
        if(count8>0)
            allavg=sum8/count8;
    }
    private void calculateWeeklyAverage()
    {
        count1=count2=count3=count4=count5=count6=count7=count8=0;
        sum1=sum2=sum3=sum4=sum5=sum6=sum7=sum8=0;
        for(int i=0;i<sugarWeekEntries.size();i++)
        {
            if(sugarWeekEntries.get(i).getMeasured().equals("Fasting"))
                calculateFastingSum(sugarWeekEntries.get(i).getConcentration());
            else if(sugarWeekEntries.get(i).getMeasured().equals("After Breakfast"))
                calculateAterBSum(sugarWeekEntries.get(i).getConcentration());
            else if(sugarWeekEntries.get(i).getMeasured().equals("Before Lunch"))
                calculateBeforeLSum(sugarWeekEntries.get(i).getConcentration());
            else if(sugarWeekEntries.get(i).getMeasured().equals("After Lunch"))
                calculateAfterLSum(sugarWeekEntries.get(i).getConcentration());
            else if(sugarWeekEntries.get(i).getMeasured().equals("Before Dinner"))
                calculateBeforeDSum(sugarWeekEntries.get(i).getConcentration());
            else if(sugarWeekEntries.get(i).getMeasured().equals("After Dinner"))
                calculateAfterDSum(sugarWeekEntries.get(i).getConcentration());
            else if(sugarWeekEntries.get(i).getMeasured().equals("Random"))
                calculateRandomSum(sugarWeekEntries.get(i).getConcentration());
            calculateAllSum(sugarWeekEntries.get(i).getConcentration());
        }
        if(count1>0)
            fastingavg=sum1/count1;
        if(count2>0)
            afterBavg=sum2/count2;
        if(count3>0)
            beforeLavg=sum3/count3;
        if(count4>0)
            afterLavg=sum4/count4;
        if(count5>0)
            beforeDavg=sum5/count5;
        if(count6>0)
            afterDavg=sum6/count6;
        if(count7>0)
            randomavg=sum7/count7;
        if(count8>0)
            allavg=sum8/count8;
    }
    private void calculateFastingSum(int conc)
    {
        sum1+=conc;
        count1++;
    }
    private void calculateAterBSum(int conc)
    {
        sum2+=conc;
        count2++;
    }
    private void calculateBeforeLSum(int conc)
    {
        sum3+=conc;
        count3++;
    }
    private void calculateAfterLSum(int conc)
    {
        sum4+=conc;
        count4++;
    }
    private void calculateBeforeDSum(int conc)
    {
        sum5+=conc;
        count5++;
    }
    private void calculateAfterDSum(int conc)
    {
        sum6+=conc;
        count6++;
    }
    private void calculateRandomSum(int conc)
    {
        sum7+=conc;
        count7++;
    }
    private void calculateAllSum(int conc)
    {
        sum8+=conc;
        count8++;
    }
    private void setAvgValues()
    {
        if(count1>0)
            tfasting.setText(String.valueOf(fastingavg)+"mmol/oL");
        if(count2>0)
            tafterB.setText(String.valueOf(afterBavg)+"mmol/oL");
        if(count3>0)
            tbeforeL.setText(String.valueOf(beforeLavg)+"mmol/oL");
        if(count4>0)
            tafterL.setText(String.valueOf(afterLavg)+"mmol/oL");
        if(count5>0)
            tbeforeD.setText(String.valueOf(beforeDavg)+"mmol/oL");
        if(count6>0)
            tafterD.setText(String.valueOf(afterDavg)+"mmol/oL");
        if(count7>0)
            trandom.setText(String.valueOf(randomavg)+"mmol/oL");
        if(count8>0)
            tall.setText(String.valueOf(allavg)+"mmol/oL");
    }
    private void setWeekAvgValues()
    {
        if(count1>0)
            twfasting.setText(String.valueOf(fastingavg)+"mmol/oL");
        if(count2>0)
            twafterB.setText(String.valueOf(afterBavg)+"mmol/oL");
        if(count3>0)
            twbeforeL.setText(String.valueOf(beforeLavg)+"mmol/oL");
        if(count4>0)
            twafterL.setText(String.valueOf(afterLavg)+"mmol/oL");
        if(count5>0)
            twbeforeD.setText(String.valueOf(beforeDavg)+"mmol/oL");
        if(count6>0)
            twafterD.setText(String.valueOf(afterDavg)+"mmol/oL");
        if(count7>0)
            twrandom.setText(String.valueOf(randomavg)+"mmol/oL");
        if(count8>0)
            twall.setText(String.valueOf(allavg)+"mmol/oL");
    }
}