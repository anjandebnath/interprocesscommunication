package com.demo.AESSecurity.crypto;

import android.content.Context;

import com.demo.AESSecurity.util.MeshLog;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;

public class CryptoHelper {

    public static void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public static String encrypt(String privateKey, String otherPartyPoint, byte[] plainText) {
        BigInteger sharedSecret = ECDSA.generateBasicSharedSecret(privateKey, otherPartyPoint);
        String secretKey = Numeric.toHexStringNoPrefix(sharedSecret);
        MeshLog.v("sendPayMessage: secretKey: " + secretKey);
        return AES.encryptString(secretKey, plainText);
    }

    public static String decrypt(String privateKey, String otherPartyPoint, String cipherText) {
        BigInteger sharedSecret = ECDSA.generateBasicSharedSecret(privateKey, otherPartyPoint);
        String secretKey = Numeric.toHexStringNoPrefix(sharedSecret);
        MeshLog.v("onMessageReceived: secretKey: " + secretKey);
        MeshLog.v("onMessageReceived: cipherText: " + cipherText);
        return AES.decryptString(secretKey, cipherText);
    }

    /*public static String decryptMessage(Context context, String senderId, String message) {
        TransportManager transportManager = TransportManager.getInstance();
        WalletService walletService = WalletService.getInstance(context);
        String privateKey = walletService.getCredentials().getEcKeyPair().getPrivateKey().toString(16);
        String publicPoint = transportManager.getPublicKeyByAddress(senderId);
        String encryptedMessage = CryptoHelper.decrypt(privateKey, publicPoint, message);
        return encryptedMessage;
    }

    public static String encryptMessage(Context context, String receiverId, byte[] message) {
        TransportManager transportManager = TransportManager.getInstance();
        WalletService walletService = WalletService.getInstance(context);
        String privateKey = walletService.getCredentials().getEcKeyPair().getPrivateKey().toString(16);
        String publicPoint = transportManager.getPublicKeyByAddress(receiverId);
        String encryptedMessage = CryptoHelper.encrypt(privateKey, publicPoint, message);
        return encryptedMessage;
    }*/
}
