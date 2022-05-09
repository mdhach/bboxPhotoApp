package com.example.bboxphotoapp;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.view.MotionEventCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private ClassNameManager cnm; // manages the classification name strings
    
    private PreviewView previewView; // surface view for camera preview
    private BboxView bboxView; // the bounding box view
    private CameraController cc; // camera object; used to take photos

    private FloatingActionButton btnTakePhoto; // button to take photos
    private ImageButton btnEditImage; // button to edit images
    private ImageView imgViewTopLeft; // used to readjust the top left of the bounding box
    private ImageView imgViewBottomRight; // used to readjust the bottom right of the bounding box
    private Spinner classSpinner; // spinner view to display and choose classification name

    private String className; // classification name

    private int xCenter; // the center x-coordinate of the image for the ImageView objects
    private int yCenter; // the center y-coordinate of the image for the ImageView objects
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // object used to manage the strings displayed in the spinner view
        cnm = new ClassNameManager();

        // initialize camera surface preview and bounding box view
        previewView = findViewById(R.id.previewView);
        bboxView = findViewById(R.id.bboxView);

        // adjustment indicators
        imgViewTopLeft = findViewById(R.id.imgViewTopLeft);
        imgViewBottomRight = findViewById(R.id.imgViewBottomRight);

        // spinner for choosing image classification
        classSpinner = findViewById(R.id.classSpinner);

        // init adjustment indicator locations after bboxView initializes dimensions
        bboxView.post(() -> {
            // image dims
            xCenter = imgViewTopLeft.getWidth() / 2;
            yCenter = imgViewTopLeft.getHeight() / 2;

            // bbox dims
            int[] topLeft = bboxView.getTopLeft();
            int[] bottomRight = bboxView.getBottomRight();

            // set top left coordinates
            imgViewTopLeft.setX(topLeft[0] - xCenter);
            imgViewTopLeft.setY(topLeft[1] - yCenter);

            // set bottom right coordinates
            imgViewBottomRight.setX(bottomRight[0] - xCenter);
            imgViewBottomRight.setY(bottomRight[1] - yCenter);
        });

        // init buttons
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnEditImage = findViewById(R.id.btnEditImage);

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
            this.btnEditImage.setImageURI(JSONManager.getHeadImageUri());
        } catch(Exception e) {
            e.printStackTrace();
        }

        // initialize camera controller object
        cc = new CameraController(this);

        // set listener on floating action button
        btnTakePhoto.setOnClickListener(view -> cc.takePhoto(
                bboxView.getTopLeft(),
                bboxView.getBottomRight(),
                className));

        // init array adapter from ClassNameManager object
        classSpinner.setAdapter(cnm.getArrayAdapter(this));

        // init item select listener
        classSpinner.setOnItemSelectedListener(getOisl());

        // init listeners on image views; allows user to readjust bbox size
        imgViewTopLeft.setOnTouchListener(getOtl(0));
        imgViewBottomRight.setOnTouchListener(getOtl(1));
    }

    /**
     * Creates a listener object using the OnTouchListener interface.
     *
     * Allows the user to readjust the bounding box in the main view by moving the image view
     * corresponding to the boxes upper left or bottom right corner.
     *
     * @param img 0: top left; 1: bottom right
     * @return View.OnTouchListener object
     */
    public View.OnTouchListener getOtl(int img) {

        View.OnTouchListener otl = new View.OnTouchListener() {

            // the current active pointer; used for multi-touch events
            private int activePointerId = INVALID_POINTER_ID;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // gets action event relative to pointer id
                int action = MotionEventCompat.getActionMasked(motionEvent);

                switch(action) {
                    case MotionEvent.ACTION_DOWN: {
                        // the current pointer index.
                        // used to differentiate between multiple touch events
                        final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);

                        // raw position data; the current position of the users pointer index in
                        // relation to the canvas
                        final int x = (int) motionEvent.getRawX(pointerIndex);
                        final int y = (int) motionEvent.getRawY(pointerIndex);

                        // save active pointer id
                        activePointerId = MotionEventCompat.getPointerId(motionEvent, pointerIndex);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        final int pointerIndex = MotionEventCompat.findPointerIndex(motionEvent, activePointerId);
                        final int x = (int) motionEvent.getRawX(pointerIndex);
                        final int y = (int) motionEvent.getRawY(pointerIndex);

                        // boundaries when repositioning image and resizing bounding box
                        if(img == 0) {
                            // top left image cannot pass bottom right image
                            if(x < imgViewBottomRight.getX()+xCenter && y < imgViewBottomRight.getY()+yCenter) {
                                bboxView.setTopLeft(x, y);
                                imgViewTopLeft.setX(x-xCenter);
                                imgViewTopLeft.setY(y-yCenter);
                            }
                        } else if(img == 1) {
                            // bottom right image cannot pass top left image
                            if(x > imgViewTopLeft.getX()+xCenter && y > imgViewTopLeft.getY()+yCenter) {
                                bboxView.setBottomRight(x, y);
                                imgViewBottomRight.setX(x-xCenter);
                                imgViewBottomRight.setY(y-yCenter);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        // invalidate pointer after user lets go of touch event
                        activePointerId = INVALID_POINTER_ID;
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        // invalidate pointer after user cancels action
                        activePointerId = INVALID_POINTER_ID;
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                        final int pointerId = MotionEventCompat.getPointerId(motionEvent, pointerIndex);

                        if(pointerId == activePointerId) {
                            // choose new pointer after up event
                            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            activePointerId = MotionEventCompat.getPointerId(motionEvent, newPointerIndex);
                        }
                        break;
                    }
                }
                return true;
            }
        };
        return otl; // return listener object
    }

    /**
     * Creates a listener using the OnItemSelectedListener interface.
     *
     * Allows the user to choose a classification name for their image.
     *
     * @return AdapterView.OnItemSelectedListener object
     */
    public AdapterView.OnItemSelectedListener getOisl() {
        AdapterView.OnItemSelectedListener oisl = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String name = String.valueOf(classSpinner.getItemAtPosition(i));
                className = name;
                
                // toast verification to user
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                
                Log.d(TAG, className);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        };
        return oisl;
    }
}