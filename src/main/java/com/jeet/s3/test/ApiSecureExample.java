package com.jeet.s3.test;

import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class ApiSecureExample {

    public static void main(String[] args) {
        try {
            String secret = "secret";
            File file = new File("C:\\Users\\LENOVO\\Desktop\\photo.jpg");
            System.out.println(System.getProperty("java.io.tmpdir"));
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC
                    .doFinal(IOUtils.toByteArray(new FileInputStream(file))));
            System.out.println(hash);
        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}
