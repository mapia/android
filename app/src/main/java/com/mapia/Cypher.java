package com.mapia;

import android.util.Base64;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by daehyun on 2015. 5. 2..
 */
public class Cypher {


    public static RSAPublicKey getPublicKeyFromString(String key)
            throws IOException, GeneralSecurityException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                Base64.decode(key, Base64.DEFAULT));
        RSAPublicKey pubKey = null;

        try {
            pubKey = (RSAPublicKey) kf.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKey;
    }

    public static String encrypt(String rawText, RSAPublicKey publicKey)
            throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(
                "RSA/NONE/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return new String(Base64.encode(
                cipher.doFinal(rawText.getBytes()), Base64.DEFAULT));
    }

}
