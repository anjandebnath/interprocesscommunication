package com.demo.AESSecurity;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.demo.AESSecurity.util.SharedPref;


public class AesApp extends MultiDexApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPref.on(this);
    }

    public static Context getAppContext(){
        return context;
    }




}
