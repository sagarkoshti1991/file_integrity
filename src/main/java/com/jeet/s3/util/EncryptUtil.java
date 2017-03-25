/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.s3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {

    private static String salt = "jeetvyas";
    private static final int iterations = 65536;
    private static final int keySize = 256;
    private static byte[] ivBytes;
    private static final String password = "P@ssw0rd";

    private static SecretKey secretKey;

    public static void main(String[] args) throws Exception {
//        salt = getSalt();
        char[] message = "PasswordToEncrypt".toCharArray();
        System.out.println("Message: " + String.valueOf(message));
        File encFile = encryptFile(new File("D:\\Research\\project\\photo.jpg"));
        File outFile = decrypt(encFile);
        System.out.println("hello");
    }

    public static File encryptFile(File file) throws Exception {
        // file to be encrypted
        FileInputStream inFile = new FileInputStream(file);
        String ext = FileUtil.getFileExtension(file);
        // encrypted file
        File outFile = File.createTempFile("enc", ext);
        FileOutputStream outFileOutputStream = new FileOutputStream(outFile);

        // password, iv and salt should be transferred to the other end
        // in a secure manner
        // salt is used for encoding
        // writing it to a file
        // salt should be transferred to the recipient securely
        // for decryption
        FileOutputStream saltOutFile = new FileOutputStream("salt.enc");
        saltOutFile.write(salt.getBytes());
        saltOutFile.close();

        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536,
                256);
        SecretKey secretKey = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        // iv adds randomness to the text and just makes the mechanism more
        // secure
        // used while initializing the cipher
        // file to store the iv
        FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        ivOutFile.write(iv);
        ivOutFile.close();

        //file encryption
        byte[] input = new byte[64];
        int bytesRead;

        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null) {
                outFileOutputStream.write(output);
            }
        }

        byte[] output = cipher.doFinal();
        if (output != null) {
            outFileOutputStream.write(output);
        }

        inFile.close();
        outFileOutputStream.flush();
        outFileOutputStream.close();

        return outFile;
    }

    public static File decrypt(File file) throws Exception {
        // reading the salt
        // user should have secure mechanism to transfer the
        // salt, iv and password to the recipient
        FileInputStream saltFis = new FileInputStream("salt.enc");
        saltFis.read(salt.getBytes());
        saltFis.close();

        // reading the iv
        FileInputStream ivFis = new FileInputStream("iv.enc");
        byte[] iv = new byte[16];
        ivFis.read(iv);
        ivFis.close();

        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536,
                256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // file decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        FileInputStream fis = new FileInputStream(file);
        String ext = FileUtil.getFileExtension(file);
        File outFile = File.createTempFile("abc", ext);
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] in = new byte[64];
        int read;
        while ((read = fis.read(in)) != -1) {
            byte[] output = cipher.update(in, 0, read);
            if (output != null) {
                fos.write(output);
            }
        }

        byte[] output = cipher.doFinal();
        if (output != null) {
            fos.write(output);
        }
        fis.close();
        fos.flush();
        fos.close();
        return outFile;
    }

    public static String getSalt() throws Exception {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[20];
        sr.nextBytes(salt);
        return new String(salt);
    }
}
