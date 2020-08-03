package com.example.diabetestracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.diabetestracker.R;
import com.example.diabetestracker.activities.MainActivity;
import com.example.diabetestracker.database.DatabaseHelper;
import com.example.diabetestracker.model.User;
import com.example.diabetestracker.utils.Preferences;
import com.google.android.material.snackbar.Snackbar;

import static java.lang.Boolean.FALSE;

public class Login extends AppCompatActivity {
    //Declaring variables
    EditText eemail,epassword;
    String email,password;
    Preferences utils;
    User user;
    DatabaseHelper dbHelper;
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Intent i=getIntent();
        //Initializing variables
        eemail=findViewById(R.id.email);
        epassword=findViewById(R.id.password);
        utils=new Preferences();
        user=new User();
        dbHelper=new DatabaseHelper(this);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    }
    //Email validation
    private boolean EmailisEmpty()
    {
        email=String.valueOf(eemail.getText());
        if(email.isEmpty()) {
            awesomeValidation.addValidation(this,R.id.email,"^[A-Za-z]+[A-Za-z0-9+_.-]*@(.+)$",R.string.error_email2);
            //change tint color
            eemail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
        else {
            eemail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);
            return false;
        }
    }
    //Password validation
    private boolean PasswordisEmpty()
    {

        password=String.valueOf(epassword.getText());
        if(password.isEmpty()) {
            awesomeValidation.addValidation(this,R.id.password,"^[a-zA-Z0-9]{5,}$",R.string.error_password3);
            epassword.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
        else
        {
            epassword.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);
            return false;
        }

    }

    public void submit(View v)
    {
        if(EmailisEmpty()==FALSE)
        {
            if(PasswordisEmpty()==FALSE)
            {
                if(awesomeValidation.validate())
                {
                    //validation successful
                    getValues();
                    //database working
                    if(dbHelper.checkUser(user.getEmail(),user.getPassword()))
                    {
                        //after database validation

                        utils.saveEmail(user.getEmail(),this);
                        if(utils.getEmail(this)!=null || !utils.getEmail(this).equals(""))
                        {
                            Intent intent=new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        Snackbar.make(v, "Invalid Email or Password", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        }
        else
        {
            epassword.setText("");
        }
    }
    //get values from edit texts and set in User class object
    private void getValues()
    {
        user.setEmail(eemail.getText().toString().trim());
        user.setPassword(epassword.getText().toString().trim());
    }
    //No Account - Onclick
    public void account(View v)
    {
        Intent ii=new Intent(this,Signup.class);
        startActivity(ii);
    }
}