package com.example.diabetestracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.diabetestracker.R;
import com.example.diabetestracker.utils.Preferences;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        if(Preferences.getEmail(this)!=null && !Preferences.getEmail(this).equals("")){
            Intent i3=new Intent(this,MainActivity.class);
            startActivity(i3);
            finish();
        }
    }
    //Login Button - Onclick
    public void goToLogin(View v){
        Intent i1=new Intent(this, Login.class);
        startActivity(i1);
    }
    //SignUp Button - Onclick
    public void goToSignup(View v){
        Intent i2=new Intent(this,Signup.class);
        startActivity(i2);
    }
}