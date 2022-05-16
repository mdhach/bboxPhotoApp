package com.example.bboxphotoapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewImageActivity extends AppCompatActivity {

    private static final String TAG = "ViewImageActivity";
    
    private RecyclerView imagesRecView;
    private ImageRecViewAdapter imageRecViewAdapter;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        
        imagesRecView = findViewById(R.id.imagesRecView);
        
        ArrayList<ImageObject> imageObjects = JSONManager.getImageObjectArray();
        
        imageRecViewAdapter = new ImageRecViewAdapter(imageObjects);
        
        imagesRecView.setAdapter(imageRecViewAdapter);
        
        imagesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}