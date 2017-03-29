/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeet.cli;

import com.jeet.s3.S3Connect;
import com.jeet.s3.util.Constants;
import com.jeet.s3.util.FileUtil;
import com.jeet.s3.util.HashUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class Admin {

    private final static BufferedReader bufferRead
            = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
            askInput();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void askInput() {
        System.out.println("1. List of Files.");
        System.out.println("2. Upload Files.");
        System.out.println("3. Exit.");
        System.out.print("Please select option: ");
        String choise = null;
        try {
            choise = bufferRead.readLine();
        } catch (IOException ioe) {
            System.out.println("Error in reading option.");
            System.out.println("Please try again.");
            askInput();
        }
        if (choise != null && NumberUtils.isDigits(choise)) {
            switch (choise) {
                case "1":
                    listFiles();
                    break;
                case "2":
                    uploadFile();
                    break;
                case "3":
                    System.exit(0);
                    break;
                default:
                    System.err.println("Please select from provided options only.");
                    askInput();
            }
        } else {
            System.err.println("Please enter digits only.");
            askInput();
        }
    }

    private static void listFiles() {
        System.out.println("Retrieving List of Files...");
        List<String> listOfFiles = S3Connect.getListOfFiles(Constants.ROOT_FOLDER);
        int count = 1;
        Map<Integer, String> fileMap = new HashMap();
        for (String fileName : listOfFiles) {
            fileMap.put(count, fileName);
            System.out.println(count + ". "
                    + fileName.replaceAll(Constants.ROOT_FOLDER + "/", ""));
            count++;
        }
        System.out.println("");
        askFileListInputs(fileMap);
    }
    public static final String ANSI_BLUE = "\u001B[34m";

    private static void askFileListInputs(Map fileMap) {
        System.out.println(ANSI_BLUE + "1. Download File.");
        System.out.println(ANSI_BLUE + "2. Check Integrity.");
        System.out.println(ANSI_BLUE + "3. Delete File.");
        System.out.println(ANSI_BLUE + "4. Main Menu.");
        System.out.println(ANSI_BLUE + "5. Exit.");
        System.out.print("Please select option: ");
        String choise = null;
        try {
            choise = bufferRead.readLine();
        } catch (IOException ioe) {
            System.err.println("Error in reading option.");
            System.err.println("Please try again.");
            askFileListInputs(fileMap);
        }
        if (choise != null && NumberUtils.isDigits(choise)) {
            switch (choise) {
                case "1":
                    downloadFile(fileMap);
                    break;
                case "2":
                    checkIntegrity(fileMap);
                    break;
                case "3":
                    deleteFile(fileMap);
                    break;
                case "4":
                    askInput();
                    break;
                case "5":
                    System.exit(0);
                    break;
                default:
                    System.err.println("Please select from provided options only.");
                    askFileListInputs(fileMap);
            }
        } else {
            System.err.println("Please enter digits only.");
            askFileListInputs(fileMap);
        }
    }
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private static void checkIntegrity(Map fileMap) {
        System.out.print("Enter File Index(0 to cancel): ");
        String choise = null;
        try {
            choise = bufferRead.readLine();
        } catch (IOException ioe) {
            System.err.println("Error in reading option.");
            System.err.println("Please try again.");
            checkIntegrity(fileMap);
        }
        if (choise != null && NumberUtils.isDigits(choise)) {
            Integer choiseInt = Integer.parseInt(choise);
            if (fileMap.containsKey(choiseInt)) {
                try {
                    String key = fileMap.get(choiseInt).toString();
                    File file = FileUtil.downloadAndDecryptFile(key);
                    Long fileLength = file.length();
                    if (FileUtil.getHash(key.split("/")[1])
                            .equals(HashUtil.generateFileHash(file))) {
                        Map userMetadata = S3Connect.getUserMetadata(key);
//                        System.out.println(userMetadata.get(Constants.LAST_MODIFIED_KEY));
//                        System.out.println(S3Connect.getLastModified(key).getTime());
                        //check last access time
                        if (userMetadata.containsKey(Constants.LAST_MODIFIED_KEY)) {
                            Long millisFromMettaData = Long.valueOf(userMetadata
                                    .get(Constants.LAST_MODIFIED_KEY).toString());
                            Long millisFromS3 = S3Connect.getLastModified(key).getTime();
                            Seconds difference = Seconds.secondsBetween(new DateTime(millisFromMettaData),
                                    new DateTime(millisFromS3));
                            if (difference.getSeconds() < Constants.LAST_MODIFIED_VARIANT) {
                                //check file length
                                if (userMetadata.containsKey(Constants.FILE_LENGTH_KEY)
                                        && fileLength.toString().equals(userMetadata.get(Constants.FILE_LENGTH_KEY))) {
                                    //check hash from user data
                                    if (userMetadata.containsKey(Constants.HASH_KEY)
                                            && userMetadata.get(Constants.HASH_KEY)
                                                    .equals(FileUtil.getHash(key.split("/")[1]))) {
                                        System.out.println(ANSI_GREEN + "Data integrity is preserved.");
                                    } else {
                                        System.out.println(ANSI_RED + "Data integrity is not preserved.");
                                    }
                                } else {
                                    System.out.println(ANSI_RED + "File is length does not matched.");
                                }
                            } else {
                                System.out.println(ANSI_RED + "File is modified outside the system.");
                            }
                        } else {
                            System.out.println(ANSI_RED + "File is modified outside the system.");
                        }
                    } else {
                        System.out.println(ANSI_RED + "Data integrity is not preserved.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Error in downlaoding file.");
                }
                askFileListInputs(fileMap);
            } else if (choiseInt.equals(0)) {
                System.out.println("Check Integrity file canceled.");
                askFileListInputs(fileMap);
            } else {
                System.err.println("Please select from provided options only.");
                checkIntegrity(fileMap);
            }
        } else {
            System.err.println("Please enter digits only.");
            checkIntegrity(fileMap);
        }
    }

    private static void deleteFile(Map fileMap) {
        System.out.print("Enter File Index(0 to cancel): ");
        String choise = null;
        try {
            choise = bufferRead.readLine();
        } catch (IOException ioe) {
            System.err.println("Error in reading option.");
            System.err.println("Please try again.");
            deleteFile(fileMap);
        }
        if (choise != null && NumberUtils.isDigits(choise)) {
            Integer choiseInt = Integer.parseInt(choise);
            if (fileMap.containsKey(choiseInt)) {
                try {
                    String key = fileMap.get(choiseInt).toString();
                    FileUtil.deleteFile(key);
                    System.out.println("File Deleted");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Error in deleting file.");
                }
                askFileListInputs(fileMap);
            } else if (choiseInt.equals(0)) {
                System.out.println("Delete file canceled.");
                askFileListInputs(fileMap);
            } else {
                System.err.println("Please select from provided options only.");
                deleteFile(fileMap);
            }
        } else {
            System.err.println("Please enter digits only.");
            deleteFile(fileMap);
        }
    }

    private static void downloadFile(Map fileMap) {
        System.out.print("Enter File Index(0 to cancel): ");
        String choise = null;
        try {
            choise = bufferRead.readLine();
        } catch (IOException ioe) {
            System.err.println("Error in reading option.");
            System.err.println("Please try again.");
            downloadFile(fileMap);
        }
        if (choise != null && NumberUtils.isDigits(choise)) {
            Integer choiseInt = Integer.parseInt(choise);
            if (fileMap.containsKey(choiseInt)) {
                try {
                    String key = fileMap.get(choiseInt).toString();
                    File file = FileUtil.downloadAndDecryptFile(key);
                    String downloadFilePath = Constants.DOWNLOAD_FOLDER
                            + fileMap.get(choiseInt).toString();
                    downloadFilePath = downloadFilePath.replaceAll("/", "\\\\");
                    Files.copy(file.toPath(), (new File(downloadFilePath)).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File Downloaded to: " + downloadFilePath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Error in downlaoding file.");
                }
                askFileListInputs(fileMap);
            } else if (choiseInt.equals(0)) {
                System.out.println("Download file canceled.");
                askFileListInputs(fileMap);
            } else {
                System.err.println("Please select from provided options only.");
                downloadFile(fileMap);
            }
        } else {
            System.err.println("Please enter digits only.");
            downloadFile(fileMap);
        }
    }

    private static void uploadFile() {
        String filePath = getFilePath();
        if (filePath.equalsIgnoreCase("0")) {
            System.out.println(ANSI_RED + "File upload canceled.");
            askInput();
        }
        File f = new File(filePath);
        if (!f.exists()) {
            System.err.println(ANSI_RED + "Error in reading file: " + filePath);
            System.err.println(ANSI_BLUE + "Please try again.");
            uploadFile();
        }
        System.out.println("Checking whether file is already exist or not.");
        if (FileUtil.fileExist(f.getName())) {
            System.err.println("The destination already has a file named \"" + f.getName() + "\"");
            System.out.println(ANSI_BLUE + "Do you want to replace it ? (Y/N)");
            if (!fileExist(f)) {
                System.out.println(ANSI_RED + "File Uploding canceled.");
                askInput();
            }
        }
        boolean fileUploaded = FileUtil.encryptAndUploadFile(f);
        if (fileUploaded) {
            System.out.println(ANSI_GREEN + "File uploaded successfully.");
            askInput();
        } else {
            System.err.println(ANSI_RED + "Error in Uploading File");
            System.err.println("Please try again.");
            uploadFile();
        }
    }

    private static String getFilePath() {
        String filePath = null;
        try {
            System.out.print("Enter File path(0 to cancel): ");
            filePath = bufferRead.readLine();
        } catch (IOException ioe) {
            System.err.println("Error in reading file path.");
            System.err.println("Please try again.");
            getFilePath();
        }
        return filePath;
    }

    private static boolean fileExist(File file) {
        String choise = "";
        try {
            choise = bufferRead.readLine();
        } catch (IOException ioe) {
            System.err.println("Error in reading option.");
            System.err.println("Please try again.");
            fileExist(file);
        }
        if (choise.equalsIgnoreCase("Y")) {
            return true;
        } else if (choise.equalsIgnoreCase("N")) {
            return false;
        } else {
            System.out.println("Please select Yes(Y) or No(N).");
            fileExist(file);
        }
        return false;
    }
}
