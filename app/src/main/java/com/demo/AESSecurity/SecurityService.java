package com.demo.AESSecurity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.demo.AESSecurity.util.SharedPref;
import com.demo.AESSecurity.util.SharedPreferenceAPIClient;

import java.util.ArrayList;
import java.util.List;

import static com.demo.AESSecurity.wallet.WalletService.PUBLIC_KEY;

public class SecurityService extends Service {

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_STOP = "ACTION_STOP";


    SharedPreferenceAPIClient apiClient;
    private SendCallBackToUi mSendCallBackToUi;

    private List<ICallBackToUI>callBackToUIS;


    @Override
    public IBinder onBind(Intent intent) {
        return securityServiceBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //In your other app where you want to access these values do this
        //Note that you would need to know which authority to access i.e the api root path
        apiClient = new SharedPreferenceAPIClient(getApplicationContext(), getApplicationContext().getString(R.string.api_authority));

        mSendCallBackToUi = new SendCallBackToUi();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
//                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP:
                    stopForegroundService();
//                    Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_LONG).show();
                    break;

            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * Need to ensure service process is destroyed when service is destroyed
         * This solution is not as perfect as expected we need to investigate in future.
         */
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END............");
    }


    private final ISecuritycheck.Stub securityServiceBinder = new ISecuritycheck.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String apiResponse(String name, String email, String appName, String apiKey) throws RemoteException {


            // send the client's parameters to server's app layer
            if(mSendCallBackToUi!= null){
                mSendCallBackToUi.sendCallBackToUi(apiKey);
            }

            /**
             * On RPC mechanism when client request for this data
             * server will response with data
             */

            String publicKey = SharedPref.read(PUBLIC_KEY);
            return "Server response:: Server Public key: "+ publicKey;
        }

        @Override
        public void callbackToAppLayer(ICallBackToUI callbackToUi) throws RemoteException {


            //Add the observer into list so that they will be notified

            // When new screen (Activity) get attached then this service will be bounded newly

            callBackToUIS = new ArrayList<>();
            callBackToUIS.add(callbackToUi);

            Log.e("service", "ui callback initialized");

        }


    };


    private class SendCallBackToUi implements ICallBackToUI{

        @Override
        public void sendCallBackToUi(String publicKey) throws RemoteException {
            callBackToUIS.get(0).sendCallBackToUi(publicKey);
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }


    /* Used to build and start foreground service. */
    private void startForegroundService() {
        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("AESService is running a background service.");
        bigTextStyle.bigText("You can stop the service by clicking on 'Stop Service' button. ");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.notification);

        // Make the notification max priority.
        //builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        //builder.setFullScreenIntent(pendingIntent, true);


        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, SecurityService.class);
        playIntent.setAction(ACTION_STOP);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.btn_star, "Stop Service", pendingPlayIntent);
        builder.addAction(playAction);



        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {


        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
        Log.e("service", "stop");

    }

}
