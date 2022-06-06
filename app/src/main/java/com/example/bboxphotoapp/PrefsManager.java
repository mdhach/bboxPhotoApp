package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public final class PrefsManager {

    private static final String TAG = "PrefsManager";
    
    private static SharedPreferences sharedPref;
    
    public static String saveLocKey;
    public static String saveNameKey;
    
    public static void init(@NonNull Activity activity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        saveLocKey = activity.getString(R.string.save_location_key);
        saveNameKey = activity.getString(R.string.save_name_key);
        
        String dSaveLocVal = activity.getFilesDir().toString();
        String dSaveNameVal = activity.getString(R.string.default_save_name_val);
        
        initDefaultPairs(saveLocKey, dSaveLocVal);
        initDefaultPairs(saveNameKey, dSaveNameVal);
    }
    
    private static void initDefaultPairs(String key, String val) {
        SharedPreferences.Editor editor = sharedPref.edit();
        
        if(sharedPref.getString(key, null) == null) {
            editor.putString(key, val);
            editor.apply();
        }
    }
    
    public static void setValue(String key, String val) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, val);
        editor.apply();
    }
    
    public static String getValue(String key) {
        return sharedPref.getString(key, null);
    }
    
}
