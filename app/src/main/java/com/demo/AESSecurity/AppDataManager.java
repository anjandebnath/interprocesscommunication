package com.demo.AESSecurity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class AppDataManager {

    public ISecuritycheck mSecurityService;
    private boolean mShouldUnbind;

    /**
     * singleton to enable the class to be used single object from multiple thread
     */

    public static class AppDataManagerHolder{
        public static AppDataManager appDataManager = new AppDataManager();
    }

    public static AppDataManager getInstance(){
        return  AppDataManagerHolder.appDataManager;
    }


    // since we initialize with application context so it will remain
    // when app is in background state
    void doBindService() {
        if (mSecurityService == null) {
            Intent intent = new Intent(AesApp.getAppContext(), SecurityService.class);
            // this will start the service in foreground mode
            intent.setAction(SecurityService.ACTION_START_FOREGROUND_SERVICE);
            AesApp.getAppContext().startService(intent);
            AesApp.getAppContext().bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
            mShouldUnbind = true;
        }
    }

    void doUnbindService() {

        if (mShouldUnbind) {
            // Release information about the service's state.
            AesApp.getAppContext().unbindService(serviceConnection);
            mShouldUnbind = false;
        }
    }


    // Initialize the service as foreground service and also bind the service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mSecurityService = ISecuritycheck.Stub.asInterface(iBinder);

            // to receive the callback
            try {
                mSecurityService.callbackToAppLayer(uiCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Toast.makeText(AesApp.getAppContext(), "Service Connected", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(AesApp.getAppContext(), "Service Disconnected", Toast.LENGTH_LONG).show();
            mSecurityService = null;
        }
    };


    // We will receive the call back from Service to this class
    private ICallBackToUI.Stub uiCallback = new ICallBackToUI.Stub() {
        @Override
        public void sendCallBackToUi(String publicKey) throws RemoteException {

            Log.e("On App layer", "Data:: "+publicKey);
        }
    };
}
