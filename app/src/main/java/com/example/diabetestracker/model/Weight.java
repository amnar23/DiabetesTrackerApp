package com.example.diabetestracker.model;

public class Weight {
    int id;
    double weight;
    String date,time,email;
    //set id
    public void setId(int id){this.id=id;}
    //get id
    public int getId(){return id;};
    //set weight
    public void setWeight(double w){this.weight=w;}
    //get weight
    public double getWeight(){return weight;}
    //set date
    public void setDate(String date){this.date=date;}
    //get date
    public String getDate(){return date;}
    //set time
    public void setTime(String time){this.time=time;}
    //get time
    public String getTime(){return time;}
    //set concentration
    public void setEmail(String emaill){this.email=emaill;}
    //get concentration
    public String getEmail(){return email;}
}
