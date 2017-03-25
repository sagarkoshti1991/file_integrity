/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.s3.util;

import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Patel
 */
public class HashUtil {

    public static String generateFileHash(File file) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(Constants.HASH_SECRET.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hash = Base64.encodeBase64String(sha256_HMAC
                    .doFinal(IOUtils.toByteArray(new FileInputStream(file))));
        } catch (Exception ex) {
            System.out.println("Error in generating hash.");
        }
        return hash;
    }
}
