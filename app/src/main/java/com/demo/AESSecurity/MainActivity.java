package com.demo.AESSecurity;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.demo.AESSecurity.util.MeshLog;
import com.demo.AESSecurity.util.SharedPref;
import com.demo.AESSecurity.wallet.WalletService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDataManager.getInstance().doBindService();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {


                WalletService.getInstance(MainActivity.this).createOrLoadWallet(WalletService.PASSWORD, new WalletService.Listener() {
                    @Override
                    public void onWalletLoaded(String walletAddress, String publicKey, String privateKey) {

                        MeshLog.d("key public:: ", publicKey);
                        MeshLog.d("key private ::  ", privateKey);

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
    }


}
