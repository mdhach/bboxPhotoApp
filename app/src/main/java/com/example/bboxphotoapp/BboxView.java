package com.example.bboxphotoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class BboxView extends AppCompatImageView {

    private static final String TAG = "BboxView";

    // objects for drawing and coloring the bounding box
    private Paint paint;
    private Rect rect;

    // thickness of the bounding box
    private int strokeWidth = 3;

    // bbox dimensions
    private int[] topLeft = new int[2];
    private int[] bottomRight = new int[2];

    public BboxView(@NonNull Context context) {
        super(context);

        init(null);
    }

    public BboxView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public BboxView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new Rect();

        // init paint settings
        paint.setStyle(Paint.Style.STROKE); // line type
        paint.setColor(Color.BLACK); // line color
        paint.setStrokeWidth(strokeWidth); // line thickness
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // rect object used to represent our bounding box.
        rect.left = this.topLeft[0]; // left
        rect.top = this.topLeft[1]; // top
        rect.right = this.bottomRight[0]; // right
        rect.bottom = this.bottomRight[1]; // bottom

        // draw rectangle
        canvas.drawRect(rect, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // init bounding box dimensions; default dims based on canvas
        setTopLeft(w/4, h/4);
        setBottomRight((int)(w * 0.75), (int)(h * 0.75));
    }

    /**
     * Sets the top left coordinate of the bounding box.
     *
     * @param x the x coordinate of the top left point
     * @param y the y coordinate of the top left point
     */
    public void setTopLeft(int x, int y) {
        this.topLeft[0] = x;
        this.topLeft[1] = y;
        postInvalidate();
    }

    /**
     * Sets the bottom right coordinate of the bounding box.
     *
     * @param x the x coordinate of the bottom right point
     * @param y the y coordinate of the bottom right point
     */
    public void setBottomRight(int x, int y) {
        this.bottomRight[0] = x;
        this.bottomRight[1] = y;
        postInvalidate();
    }

    /**
     * Returns the top left coordinate as an integer array (dim: int[2]).
     *
     * @return the top left coordinate
     */
    public int[] getTopLeft() { return this.topLeft; }

    /**
     * Returns the bottom right coordinate as an integer array (dim: int[2]).
     *
     * @return the bottom right coordinate
     */
    public int[] getBottomRight() { return this.bottomRight; }

    /**
     * Concatenates the top left and bottom right coordinates into an array.
     * 
     * @return the top left and bottom right coordinates
     */
    public int[] getBbox() {
        int[] bbox = new int[topLeft.length + bottomRight.length];
        System.arraycopy(topLeft, 0, bbox, 0, topLeft.length);
        System.arraycopy(bottomRight, 0, bbox, topLeft.length, bottomRight.length);
        return bbox;
    }
}