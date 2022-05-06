package com.example.bboxphotoapp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JSONManager {

    private static final String TAG = "JSONManager";

    // json info
    private static JSONObject JSONMain; // main JSON object
    private static String JSONName = new String("ImageBboxInfo.json");
    private static File dir; // JSON file path
    private static File JSONFile; // local JSON file

    // arbitrary value
    private static Uri headImageUri;

    /**
     * Retrieves, if exists, the local main JSON file and initializes it.
     *
     * Gets called in MainActivity.
     *
     * @param context current context
     */
    public static void initJSON(Context context) {
        dir = context.getFilesDir(); // get file path
        JSONFile = new File(dir, JSONName); // JSON file (path, name)

        if(JSONFile.exists()) {
            String strToJSON; // JSON as string

            // try loading local JSON file
            try {
                // init reader objects
                FileReader fr = new FileReader(JSONFile); // read local JSON file
                BufferedReader br = new BufferedReader(fr); // parse as char-input stream
                StringBuilder sb = new StringBuilder(); // convert stream to string

                // read each line as a string and append to StringBuilder object
                String line = br.readLine();
                while(line != null) {
                    sb.append(line).append("\n");
                    line = br.readLine();
                }

                br.close(); // close buffered reader

                strToJSON = sb.toString(); // saves parsed JSON file to string object

                JSONMain = new JSONObject(strToJSON); // init JSONMain with parsed string

                verifyJSON(context); // verifies JSONMain contents
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException | JSONException e) {
                e.printStackTrace();
            }
        } else { JSONMain = new JSONObject(); }
    }

    /**
     * Used during initialization. For any image that a user deletes, it will also be removed
     * from the main JSON file.
     *
     * @param context the current context
     */
    public static void verifyJSON(Context context) {
        Uri id; // uri path counter
        File file; // file to store uri path
        String key; // uri id counter
        List<String> keys = new ArrayList<>(); // stores the ids of current images in MediaStore

        // columns to retrieve
        String[] projection = {MediaStore.Images.Media._ID};

        // the uri path to the primary external storage volume
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // how the query is sorted
        String sortOrder = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

        // query all MediaStore images
        Cursor cursor = context.getContentResolver().query(
                uri, projection, null, null, sortOrder);

        // iterates through all MediaStore images
        while(cursor.moveToNext()) {
            id = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    MediaStore.Images.Media._ID)));

            // stores the uri of the first image.
            // used as the preview image for btnImage in Main
            if(headImageUri == null) { headImageUri = id; }

            file = new File(id.getPath()); // store file at the uri path
            keys.add(file.getName()); // add the ID of the file to the keys list
        }

        cursor.close();

        Iterator<String> iterator = JSONMain.keys();

        while(iterator.hasNext()) {
            key = iterator.next();
            // if a user deletes an image, the JSONObject corresponding to that image
            // will also be removed at the start of the MainActivity.
            if(!keys.contains(key)) {
                iterator.remove();
            }
        }
    }


    /**
     * Saves image information to main JSON
     *
     * @param uri uri of image to save
     */
    public static void saveToJSON(Context context, Uri uri) {
        try{
            int[][] arr = new int[2][2]; // placeholder

            String name = getImageName(context, uri);
            String id = uri.getLastPathSegment();

            // init imageJSON and get main JSON object
            ImageObject imageObject = new ImageObject(name, uri.toString(), "Person", arr);
            JSONObject imageJSON = imageObject.getImageJSON();

            // append ImageObject to JSONMain as imageJSON
            JSONMain.put(id, imageJSON);

            // save/overwrite local JSON file: JSONMain
            saveJSONAsFile();
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Saves/overwrites local JSON file; creates a new one otherwise.
     *
     * Emulator path: data/data/app_name
     */
    public static void saveJSONAsFile() {
        String jsonToStr = JSONMain.toString(); // converts JSONMain to string format
        try {
            FileWriter fw = new FileWriter(JSONFile); // write character file: JSONFile
            BufferedWriter bw = new BufferedWriter(fw); // write file as char-input stream
            bw.write(jsonToStr); // write char-input stream to output file: "ImageBboxInfo.json"
            bw.close(); // close BufferedWriter object
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Locates and returns the display name of an image with its corresponding uri.
     * Returns null if the display name could not be found.
     *
     * @param context the current context
     * @param uri the image uri
     * @return the image name
     */
    public static String getImageName(Context context, Uri uri) {
        String imageName = null;

        if(uri.getScheme().equalsIgnoreCase("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try{
                if(cursor != null && cursor.moveToFirst()) {
                    imageName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return imageName;
    }

    /**
     * Returns the uri of the first image indexed by the JSON verification process.
     *
     * @return uri of the first image indexed
     */
    public static Uri getHeadImageUri() { return headImageUri; }

    /**
     * Returns main JSON file as JSONObject.
     *
     * Typically called to append data or to update key/value pairings.
     *
     * @return the main JSONObject
     */
    public static JSONObject getJSON() { return JSONMain; }
}