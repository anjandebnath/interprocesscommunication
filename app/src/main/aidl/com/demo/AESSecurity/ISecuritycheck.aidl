// ISecuritycheck.aidl
package com.demo.AESSecurity;
import com.demo.AESSecurity.ICallBackToUI;

// Declare any non-default types here with import statements

interface ISecuritycheck {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    String apiResponse(String name, String email, String appName, String apiKey);




    // to get notified anynomously when any call back need to send to Ui
    // this is only for server app
    // testing purpose we are using it
    void callbackToAppLayer(ICallBackToUI callbackToUi);
}
