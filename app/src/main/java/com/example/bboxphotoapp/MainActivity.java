package com.example.bboxphotoapp;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private CameraController cc; // camera object; used to take photos
    
    // spinner objects
    private Spinner classSpinner; // spinner view to display and choose classification name
    private SpinnerAdapter spAdapter; // custom spinner adapter
    
    // alert dialog objects
    private AlertDialog.Builder dialogBuilder; // used to build an alert dialog
    private AlertDialog dialog; // the dialog object
    private EditText editClassText; // user-defined class to add to spinner
    private Button btnAddButton; // adds class to spinner
    private Button btnCancelButton; // cancels dialog action
    
    // views
    private PreviewView previewView; // surface view for camera preview
    private BboxView bboxView; // the bounding box view
    private ImageView imgViewTopLeft; // used to readjust the top left of the bounding box
    private ImageView imgViewBottomRight; // used to readjust the bottom right of the bounding box
    
    // buttons
    private FloatingActionButton btnTakePhoto; // button to take photos
    private ImageButton btnViewImage; // button to edit images
    private ImageButton btnAddClass; // button to add classes
    
    // ImageView variables
    private int xCenter; // the center x-coordinate of the image for the ImageView objects
    private int yCenter; // the center y-coordinate of the image for the ImageView objects
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init views
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
        btnViewImage = findViewById(R.id.btnViewImage);
        btnAddClass = findViewById(R.id.btnAddClass);

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

        // try to set preview image for btnViewImage
        try{
            this.btnViewImage.setImageURI(JSONManager.getHeadImageUri());
        } catch(Exception e) {
            e.printStackTrace();
        }

        // initialize camera controller object
        cc = new CameraController(this);
        
        // initialize spinner adapter with an array of default
        spAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, new ArrayList<String>() {
            {
                add("Default");
            }
        });
        
        // set class spinner adapter to custom adapter
        classSpinner.setAdapter(spAdapter);

        // init add class button
        btnAddClass.setOnClickListener(view -> {
            createDialog();
        });

        // init listeners on image views; allows user to readjust bbox size
        imgViewTopLeft.setOnTouchListener(getOtl(0));
        imgViewBottomRight.setOnTouchListener(getOtl(1));
        
        btnViewImage.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ViewImageActivity.class));
        });

        // set listener on floating action button
        btnTakePhoto.setOnClickListener(view -> cc.takePhoto(
                bboxView.getTopLeft(),
                bboxView.getBottomRight(),
                spAdapter.getClassName())); // gets class name from spinner adapter
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
     * Creates a dialog box that allows the user to add a new class to the
     * class spinner object.
     * 
     */
    public void createDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.class_prompt, null);
        
        editClassText = dialogView.findViewById(R.id.addClassText);
        btnAddButton = dialogView.findViewById(R.id.addButton);
        btnCancelButton = dialogView.findViewById(R.id.cancelButton);
        
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
        dialog.show();
        
        // adds a user-defined string to the list contained by the Spinner Adapter object
        btnAddButton.setOnClickListener(view -> {
            // get string value
            String newClass = String.valueOf(editClassText.getText());
            
            // add string to Spinner Adapter
            spAdapter.add(newClass);
            
            // refresh adapter list
            spAdapter.notifyDataSetChanged();
            
            // dismiss dialog box
            dialog.dismiss();
        });
        
        // allows user to cancel dialog action
        btnCancelButton.setOnClickListener(view -> {
            // dismiss dialog box
            dialog.dismiss();
        });
    }
}