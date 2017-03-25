/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.s3.util;

public class Constants {

    public static final String HASH_SECRET = "random text for secret";
    public static final String ROOT_FOLDER = "root";
    public static final String HASH_FOLDER = "root/hash";
    public static final String TEMP_FOLDER = System.getProperty("TEMP_FOLDER");
    public static final String DOWNLOAD_FOLDER = System.getProperty("DOWNLOAD_FOLDER");
    public static final String BUCKET_NAME = System.getProperty("BUCKET_NAME");
    public static final String AWS_ACCESS_KEY = System.getProperty("AWS_ACCESS_KEY");
    public static final String AWS_SECRET_KEY = System.getProperty("AWS_SECRET_KEY");
    public static final String HASH_KEY = "HASH";
    public static final String LAST_MODIFIED_KEY = "LAST_MODIFIED";
}
