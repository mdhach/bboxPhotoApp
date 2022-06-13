package com.example.bboxphotoapp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used to statically access and perform operations on the main JSON object or file.
 * 
 */
public final class JSONManager {

    private static final String TAG = "JSONManager";

    // json info
    private static JSONObject JSONMain; // main JSON object
    private static File JSONFile; // local JSON file

    // arbitrary value used to preview an image
    private static Uri headImageUri;

    /**
     * Retrieves, if exists, the local main JSON file and initializes it.
     *
     * Gets called in MainActivity.
     *
     * @param context current activity context
     */
    public static void initJSON(Context context) {
        String JSONName = context.getString(R.string.default_json_name);
        String sourcePath = PrefsManager.getValue(PrefsManager.saveLocKey);

        JSONFile = new File(sourcePath, JSONName); // JSON file (path, name)
        
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

                // init JSONMain with the string parsed from the buffered reader
                strToJSON = sb.toString();
                JSONMain = new JSONObject(strToJSON);
                
                Log.d(TAG, "M/initJSON: Instantiated JSONMain with JSONFile.");
                
                // set image button uri if not set
                if(headImageUri == null) {
                    initHeadImageUri(context);
                }
            } catch(JSONException | IOException e) {
                Log.d(TAG, "M/initJSON: JSONException or IOException thrown...");
                Log.d(TAG, "M/initJSON: " + e.getMessage());
                Log.d(TAG, "M/initJSON: JSONFile exists but is likely to be empty...");
                Log.d(TAG, "M/initJSON: Instantiating new JSONMain...");
                
                JSONMain = new JSONObject();
            }
        } else {
            Log.d(TAG, "M/initJSON: JSONFile does not exist...");
            Log.d(TAG, "M/initJSON: Instantiating new JSONMain...");
            
            JSONMain = new JSONObject();
        }
    }

    /**
     * Initializes an arbitrary image that is display as the ViewImageActivity button
     * in the main activity.
     *
     * @param context current context
     */
    private static void initHeadImageUri(Context context) {
        Cursor cursor = getImageCursor(context); // cursor directed at MediaStore
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // content path
        String id = MediaStore.Images.Media._ID; // static id field

        try {
            if(cursor.moveToFirst()) {
                headImageUri = ContentUris.withAppendedId(
                        uri, cursor.getInt(cursor.getColumnIndexOrThrow(id)));
            }
            cursor.close();
        } catch(Exception e) {
            Log.d(TAG, "M/initHeadImageUri: Null...");
            Log.d(TAG, "M/initHeadImageUri: " + e.getMessage());
        }
    }

    /**
     * Cleans JSON of deleted images.
     * 
     * Refers to the images stored in the MediaStore directory.
     *
     * @param keys list of uri keys
     */
    public static void cleanJSON(List<String> keys) {
        String key; // uri id counter

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
    public static void saveToJSON(Context context, Uri uri, String className, int[] bbox) {
        try{
            // init image name and id using the uri
            String name = getImageName(context, uri);
            String id = uri.getLastPathSegment();

            // init imageJSON and get main JSON object
            ImageObject imageObject = new ImageObject(name, uri.toString(), className, bbox);
            JSONObject imageJSON = imageObject.getImageJSON();

            // append ImageObject to JSONMain as imageJSON
            JSONMain.put(id, imageJSON);

            // save/overwrite local JSON file: JSONMain
            saveJSONAsFile();

            Log.d(TAG, "M/saveToJSON: Image saved with parameters: " + imageObject);
        } catch(JSONException e) {
            Log.d(TAG, "M/saveToJSON: JSONException...");
            Log.d(TAG, "M/saveToJSON: " + e.getMessage());
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
            Log.d(TAG, "M/saveJSONAsFile: JSONFile saved to " + JSONFile.getParent());
        } catch (IOException e) {
            Log.d(TAG, "M/saveJSONAsFile: IOException...");
            Log.d(TAG, "M/saveJSONAsFile: " + e.getMessage());
        }
    }

    /**
     * Unpacks JSONObject as an ImageObject.
     *
     * @param jsonObject the JSONObject to compile
     * @return an ImageObject with the data from the JSONObject
     */
    public static ImageObject JSONToImageObject(JSONObject jsonObject) {
        ImageObject imageObject; // the ImageObject to return
        JSONArray jsonArray; // used to convert JSONArray to an int array
        String name, uri, className; // field variables
        int[] bbox; // int array for the bounding box coordinates

        try {
            // get JSONObject field data
            name = jsonObject.getString("name");
            uri = jsonObject.getString("uri");
            className = jsonObject.getString("class");
            jsonArray = jsonObject.getJSONArray("bbox");

            bbox = new int[jsonArray.length()];

            for(int i = 0; i < bbox.length; i++) {
                bbox[i] = jsonArray.optInt(i);
            }

            imageObject = new ImageObject(name, uri, className, bbox);
            return imageObject;
        } catch(JSONException e) {
            Log.d(TAG, "M/JSONToImageObject: JSONException...");
            Log.d(TAG, "M/JSONToImageObject: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get the display name of an image using its uri.
     *
     * @param context the current context
     * @param uri the image uri
     * @return String image name; default is null
     */
    public static String getImageName(Context context, Uri uri) {
        String imageName = null; // name of image
        String _id = OpenableColumns.DISPLAY_NAME; // static id field

        if(uri.getScheme().equalsIgnoreCase("content")) {
            Cursor cursor = context.getContentResolver().query(
                    uri, 
                    null, 
                    null, 
                    null, 
                    null);
            try{
                if(cursor.moveToFirst()) {
                    imageName = cursor.getString(cursor.getColumnIndexOrThrow(_id));
                }
                cursor.close();
            } catch(Exception e) {
                Log.d(TAG, "M/getImageName: Null or out of bounds...");
                Log.d(TAG, "M/getImageName: " + e.getMessage());
            }
        }
        return imageName;
    }

    /**
     * Gets a cursor to iterate through the images stored in MediaStore (DCIM).
     * 
     * Sorts images by default; by their display names.
     * 
     * @param context current context
     * @return Cursor object directed at the MediaStore directory (DCIM)
     */
    public static Cursor getImageCursor(Context context) {
        // columns to retrieve
        String[] projection = {MediaStore.Images.Media._ID};

        // the uri path to the primary external storage volume
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // how the query is sorted
        String sortOrder = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

        // query all MediaStore images; return cursor
        return context.getContentResolver().query(
                uri, 
                projection, 
                null, 
                null, 
                sortOrder);
    }

    /**
     * Gets a list of keys to access the images in MediaStore.
     * 
     * @param context current context
     * @return String List of MediaStore keys
     */
    public static List<String> getImageKeys(Context context) {
        Cursor cursor = getImageCursor(context); // cursor directed at MediaStore
        Uri id; // uri path counter
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // content path
        String _id = MediaStore.Images.Media._ID; // static id field
        File file; // file to store uri path
        List<String> keys = new ArrayList<>(); // stores the ids of current images in MediaStore
        
        // iterates through all MediaStore images
        while(cursor.moveToNext()) {
            id = ContentUris.withAppendedId(
                    uri, cursor.getInt(cursor.getColumnIndexOrThrow(_id)));

            // stores the uri of the first image.
            // used as the preview image for btnImage in Main
            if(headImageUri == null) { headImageUri = id; }

            file = new File(id.getPath()); // store file at the uri path
            keys.add(file.getName()); // add the ID of the file to the keys list
        }
        
        cursor.close();
        
        return keys;
    }
    
    /**
     * Gets all the current images in JSONMain and returns them in an
     * ArrayList of ImageObjects.
     * 
     * @return ArrayList of ImageObjects
     */
    public static ArrayList<ImageObject> getImageObjectArray() {
        ArrayList<ImageObject> arr = new ArrayList<>();
        
        if(JSONMain != null) {
            JSONObject jsonObject;
            
            String key;
            Iterator<String> iterator = JSONMain.keys();

            while(iterator.hasNext()) {
                key = iterator.next();
                try{
                    if(JSONMain.get(key) instanceof JSONObject) {
                        jsonObject = JSONMain.getJSONObject(key);
                        arr.add(JSONToImageObject(jsonObject));
                    }
                } catch(JSONException e) {
                    Log.d(TAG, "M/getImageObjectArray: JSONException...");
                    Log.d(TAG, "M/getImageObjectArray: " + e.getMessage());
                }
            }
        }
        return arr;
    }

    /**
     * Reinitializes JSONManager components based on resume situation in main activity.
     * 
     * @param context current context
     */
    public static void resume(Context context) {
        if(!JSONFile.exists() && JSONMain != null) {
            Log.d(TAG, "M/resume: No JSONFile...");
            Log.d(TAG, "M/resume: Writing JSONMain to file...");
            saveJSONAsFile();
        } else if(!JSONFile.exists() && JSONMain == null) {
            Log.d(TAG, "M/resume: No JSONFile and JSONMain is null...");
            Log.d(TAG, "M/resume: Reinitializing...");
            initJSON(context);
        }else if(JSONFile.exists() && JSONMain == null) {
            Log.d(TAG, "M/resume: JSONMain is null...");
            Log.d(TAG, "M/resume: Reinitializing...");
            initJSON(context);
        }
    }

    /**
     * Returns the uri of the first image indexed by the JSON verification process.
     *
     * @return uri of the first image indexed
     */
    public static Uri getHeadImageUri() { return headImageUri; }
}