package com.demo;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.demo.aesclient.util.SharedPref;


public class AesApp extends MultiDexApplication {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPref.on(this);
    }

    public static Context getContext(){
        return context;
    }



}
