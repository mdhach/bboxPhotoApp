package com.example.bboxphotoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnFloating;
    private PreviewView previewView;
    private CameraController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        btnFloating = findViewById(R.id.btnFloating);

        cc = new CameraController(this);
        cc.startCamera();

        // button action
        btnFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // placeholder button action
                Toast.makeText(MainActivity.this, "Placeholder action", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
