/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.jeet.s3.util.Constants;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class S3Connect {

//    private static final AmazonS3Client s3 = new AmazonS3Client(new AWSCredentials() {
//
//        @Override
//        public String getAWSAccessKeyId() {
//            return Constants.AWS_ACCESS_KEY;
//        }
//
//        @Override
//        public String getAWSSecretKey() {
//            return Constants.AWS_SECRET_KEY;
//        }
//    });
//    private static final AmazonS3Client s3 = new AmazonS3Client(new ProfileCredentialsProvider());
    private static final BasicAWSCredentials BASIC_AWS_CREDENTIALS
            = new BasicAWSCredentials(Constants.AWS_ACCESS_KEY,
                    Constants.AWS_SECRET_KEY);
    private static final AmazonS3 AMAZON_S3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(BASIC_AWS_CREDENTIALS)).build();

    public static InputStream getS3Image(String path) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.getObjectStream(path);
    }

    public static File getS3File(String path) {
        try {
            AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
            AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
            return amazonS3ClientWrapper.getObjectFile(path);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean uploadFile(String path, File f) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.uploadFile(path, f);
    }

    public static boolean uploadFile(String path, InputStream is, String hash) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.uploadFile(path, is, hash);
    }

    public static void renameTempImage(String p1, String p2) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        amazonS3ClientWrapper.renameImage(p1, p2);
    }

    public static List<String> getListOfFiles(String path) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.getListOfFiles(path);
    }

    public static List<Bucket> listBuckets() {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.listBuckets();
    }

    public static void deleteFile(String p1) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        amazonS3ClientWrapper.deleteFile(p1);
    }

    public static String getUrlWithExpireTime(String key) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.generatePresignedURLForContent(key);
    }

    public static String getUrl(String key) {
        AMAZON_S3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        AmazonS3ClientWrapper amazonS3ClientWrapper = new AmazonS3ClientWrapper(AMAZON_S3);
        return amazonS3ClientWrapper.generatePresignedURL(key);
    }

}
