package com.example.bboxphotoapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnFloating;
    private ImageButton btnImage;
    private PreviewView previewView;
    private CameraController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
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

        try{
            // set preview image for edit button
            this.btnImage.setImageURI(getHeadImageUri());
        } catch(Exception e) {
            e.printStackTrace();
        }

        // initialize camera controller object
        cc = new CameraController(this);

        // floating button action
        btnFloating.setOnClickListener(view -> cc.takePhoto());
    }

    /**
     * Returns the URI for the first image in MediaStore
     *
     * @return image URI; null otherwise
     */
    private Uri getHeadImageUri() {
        Uri head = null;

        // columns to retrieve
        String[] projection = {MediaStore.Images.Media._ID};

        // the uri path to the primary external storage volume
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // how the query is sorted
        String sortOrder = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder);

        if(cursor != null) {
            cursor.moveToFirst();

            head = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    MediaStore.Images.Media._ID)));

            cursor.close();
        }
        return head;
    }
}
