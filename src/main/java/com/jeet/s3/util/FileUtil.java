/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.s3.util;

import com.jeet.s3.S3Connect;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Patel
 */
public class FileUtil {

    public static boolean encryptAndUploadFile(File file) {
        //generate Hash code and upload to hash folder        
        String hash = HashUtil.generateFileHash(file);
        try {
            File hashFile = new File(System.getProperty("java.io.tmpdir")
                    + file.getName() + ".txt");
            FileOutputStream fos = new FileOutputStream(hashFile);
            fos.write(hash.getBytes());
            System.out.println("Hash file generated.");
            boolean hashUploaded = S3Connect.uploadFile(Constants.HASH_FOLDER
                    + "/" + hashFile.getName(), hashFile);
            if (hashUploaded) {
                System.out.println("Hash uploaded.");
            } else {
                System.out.println("Error in uploading hash file.");
                return false;
            }
        } catch (IOException ex) {
            System.out.println("Error while generating hash file");
            return false;
        }
        //encrypt file
        System.out.println("File Encrypted.");
        File encFile;
        try {
            encFile = EncryptUtil.encryptFile(file);
            return S3Connect.uploadFile(Constants.ROOT_FOLDER
                    + "/" + file.getName(), new FileInputStream(encFile), hash);
        } catch (Exception ex) {
//            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static File downloadAndDecryptFile(String key) {
        File file = S3Connect.getS3File(key);
        try {
            File decFile = EncryptUtil.decrypt(file);
            return decFile;
        } catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    public static void deleteFile(String key) {
        S3Connect.deleteFile(key);
        S3Connect.deleteFile(Constants.HASH_FOLDER + "/" + key.split("/")[1] + ".txt");
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf("."));
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean fileExist(String fileName) {
        return S3Connect.getS3File(Constants.ROOT_FOLDER
                + "/" + fileName) != null;
    }

    public static String getHash(String fileName) throws Exception {

        File hashFile = S3Connect.getS3File(Constants.HASH_FOLDER
                + "/" + fileName + ".txt");
        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(hashFile);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                stringBuilder.append(strLine);
            }
        }
        return stringBuilder.toString();
    }
}
