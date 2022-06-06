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
 */
public class ZipManager {

    private static final String TAG = "ZipManager";
    
    private String zipName;
    private String saveLocation;
    private File zipPath;

    /**
     * Constructor method.
     */
    public ZipManager() {
        
    }

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
        this.zipName = PrefsManager.getValue(PrefsManager.saveNameKey);
    }
    
    public boolean zip() {
        // create new zip file; saves at arg0 (saveLocation) with name 
        // arg1 (String.format("%s.zip", this.zipName))
        File zipFile = new File(saveLocation, String.format("%s.zip", this.zipName));
        
        try {
            // init ZipStream with output zip File
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            zipSubDir(out, zipPath);
            out.close();
        } catch(IOException e) {
            Log.d(TAG, "M/zip: IOException");
            e.printStackTrace();
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
            Log.d(TAG, "M/zipSubDir: IOException");
            e.printStackTrace();
        }
        
    }
}