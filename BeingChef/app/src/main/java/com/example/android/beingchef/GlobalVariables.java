package com.example.android.beingchef;

import android.app.Application;

/**
 * Created by Pallav on 7/18/2017.
 */
//could use shared preferences instead
public class GlobalVariables extends Application {

    private String location="";
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
//    @Override
//    public void onCreate() {
//        //reinitialize variable
//    }
}
