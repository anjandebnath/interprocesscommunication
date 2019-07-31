package com.demo.AESSecurity.util;

import android.util.Log;

public class MeshLog {

    private static String TAG = "MeshLog";




    public static void e(String msg){
/*      e(TAG, msg);
        writeText(msg, true);*/
    }


    public static void p(String msg){
 /*       p(TAG, msg);
        writeText(msg, true);*/
    }

    public static void k(String msg){
/*        e(TAG, msg);
        writeText(msg, true);*/
    }



    public static void i(String msg){
//        i(TAG, msg);
//        writeText(msg, true);
    }



    public static void e(String tag, String msg){
        Log.e(tag, msg);
    }

    public static void p(String tag, String msg){
        Log.e(tag, msg);
    }

    public static void i(String tag, String msg){
        Log.i(tag, msg);
    }
    public static void d(String msg){
        /*d(TAG, msg);
        writeText(msg, true);*/
    }

    public static void d(String tag, String msg){
        Log.d(tag, msg);
    }

    public static void v(String msg){
/*        v(TAG, msg);
        writeText(msg, true);*/
    }

    public static void v(String tag, String msg){
        Log.v(tag, msg);
    }

}
