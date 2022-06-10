package com.example.bboxphotoapp;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ViewImageActivity extends AppCompatActivity {

    private static final String TAG = "ViewImageActivity";
    
    private RecyclerView imagesRecView;
    private ImageRecViewAdapter imageRecViewAdapter;
    private Button btnBack;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        // init back button
        btnBack = findViewById(R.id.btnBack);
        
        // init the activity recycler adapter view
        imagesRecView = findViewById(R.id.imagesRecView);
        
        // get JSONMain as an array of ImageObjects
        ArrayList<ImageObject> imageObjects = JSONManager.getImageObjectArray();
        
        // init recycler adapter with the ImageObjects array
        imageRecViewAdapter = new ImageRecViewAdapter(imageObjects);
        
        // set recycler adapter
        imagesRecView.setAdapter(imageRecViewAdapter);
        
        // controls the layout of the adapter.
        // horizontal: scroll left/right to see previous/next image
        imagesRecView.setLayoutManager(new LinearLayoutManager(
                this, 
                LinearLayoutManager.HORIZONTAL, 
                false));
        
        // set listener to end activity on click
        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // finishes activity if the phone 'back' button is pressed (not the app 'back' button)
        finish();
    }
}