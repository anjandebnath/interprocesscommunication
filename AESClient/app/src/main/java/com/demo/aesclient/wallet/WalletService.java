package com.demo.aesclient.wallet;

import android.content.Context;
import android.text.TextUtils;


import com.demo.aesclient.crypto.ECDSA;
import com.demo.aesclient.util.HandlerUtil;
import com.demo.aesclient.util.MeshLog;
import com.demo.aesclient.util.SharedPref;

import org.web3j.crypto.Credentials;

import java.io.File;

public class WalletService {
    private Context mContext;
    private final String walletSuffixDir = "aes_wallet/";
    public static final String PASSWORD = "987654321";
    private volatile Credentials mCredentials;
    private final String WALLET_ADDRESS = "wallet_address";
    private final String WALLET_FILE_NAME = "wallet_name";
    private final String PUBLIC_KEY = "public_key";
    private final String PRIVATE_KEY = "private_key";
    private static WalletService walletService;

    public interface Listener {
        void onWalletLoaded(String walletAddress, String publicKey, String privateKey);

        void onErrorOccurred(String message);
    }

    private WalletService(Context context) {
        this.mContext = context;
    }

    public static WalletService getInstance(Context context) {
        if (walletService == null) {
            walletService = new WalletService(context);
        }

        return walletService;
    }

    public boolean isWalletExists() {
        boolean isWalletExists = false;

        String filePath = Web3jWalletHelper.onInstance(mContext).getWalletDir(walletSuffixDir);
        File directory = new File(filePath);

        File[] list = directory.listFiles();
        if (list != null) {
            for (File f : list) {
                String name = f.getName();
                if (name.endsWith(".json")) {
                    SharedPref.write(WALLET_FILE_NAME, name);
                    isWalletExists = true;
                    break;
                }
            }
        }

        return isWalletExists;
    }

    public void createOrLoadWallet(final String password, final Listener listener) {
        HandlerUtil.postBackground(() -> {
            String existAddress = SharedPref.read(WALLET_ADDRESS);
            if (!TextUtils.isEmpty(existAddress)) {
                MeshLog.v("Ethereum address already exist");
                listener.onWalletLoaded(existAddress, SharedPref.read(PUBLIC_KEY), SharedPref.read(PRIVATE_KEY));
                initCredential();
                return;
            }

            if (isWalletExists()) {
                String keyStoreFileName = SharedPref.read(WALLET_FILE_NAME);
                loadWalletFromKeystore(PASSWORD, keyStoreFileName);

                if (mCredentials != null) {

                    listener.onWalletLoaded(mCredentials.getAddress(),
                            mCredentials.getEcKeyPair().getPublicKey().toString(16),
                            mCredentials.getEcKeyPair().getPrivateKey().toString(16));

                } else {
                    listener.onErrorOccurred("Wallet credential load failed");
                }
            } else {
                String keyStoreFileName = Web3jWalletHelper.onInstance(mContext).createWallet(password, walletSuffixDir);

                if (keyStoreFileName != null) {

                    loadWalletFromKeystore(PASSWORD, keyStoreFileName);

                    if (mCredentials != null) {

                        listener.onWalletLoaded(mCredentials.getAddress(),
                                mCredentials.getEcKeyPair().getPublicKey().toString(16),
                                mCredentials.getEcKeyPair().getPrivateKey().toString(16));


                    } else {

                        listener.onErrorOccurred("Wallet credential load failed");
                    }
                } else {

                    listener.onErrorOccurred("Wallet file generate failed");
                }
            }
        });
    }

    /**
     * load wallet and public key nd private key is stored into shared preference
     * @param password
     * @param keyStoreFileName
     */
    private void loadWalletFromKeystore(String password, String keyStoreFileName) {
        mCredentials = Web3jWalletHelper.onInstance(mContext).getWallet(password, walletSuffixDir, keyStoreFileName);
        SharedPref.write(WALLET_ADDRESS, mCredentials.getAddress());
       // SharedPref.write(PUBLIC_KEY, mCredentials.getEcKeyPair().getPublicKey().toString(16));
        SharedPref.write(PRIVATE_KEY, mCredentials.getEcKeyPair().getPrivateKey().toString(16));
        SharedPref.write(PUBLIC_KEY, ECDSA.getHexEncodedPoint(SharedPref.read(PRIVATE_KEY)));
    }

    public Credentials getCredentials() {
        if (mCredentials == null) {
            String privateKey = SharedPref.read(PRIVATE_KEY);
            if (TextUtils.isEmpty(privateKey)) {
                String keyStoreFileNmae = SharedPref.read(WALLET_FILE_NAME);
                loadWalletFromKeystore(PASSWORD, keyStoreFileNmae);
            } else {
                mCredentials = Credentials.create(privateKey);
            }
        }
        return mCredentials;
    }

    private void initCredential() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCredentials();
            }
        }).start();

    }
}
