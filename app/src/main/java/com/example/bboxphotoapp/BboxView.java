package com.example.bboxphotoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class BboxView extends View {

    private static final String TAG = "BboxView";

    private Paint paint;
    private Rect rect;

    private int strokeWidth = 3;

    // bbox dimensions
    private int[] topLeft = new int[2];
    private int[] bottomRight = new int[2];

    public BboxView(Context context) {
        super(context);

        init(null);
    }

    public BboxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public BboxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public BboxView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

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

    public void update() {
//        paint.setColor(paint.getColor() == Color.BLACK ? Color.RED : Color.BLACK);

        // calls onDraw() method
        postInvalidate();
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

    public void setTopLeft(int x, int y) {
        this.topLeft[0] = x;
        this.topLeft[1] = y;
    }

    public void setBottomRight(int x, int y) {
        this.bottomRight[0] = x;
        this.bottomRight[1] = y;
    }

    public int[] getTopLeft() { return this.topLeft; }

    public int[] getBottomRight() { return this.bottomRight; }
}