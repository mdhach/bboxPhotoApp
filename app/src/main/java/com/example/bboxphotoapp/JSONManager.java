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
            
            Log.d(TAG, "M/initJSON: getParent() " + JSONFile.getParent());
            Log.d(TAG, "M/initJSON: getName() " + JSONFile.getName());
            Log.d(TAG, "M/initJSON: String.format() " + String.format("%s.zip", JSONFile.getName()));
            Log.d(TAG, "M/initJSON: getPath().length() " + JSONFile.getPath().length());
            
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

                //verifyJSON(context); // verifies JSONMain contents
            } catch(JSONException | IOException e) {
                Log.d(TAG, "M/initJSON: File not found...");
                e.printStackTrace();
            }
        } else { JSONMain = new JSONObject(); }
    }

    /**
     * If the path for the JSON file has changed, create new file at the new path and 
     * overwrite the currently referenced JSON file with the new one. 
     * 
     * @param context current activity context
     */
    public static void reinitializeJSON(Context context) {
        String JSONName = context.getString(R.string.default_json_name);
        String sourcePath = PrefsManager.getValue(PrefsManager.saveLocKey);
        
        final int BUFFER = 1024;
        
        File file = new File(sourcePath, JSONName);
        try (InputStream in = new FileInputStream(JSONFile)){
            try (OutputStream out = new FileOutputStream(file)) {
                byte[] data = new byte[BUFFER];
                int len;
                while ((len = in.read(data)) > 0) {
                    out.write(data, 0, len);
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            Log.d(TAG, "M/reinitializeJSON: IOException");
            e.printStackTrace();
        }
    }

    /**
     * Updates JSON file by removing image entries that were deleted by the user.
     * 
     * Called in initJSON() method.
     * 
     * (Temporarily disabled in case user deletes images after compressing them to a zip file,
     * but also wants to keep the data stored in the JSON file).
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

            Log.d(TAG, "Image saved with parameters: " + imageObject);
        } catch(JSONException e) {
            Log.d(TAG, "M/saveToJSON: JSONException...");
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
            Log.d(TAG, "M/saveJSONAsFile: IOException...");
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
                    cursor.close();
                }
            } catch(Exception e) {
                Log.d(TAG, "M/getImageName: Null or out of bounds...");
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
                    e.printStackTrace();
                }
            }
        }
        
        return arr;
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
            e.printStackTrace();
        }

        return null;
    }
}