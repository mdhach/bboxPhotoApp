package com.example.bboxphotoapp;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Code sourced from stackoverflow:
 *  
 *      https://stackoverflow.com/a/53960424/15224280
 *      
 * Zips file at given path; recursively zips all files if argument is a directory.
 * 
 */
public class ZipManager {

    private static final String TAG = "ZipManager";
    
    private String zipName;
    private String saveLocation;
    private File zipPath;

    /**
     * Overload constructor method.
     * 
     * Retrieves and sets source path and save location.
     * 
     * @param sourcePath the path of the directory to zip
     * @param saveLocation the path where the zipped file is saved
     */
    public ZipManager(String sourcePath, String saveLocation) {
        this.zipPath = new File(sourcePath);
        this.saveLocation = saveLocation;
        
        String name = PrefsManager.getValue(PrefsManager.saveNameKey);
        this.zipName = String.format("%s.zip", name);
    }

    /**
     * Utilizes ZipOutputStream to recursively zip files at sourcePath
     * 
     * @return true if process completes; false if an exception was thrown
     */
    public boolean zip() {
        // create new zip file; saves at saveLocation with name zipName
        File zipFile = new File(saveLocation, zipName);
        
        try {
            // init ZipStream with output zip File
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            zipSubDir(out, zipPath);
            out.close();
        } catch(IOException e) {
            Log.d(TAG, "M/zip: IOException...");
            Log.d(TAG, "M/zip: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Recursive function to zip files
     * 
     * @param out the ZipStream for the output File
     * @param folder target file(s) to be zipped
     */
    private void zipSubDir(ZipOutputStream out, @NonNull File folder) {
        File[] fileList = folder.listFiles();
        BufferedInputStream origin;
        
        final int BUFFER = 2048;
        
        if(fileList != null) {
            try {
                for(File file : fileList) {
                    if(file.isDirectory()) {
                        zipSubDir(out, file);
                    } else {
                        byte[] data = new byte[BUFFER];

                        String unmodifiedFilePath = file.getPath();
                        int lastIdx = unmodifiedFilePath.lastIndexOf("/") + 1;
                        String relativePath = unmodifiedFilePath.substring(lastIdx);

                        FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                        origin = new BufferedInputStream(fi, BUFFER);

                        ZipEntry entry = new ZipEntry(relativePath);
                        entry.setTime(file.lastModified());
                        out.putNextEntry(entry);

                        int count;
                        while((count=origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                        }
                        origin.close();
                        out.closeEntry();
                    }
                }
            } catch(IOException e) {
                Log.d(TAG, "M/zipSubDir: IOException...");
                Log.d(TAG, "M/zipSubDir: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "M/zipSubDir: No images found.");
        }
    }
}