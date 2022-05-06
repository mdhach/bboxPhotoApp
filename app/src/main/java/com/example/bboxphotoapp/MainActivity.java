package com.example.bboxphotoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FloatingActionButton btnFloating;
    private ImageButton btnImage;
    private ImageView imgViewTopLeft;
    private ImageView imgViewBottomRight;

    private PreviewView previewView;
    private BboxView bboxView;
    private CameraController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // views
        previewView = findViewById(R.id.previewView);
        bboxView = findViewById(R.id.bboxView);

        // adjustment indicators
        imgViewTopLeft = findViewById(R.id.imgViewTopLeft);
        imgViewBottomRight = findViewById(R.id.imgViewBottomRight);

        // init adjustment indicator locations after bboxView initializes dimensions
        bboxView.post(() -> {
            // image dims
            int imgWidth = imgViewTopLeft.getWidth() / 2;
            int imgHeight = imgViewTopLeft.getHeight() / 2;

            // bbox dims
            int[] topLeft = bboxView.getTopLeft();
            int[] bottomRight = bboxView.getBottomRight();

            // set top left coordinates
            imgViewTopLeft.setX(topLeft[0] - imgWidth);
            imgViewTopLeft.setY(topLeft[1] - imgHeight);

            // set bottom right coordinates
            imgViewBottomRight.setX(bottomRight[0] - imgWidth);
            imgViewBottomRight.setY(bottomRight[1] - imgHeight);
        });

        // init buttons
        btnFloating = findViewById(R.id.btnFloating);
        btnImage = findViewById(R.id.btnImage);

        // get storage permissions
        if(!Utils.hasStoragePermissions(this)) {
            Utils.requestStoragePermissions(this);
        }

        // get camera permissions
        if(!Utils.hasCameraPermissions(this)) {
            Utils.requestCameraPermissions(this);
        }

        // initialize JSON object if it exists in storage
        JSONManager.initJSON(this);

        // try to set preview image for edit button
        try{
            this.btnImage.setImageURI(JSONManager.getHeadImageUri());
        } catch(Exception e) {
            e.printStackTrace();
        }

        // initialize camera controller object
        cc = new CameraController(this);

        // set listener on floating action button
        btnFloating.setOnClickListener(view -> cc.takePhoto());
    }
}
