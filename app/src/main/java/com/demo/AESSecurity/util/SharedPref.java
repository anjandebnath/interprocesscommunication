package com.demo.AESSecurity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.demo.AESSecurity.R;


/*
 * ****************************************************************************
 * * Copyright © 2018 W3 Engineers Ltd., All rights reserved.
 * *
 * * Created by:
 * * Name : SUDIPTA KUMAR PAIK
 * * Date : 2/1/18
 * * Email : sudipta@w3engineers.com
 * *
 * * Purpose : SharedPref for all type of SharedPreferences functionality
 * *
 * * Last Edited by :  Md. Nomanur Rashid on 5 May 2018.
 * *
 * * Last Reviewed by :
 * ****************************************************************************
 */

public class SharedPref {


    private static SharedPreferences preferences;
    private static SharedPref sharedPref;
    private static SharedPreferenceAPIClient apiClient;

    private SharedPref() {
    }

    public SharedPref(Context mContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static SharedPref on(Context context) {

        preferences = context.getSharedPreferences(SharedPreferenceAPI.PREFERENCE_NAME, Context.MODE_PRIVATE);


        apiClient = new SharedPreferenceAPIClient(context,context.getString(R.string.api_authority));

        //preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        if (sharedPref == null) {
            sharedPref = new SharedPref();
        }
        return sharedPref;
    }

    public static boolean write(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean write(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(key, value);

        return editor.commit();
    }

    public static boolean write(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static boolean write(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }


    public static String read(String key) {

        return apiClient.getString(key, "");
    }

    public static String read(String key, String defaultValue) {
        return apiClient.getString(key, defaultValue);
    }

    public static long readLong(String key) {
        return apiClient.getLong(key, 0l);
    }

    public static int readInt(String key) {
        return readInt(key, 0);
    }

    public static int readInt(String key, int defaultValue) {
        return apiClient.getInt(key, defaultValue);
    }

    public static boolean readBoolean(String key) {
        return readBoolean(key, false);
    }

    public static boolean readBoolean(String key, boolean defaultValue) {
        return apiClient.getBoolean(key, defaultValue);
    }

    public static boolean readBooleanWithDefaultValue(String key, boolean value) {
        return apiClient.getBoolean(key, value);
    }

    public boolean readSettingsBoolean(String key) {
        return preferences.getBoolean(key, true);
    }

    public static boolean readBooleanDefaultTrue(String key) {
        return apiClient.getBoolean(key, true);
    }

    public static boolean contains(String key) {
        return preferences.contains(key);
    }



}