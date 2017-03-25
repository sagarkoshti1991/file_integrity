/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.s3.test;

import com.jeet.s3.S3Connect;
import java.io.File;
import java.util.List;

/**
 *
 * @author Patel
 */
public class Main {

    public static void main(String[] args) {
//        List<Bucket> listBuckets = S3Connect.listBuckets();
//        listBuckets.stream()
//                .forEach(buckect -> System.out.println(buckect.getName()));
//     
//        uploadTest();
//        listFile("janir/");
        //      downloadTest();
    }

    private static void uploadTest() {
        File f = new File("C:\\Users\\LENOVO\\Desktop\\photo.jpg");
        S3Connect.uploadFile("C:\\Users\\LENOVO\\Desktop" + f.getName(), f);
    }

    private static void downloadTest() {
        String url = S3Connect.getUrl("C:\\Users\\LENOVO\\Desktop\\photo.jpg");
        System.out.println(url);
    }

    private static void listFile(String str) {
        List<String> listOfFiles = S3Connect.getListOfFiles(str);
        listOfFiles.stream()
                .forEach(System.out::println);
    }
}
