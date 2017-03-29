package com.jeet.s3;

/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import com.jeet.s3.util.StringUtils;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.jeet.s3.util.Constants;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AmazonS3ClientWrapper {

    private final AmazonS3 s3Client;

    public AmazonS3ClientWrapper(AmazonS3 client) {
        this.s3Client = client;
    }

    public InputStream getObjectStream(String path) {
        try {
            S3Object object = s3Client.getObject(
                    new GetObjectRequest(Constants.BUCKET_NAME, path));
            return object.getObjectContent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public File getObjectFile(String path) {
        try {
            File f = new File(System.getProperty("java.io.tmpdir") + "\\" + UUID.randomUUID() + ".jpg");
            s3Client.getObject(
                    new GetObjectRequest(Constants.BUCKET_NAME, path),
                    f);
            return f;
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return null;
    }

    public boolean uploadFile(String path, File f) {
        boolean isFileUploaded = false;
        try {
            PutObjectResult objectResult = s3Client.putObject(new PutObjectRequest(Constants.BUCKET_NAME, path, f));
            if (!StringUtils.isEmpty(objectResult.getContentMd5())) {
                isFileUploaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFileUploaded;
    }

    public boolean uploadFile(String path, InputStream is, String hash,
            Long fileLength) {
        boolean isFileUploaded = false;
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            Map userMetadata = new HashMap();
            userMetadata.put(Constants.FILE_LENGTH_KEY, fileLength.toString());
            userMetadata.put(Constants.HASH_KEY, hash);
            userMetadata.put(Constants.LAST_MODIFIED_KEY, String.valueOf(new Date().getTime()));
            objectMetadata.setUserMetadata(userMetadata);
            PutObjectResult objectResult = s3Client
                    .putObject(
                            new PutObjectRequest(Constants.BUCKET_NAME, path, is, objectMetadata));
            if (!StringUtils.isEmpty(objectResult.getContentMd5())) {
                isFileUploaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFileUploaded;
    }

    public Date getLastModified(String path) {
        try {
            ObjectMetadata objectMetadata = s3Client.getObjectMetadata(Constants.BUCKET_NAME, path);
            return objectMetadata != null ? objectMetadata.getLastModified() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map getUserMetadata(String path) {
        try {
            ObjectMetadata objectMetadata = s3Client.getObjectMetadata(Constants.BUCKET_NAME, path);
            return objectMetadata != null ? objectMetadata.getUserMetadata() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_MAP;
        }
    }

    public void renameImage(String originalpath, String newpath) {
        try {
            s3Client.copyObject(Constants.BUCKET_NAME,
                    originalpath, Constants.BUCKET_NAME, newpath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String originalpath) {
        try {
            s3Client.deleteObject(Constants.BUCKET_NAME, originalpath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Bucket> listBuckets() {
        try {
            return s3Client.listBuckets();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<String> getListOfFiles(String basepath) {
        try {
            List<String> keys = new ArrayList();
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(Constants.BUCKET_NAME);
//            listObjectsRequest.setDelimiter(basepath);
            listObjectsRequest.setPrefix(basepath);
            ObjectListing objectListing;
            do {
                objectListing = s3Client.listObjects(listObjectsRequest);
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//                    System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
                    keys.add(objectSummary.getKey());
                }
                listObjectsRequest.setMarker(objectListing.getNextMarker());
            } while (objectListing.isTruncated());
            return keys;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String generatePresignedURLForContent(String key) {
        try {
            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += 1000 * 60 * 60; // 1 hour.
            expiration.setTime(msec);
            GeneratePresignedUrlRequest generatePresignedUrlRequest
                    = new GeneratePresignedUrlRequest(Constants.BUCKET_NAME, key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
            generatePresignedUrlRequest.setExpiration(expiration);

            URL s = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return s.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public String generatePresignedURL(String key) {
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest
                    = new GeneratePresignedUrlRequest(Constants.BUCKET_NAME, key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
            URL s = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return s.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
