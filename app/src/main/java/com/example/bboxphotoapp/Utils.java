package com.example.bboxphotoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public final class Utils {

    // constants
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final String[] STORAGE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int CAMERA_REQUEST_CODE = 10;
    private static final int STORAGE_REQUEST_CODE = 20;

    /**
     * Returns the activity of a given context.
     *
     * @param context current context
     * @return activity of parameter context
     */
    public static Activity getActivity(Context context) {
        if(context == null) {
            return null;
        } else if(context instanceof ContextWrapper) {
            if(context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }

    /**
     * Checks if a context was given camera permissions.
     *
     * @param context current context
     * @return boolean
     */
    public static boolean hasCameraPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if a context was given storage permissions.
     *
     * @param context current context
     * @return boolean
     */
    public static boolean hasStoragePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests camera permissions for given context
     *
     * @param context current context
     */
    public static void requestCameraPermissions(Context context) {
        Activity activity = getActivity(context);
        ActivityCompat.requestPermissions(activity, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
    }

    /**
     * Requests storage permissions for given context
     *
     * @param context current context
     */
    public static void requestStoragePermissions(Context context) {
        Activity activity = getActivity(context);
        ActivityCompat.requestPermissions(activity, STORAGE_PERMISSION, STORAGE_REQUEST_CODE);
    }

}
