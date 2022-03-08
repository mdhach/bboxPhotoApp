package com.example.bboxphotoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

public class CameraController {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Context context;
    private PreviewView previewView;
    private Activity activity;
    private AppTools appTools = new AppTools();

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    public CameraController(Context context) {
        this.context = context;
        this.activity = appTools.getActivity(this.context);
        this.previewView = this.activity.findViewById(R.id.previewView);

        if(!hasPermission()) { requestPermission(); }
    }

    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.context);

        cameraProviderFuture.addListener(() -> {
            try {
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

        // select camera
        CameraSelector cameraSelector = new CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // provides a surface for the camera preview
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // starts data capture
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this.activity, cameraSelector, preview);
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this.context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this.activity, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
    }

}
