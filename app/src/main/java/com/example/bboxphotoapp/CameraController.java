package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CameraController {

    private Context context;
    private PreviewView previewView;
    private Activity activity;

    // camera variables
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    public CameraController(Context context) {
        this.context = context;
        this.activity = Utils.getActivity(this.context);
        this.previewView = this.activity.findViewById(R.id.previewView);

        // access camera and start-up camera
        startCamera();
    }

    private void startCamera() {
        // create camera singleton
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.context);

        // add camera listener
        cameraProviderFuture.addListener(() -> {
            try {
                // check for camera instance and bind it to a preview
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this.context));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
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
                (LifecycleOwner) this.activity,
                cameraSelector,
                imageCapture,
                preview);
    }

    public void takePhoto() {
        // take picture; toast messages for confirmation
        imageCapture.takePicture(getOutputOptions(),
                ContextCompat.getMainExecutor(this.activity),
                new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                Toast.makeText(context,
                        "Image saved",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(ImageCaptureException error) {
                error.printStackTrace();
                Toast.makeText(context,
                        "Error: Picture could not be saved/taken.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ImageCapture.OutputFileOptions getOutputOptions() {
        // output image name; current date and time
        String outputName = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());

        // MediaStore values
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, outputName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        // set image capture output options
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        this.activity.getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues).build();

        return outputFileOptions;
    }
}
