package com.example.bboxphotoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

        imgViewTopLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                int xCenter = imgViewTopLeft.getWidth() / 2;
                int yCenter = imgViewTopLeft.getHeight() / 2;
                int x = (int) motionEvent.getRawX();
                int y = (int) motionEvent.getRawY();

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        bboxView.setTopLeft(x, y);
                        imgViewTopLeft.setX(x-xCenter);
                        imgViewTopLeft.setY(y-yCenter);
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    default:
                        return false;
                }
            }
        });

        imgViewBottomRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                int xCenter = imgViewBottomRight.getWidth() / 2;
                int yCenter = imgViewBottomRight.getHeight() / 2;
                int x = (int) motionEvent.getRawX();
                int y = (int) motionEvent.getRawY();

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        bboxView.setBottomRight(x, y);
                        imgViewBottomRight.setX(x-xCenter);
                        imgViewBottomRight.setY(y-yCenter);
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
