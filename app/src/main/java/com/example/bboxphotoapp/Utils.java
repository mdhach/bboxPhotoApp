package com.example.bboxphotoapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import java.io.File;

/**
 * Static methods that may be used globally on the application.
 * 
 */
public final class Utils {

    private static final String TAG = "Utils";

    // permissions
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final String[] STORAGE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // request codes
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

    /**
     * Creates a confirmation dialog box that executes a method argument.
     *
     * @param context current context
     * @param title name of the dialog pop-up box
     * @param message display text
     * @param func method to execute on confirmation
     */
    public static void addConfirmDialog(Context context, String title, String message, Runnable func) {
        ConfirmDialogFrag dialogFrag = new ConfirmDialogFrag(title, message);
        FragmentActivity activity = (FragmentActivity) getActivity(context);
        dialogFrag.show(activity.getSupportFragmentManager(), "ConfirmDialogFrag");

        String requestKey = context.getString(R.string.rq_confirmation);
        String bundleKey = context.getString(R.string.bn_confirmation);

        activity.getSupportFragmentManager()
                .setFragmentResultListener(requestKey, activity, (key, bundle) -> {
                    if(key.equals(requestKey)) {
                        if(bundle.getBoolean(bundleKey)) {
                            func.run();
                            Log.d(TAG, "M/addConfirmDialog: request success");
                        } else {
                            Log.d(TAG, "M/addConfirmDialog: incorrect bundleKey");
                        }
                    } else {
                        Log.d(TAG, "M/addConfirmDialog: incorrect requestKey");
                    }
                    activity.getSupportFragmentManager().clearFragmentResultListener(requestKey);
                });
    }

    /**
     * Creates a confirmation dialog box that executes a method argument.
     * 
     * Accepts a warning argument that will append a warning text to your dialog message.
     *
     * @param context current context
     * @param title name of the dialog pop-up box
     * @param message display text
     * @param appended text to append to message
     * @param type 0 warning; 1 info; 2 hint
     * @param func method to execute on confirmation
     */
    public static void addConfirmDialog(Context context, String title, String message, String appended, int type, Runnable func) {
        String text = message;
        
        // change appended text based on appended type
        switch(type) {
            case 0:
                text += "\n\nWarning: " + appended;
                break;
            case 1:
                text += "\n\nInfo: " + appended;
                break;
            case 2:
                text += "\n\nHint: " + appended;
                break;
            default:
                text += "\n\nNULL: " + appended;
                break;
        }
        
        ConfirmDialogFrag dialogFrag = new ConfirmDialogFrag(title, text);
        FragmentActivity activity = (FragmentActivity) getActivity(context);
        dialogFrag.show(activity.getSupportFragmentManager(), "ConfirmDialogFrag");

        String requestKey = context.getString(R.string.rq_confirmation);
        String bundleKey = context.getString(R.string.bn_confirmation);

        activity.getSupportFragmentManager()
                .setFragmentResultListener(requestKey, activity, (key, bundle) -> {
                    if(key.equals(requestKey)) {
                        if(bundle.getBoolean(bundleKey)) {
                            func.run();
                            Log.d(TAG, "M/addConfirmDialog: request success");
                        } else {
                            Log.d(TAG, "M/addConfirmDialog: incorrect bundleKey");
                        }
                    } else {
                        Log.d(TAG, "M/addConfirmDialog: incorrect requestKey");
                    }
                    activity.getSupportFragmentManager().clearFragmentResultListener(requestKey);
                });
    }

    /**
     * Static utility method taken from:
     * 
     *      https://gist.github.com/asifmujteba/d89ba9074bc941de1eaa#file-asfurihelper
     *      
     * Used to get the real path from a document uri.
     * 
     * @param context current context
     * @param uri document uri 
     * @return String path from document uri
     */
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    try {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } catch(ArrayIndexOutOfBoundsException e) {
                        Log.d(TAG, "M/getPath: ArrayIndexOutOfBoundsException...");
                        Log.d(TAG, "M/getPath: return excluding split[1]...");
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param path the path to splice
     * @return the last component of a path
     */
    public static String getRelativePath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}