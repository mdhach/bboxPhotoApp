package com.example.bboxphotoapp;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Creates a listener for the image adjusters on the Main Activity.
 *
 * Allows the user to readjust the bounding box by moving the image views corresponding to the 
 * boxes at the upper left or bottom right corner.
 * 
 */
public class AdjustOnTouch implements View.OnTouchListener {

    private static final String TAG = "AdjustOnTouch";

    // the current active pointer; used for multi-touch events
    private int activePointerId = INVALID_POINTER_ID;
    private int xCenter;
    private int yCenter;
    private int loc;
    private ImageView topLeft;
    private ImageView bottomRight;
    private BboxView bboxView;

    /**
     * Constructor for the adjuster listeners.
     * 
     * @param topLeft top left adjuster image view
     * @param bottomRight bottom right adjuster image view
     * @param bboxView the bounding box to manipulate
     * @param loc listen to top left or bottom right movement; 0 for top left, 1 for bottom right
     */
    public AdjustOnTouch(ImageView topLeft, ImageView bottomRight, BboxView bboxView, int loc) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.bboxView = bboxView;
        this.loc = loc;
        
        // used to offset the image source for the adjuster views
        this.xCenter = topLeft.getWidth() / 2;
        this.yCenter = topLeft.getHeight() / 2;
    }
    
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // gets action event relative to pointer id
        int action = motionEvent.getActionMasked();

        
        
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            switch(action) {
                case MotionEvent.ACTION_DOWN: {
                    // the current pointer index.
                    // used to differentiate between multiple touch events
                    final int pointerIndex = motionEvent.getAction();

                    // raw position data; the current position of the users pointer index in
                    // relation to the canvas
                    final int x = (int) motionEvent.getRawX(pointerIndex);

                    final int y = (int) motionEvent.getRawY(pointerIndex);

                    // save active pointer id
                    activePointerId = motionEvent.getPointerId(pointerIndex);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    final int pointerIndex = motionEvent.findPointerIndex(activePointerId);
                    final int x = (int) motionEvent.getRawX(pointerIndex);
                    final int y = (int) motionEvent.getRawY(pointerIndex);

                    // boundaries when repositioning image and resizing bounding box
                    if (loc == 0) {
                        // top left adjuster cannot pass bottom right image
                        if (x < bottomRight.getX() + xCenter && y < bottomRight.getY() + yCenter
                                && x < bboxView.getMaxWidth() && y < bboxView.getMaxHeight()) {
                            bboxView.setTopLeft(x, y);
                            topLeft.setX(x - xCenter);
                            topLeft.setY(y - yCenter);
                        }
                    } else if (loc == 1) {
                        // bottom right adjuster cannot pass top left image
                        if (x > topLeft.getX() + xCenter && y > topLeft.getY() + yCenter
                                && x < bboxView.getMaxWidth() && y < bboxView.getMaxHeight()) {
                            bboxView.setBottomRight(x, y);
                            bottomRight.setX(x - xCenter);
                            bottomRight.setY(y - yCenter);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                    // invalidate pointer after user lets go of touch event
                    view.performClick();
                    activePointerId = INVALID_POINTER_ID;
                    break;
                case MotionEvent.ACTION_CANCEL: {
                    // invalidate pointer after user lets go of touch event
                    activePointerId = INVALID_POINTER_ID;
                    break;
                }// invalidate pointer after user cancels action
                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = motionEvent.getActionIndex();
                    final int pointerId = motionEvent.getPointerId(pointerIndex);

                    if (pointerId == activePointerId) {
                        // choose new pointer after up event
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        activePointerId = motionEvent.getPointerId(newPointerIndex);
                    }
                    break;
                }
            }
        }
        return true;
    }
}
