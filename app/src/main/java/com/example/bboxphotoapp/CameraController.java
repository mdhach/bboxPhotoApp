package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Class used to initialize and manage the camera.
 * 
 */
public class CameraController {

    private static final String TAG = "CameraController";
    
    private PreviewView previewView;

    // camera variables
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    /**
     * Creates a camera instance and allows the user to take photos.
     *
     * @param context the current context
     */
    public CameraController(Context context) {
        this.previewView = Utils.getActivity(context).findViewById(R.id.previewView);
        
        // get storage permissions
        if(!Utils.hasStoragePermissions(context)) {
            Utils.requestStoragePermissions(context);
        }

        // get camera permissions
        if(!Utils.hasCameraPermissions(context)) {
            Utils.requestCameraPermissions(context);
        }

        // access camera and start-up camera
        startCamera(context);
    }

    /**
     * Initializes the camera instance.
     *
     */
    private void startCamera(Context context) {
        // create camera instance
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        // add camera listener
        cameraProviderFuture.addListener(() -> {
            try {
                // get camera instance and bind it to a preview
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, Utils.getActivity(context));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /**
     * Binds a preview to the camera object.
     *
     * @param cameraProvider camera instance within the current context
     */
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, Activity activity) {
        // builds an immutable camera preview
        Preview preview = new Preview.Builder().build();

        // select camera lens
        CameraSelector cameraSelector = new CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // provides a surface for the camera preview
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // instantiate image capture object
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(this.previewView.getDisplay().getRotation())
                .build();

        // bind image capture object to current camera instance.
        // starts data capture; opens camera preview for end-user
        cameraProvider.bindToLifecycle(
                (LifecycleOwner) activity,
                cameraSelector,
                imageCapture,
                preview);
    }

    /**
     * Captures the image within the camera surface preview. Passes the arguments as a bounding box
     * to be saved within the main JSON file.
     *
     * @param bbox the image bbox coordinates
     * @param className the image classification name
     */
    public void takePhoto(Context context, int[] bbox,  String className) {
        // take picture; toast messages for confirmation
        imageCapture.takePicture(getOutputOptions(Utils.getActivity(context)),
                ContextCompat.getMainExecutor(Utils.getActivity(context)),
                new ImageCapture.OnImageSavedCallback() {

            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // save output to json
                JSONManager.saveToJSON(context,
                        outputFileResults.getSavedUri(),
                        className,
                        bbox);

                Log.d(TAG, "M/takePhoto: uri " + outputFileResults.getSavedUri());
                
                // toast notification on image capture success
                Toast.makeText(context,
                        "Image saved",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();

                // toast notification on image capture failure
                Toast.makeText(context,
                        "Error: Picture could not be saved/taken.",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * Custom metadata for the output of an image.
     *
     * @return ImageCapture.OutputFileOptions object with custom metadata
     */
    private @NonNull ImageCapture.OutputFileOptions getOutputOptions(Activity activity) {
        // output image name; current date and time
        String outputName = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss",
                Locale.getDefault()).format(new Date());

        // MediaStore values
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, outputName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        // return image capture output options
        return new ImageCapture.OutputFileOptions.Builder(
                activity.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues).build();
    }
}