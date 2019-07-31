package com.demo.aesclient;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.demo.AESSecurity.ISecuritycheck;
import com.demo.aesclient.util.MeshLog;
import com.demo.aesclient.wallet.WalletService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ISecuritycheck securitycheck;
    private ProgressBar simpleProgressBar;
    private EditText etPublicKey;
    private EditText etAppName;
    private EditText etClientName;
    private Button sendButton;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(getApplicationContext(),"AES Service Connected", Toast.LENGTH_LONG).show();
            securitycheck =  ISecuritycheck.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(),"AES Service DisConnected", Toast.LENGTH_LONG).show();
            securitycheck = null;
        }
    };


    private void initServiceConnection() {
        if (securitycheck == null) {
            Intent intent = new Intent(ISecuritycheck.class.getName());

            /*this is service name that is associated with server end*/
            intent.setAction("service.aes_security");

            /*From 5.0 annonymous intent calls are suspended so replacing with server app's package name*/
            intent.setPackage("com.demo.AESSecurity");

            // binding to remote service
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleProgressBar = findViewById(R.id.progressBar);
        etPublicKey = findViewById(R.id.editTextpublicKey);
        etAppName = findViewById(R.id.editTextAppName);
        etClientName = findViewById(R.id.editTextUserName);
        sendButton = findViewById(R.id.buttonSend);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                simpleProgressBar.setVisibility(View.VISIBLE);

                WalletService.getInstance(MainActivity.this).createOrLoadWallet(WalletService.PASSWORD, new WalletService.Listener() {
                    @Override
                    public void onWalletLoaded(String walletAddress, String publicKey, String privateKey) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                simpleProgressBar.setVisibility(View.GONE);
                                MeshLog.d("key public:: ", publicKey);
                                MeshLog.d("key private ::  ", privateKey);
                                etPublicKey.setText(publicKey);
                            }
                        });


                    }

                    @Override
                    public void onErrorOccurred(String message) {
                        MeshLog.d("key error:: ", message);
                    }
                });

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

        sendButton.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(etPublicKey.getText().toString())){

                String publicKey = etPublicKey.getText().toString();
                String appName = etAppName.getText().toString();
                String clientName = etClientName.getText().toString();

                try {
                    if(securitycheck!= null){

                    }
                    String response = securitycheck.apiResponse(clientName, "unicef@w3.com", appName, publicKey);
                    Toast.makeText(this, response+"", Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        initServiceConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        unbindService(serviceConnection);
    }
}
