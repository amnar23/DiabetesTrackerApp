package com.example.diabetestracker.model;

public class Medication {
    String medication,date,time,unit,email;
    double dosage;
    int id;
    //set id
    public void setId(int id){this.id=id;}
    //get id
    public int getId(){return id;}
    //set medication
    public void setMedication(String med){this.medication=med;}
    //get medication
    public String getMedication(){return medication;}
    //set unit
    public void setUnit(String u){this.unit=u;}
    //get unit
    public String getUnit(){return unit;}
    //set date
    public void setDate(String date){this.date=date;}
    //get date
    public String getDate(){return date;}
    //set time
    public void setTime(String t){this.time=t;}
    //get time
    public String getTime(){return time;}
    //set dosage
    public void setDosage(double dos){this.dosage=dos;}
    //get dosage
    public double getDosage(){return dosage;}
    //set email
    public void setEmail(String email){this.email=email;}
    //get email
    public String getEmail(){return  email;}
}
