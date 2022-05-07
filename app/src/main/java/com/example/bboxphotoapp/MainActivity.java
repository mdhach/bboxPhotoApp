package com.example.bboxphotoapp;

import static android.view.MotionEvent.INVALID_POINTER_ID;

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
import androidx.core.view.MotionEventCompat;

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

    private int xCenter;
    private int yCenter;

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
        btnFloating.setOnClickListener(view -> cc.takePhoto(bboxView.getTopLeft(), bboxView.getBottomRight()));

        imgViewTopLeft.setOnTouchListener(getOtl(0));

        imgViewBottomRight.setOnTouchListener(getOtl(1));
    }

    /**
     * Creates an OnTouchListener object.
     *
     * Allows the user to readjust the bounding box in the main view by moving the image views.
     *
     * @param img 0: top left; 1: bottom right
     * @return an OnTouchListener object
     */
    public View.OnTouchListener getOtl(int img) {

        View.OnTouchListener otl = new View.OnTouchListener() {

            // pointer used for multi-touch events
            private int mActivePointerId = INVALID_POINTER_ID;

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
                        mActivePointerId = MotionEventCompat.getPointerId(motionEvent, pointerIndex);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        final int pointerIndex = MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
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
                        mActivePointerId = INVALID_POINTER_ID;
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        // invalidate pointer after user cancels action
                        mActivePointerId = INVALID_POINTER_ID;
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                        final int pointerId = MotionEventCompat.getPointerId(motionEvent, pointerIndex);

                        if(pointerId == mActivePointerId) {
                            // choose new pointer after up event
                            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            mActivePointerId = MotionEventCompat.getPointerId(motionEvent, newPointerIndex);
                        }
                        break;
                    }
                }
                return true;
            }
        };

        return otl; // return listener object
    }
}
