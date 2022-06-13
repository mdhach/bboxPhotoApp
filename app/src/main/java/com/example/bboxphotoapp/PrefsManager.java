package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Static class used to access the SharedPreference file.
 * 
 */
public final class PrefsManager {

    private static final String TAG = "PrefsManager";
    
    private static SharedPreferences sharedPref;
    
    public static String saveLocKey;
    public static String saveNameKey;
    public static String imgDirKey;
    
    private static String dSaveLocVal;
    private static String dSaveNameVal;
    private static String dImgDirVal;
    
    public static void init(@NonNull Activity activity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        
        // keys stored in string resource
        saveLocKey = activity.getString(R.string.save_location_key);
        saveNameKey = activity.getString(R.string.save_name_key);
        imgDirKey = activity.getString(R.string.img_dir_key);
        
        // default values for initialization
        dSaveLocVal = activity.getFilesDir().toString();
        dSaveNameVal = activity.getString(R.string.default_save_name_val);
        dImgDirVal = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        
        // initialize default pairs
        initDefaultPairs(saveLocKey, dSaveLocVal);
        initDefaultPairs(saveNameKey, dSaveNameVal);
        initDefaultPairs(imgDirKey, dImgDirVal);
    }

    /**
     * Checks if key/val pair exists; creates new pair as key/val otherwise
     * 
     * @param key SharedPreferences key
     * @param val SharedPreferences value
     */
    private static void initDefaultPairs(String key, String val) {
        SharedPreferences.Editor editor = sharedPref.edit();
        
        // if key pair is returns null, add args as new key pair
        if(sharedPref.getString(key, null) == null) {
            editor.putString(key, val);
            editor.apply();
        }
    }

    /**
     * Check if static reference to SharedPreferences is valid.
     * 
     * @return true if reference exists; false otherwise
     */
    public static boolean hasReference() {
        return sharedPref != null;
    }

    /**
     * Resets all values to default settings.
     * 
     */
    public static void reset() {
        setValue(saveLocKey, dSaveLocVal);
        setValue(saveNameKey, dSaveNameVal);
        setValue(imgDirKey, dImgDirVal);
    }

    /**
     * Change or add a key/value pair to the SharedPreferences file.
     * 
     * @param key SharedPreferences key
     * @param val SharedPreferences value
     */
    public static void setValue(String key, String val) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, val);
        editor.apply();
        Log.d(TAG, "M/setValue: value set to {" + val + "} with key {" + key + "}");
    }

    /**
     * Given a key, returns the corresponding SharedPreferences value.
     * 
     * @param key SharedPreferences key
     * @return SharedPreferences value
     */
    public static String getValue(String key) {
        return sharedPref.getString(key, null);
    }
    
    public static String getDefaultSaveLocation() { return dSaveLocVal; }
    
    public static String getDefaultSaveName() { return dSaveNameVal; }
    
    public static String getDefaultImageDir() { return dImgDirVal; }
    
}
