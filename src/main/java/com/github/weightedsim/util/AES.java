package com.github.weightedsim.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final SecureRandom rnd = new SecureRandom();
    private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final IvParameterSpec ivSpec = new IvParameterSpec(iv);

    public static SecretKey getRandomKey(int keySize){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize, rnd);
            return keyGenerator.generateKey();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(byte[] bytesToEncrypt, SecretKey secretKey)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            return cipher.doFinal(bytesToEncrypt);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }


    public static byte[] decrypt(byte[] bytesToDecrypt, SecretKey secretKey)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return cipher.doFinal(bytesToDecrypt);
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }



}


