package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.annotation.NonNull;

public final class PrefsManager {

    private static final String TAG = "PrefsManager";
    
    private static SharedPreferences sharedPref;
    
    public static String saveLocKey;
    public static String saveNameKey;
    public static String imgDirKey;
    
    public static void init(@NonNull Activity activity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        saveLocKey = activity.getString(R.string.save_location_key);
        saveNameKey = activity.getString(R.string.save_name_key);
        imgDirKey = activity.getString(R.string.img_dir_key);
        
        // default values for zip file save location and save name
        String dSaveLocVal = activity.getFilesDir().toString();
        String dSaveNameVal = activity.getString(R.string.default_save_name_val);
        String dImgDirVal = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        
        initDefaultPairs(saveLocKey, dSaveLocVal);
        initDefaultPairs(saveNameKey, dSaveNameVal);
        initDefaultPairs(imgDirKey, dImgDirVal);
    }

    /**
     * Checks if key/val pair exists; sets new pair if null.
     * 
     * @param key SharedPreference key
     * @param val SharedPreference value
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
     * Change or add a key/value pair to the shared preferences file.
     * 
     * @param key SharedPreference key
     * @param val SharedPreference value
     */
    public static void setValue(String key, String val) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, val);
        editor.apply();
    }

    /**
     * Given a key, returns the corresponding preferences value.
     * 
     * @param key SharedPreference key
     * @return SharedPreference value
     */
    public static String getValue(String key) {
        return sharedPref.getString(key, null);
    }
    
}
