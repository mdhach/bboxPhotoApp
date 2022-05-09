package com.example.bboxphotoapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClassNameManager {

    private static final String TAG = "ClassNameManager";
    
    private List<String> classList;
    private final int minSize = 2;

    public ClassNameManager() {
        classList = new ArrayList<>();
        addValue("Add Class");
    }
    
    public void addValue(String value) {
        if(!classList.contains(value)) {
            classList.add(value);
        }
    }

    public void removeValue(String value) {
        if(classList.size() > minSize) {
            if(classList.contains(value)) {
                classList.remove(value);
            }
        }
    }

    public void setValue(String oldValue, String newValue) {
        if(classList.contains(oldValue)) {
            int idx = classList.indexOf(oldValue);
            classList.set(idx, newValue);
        }
    }

    public ArrayAdapter<String> getArrayAdapter(Context context) {
        // create ArrayAdapter<String> for the class names and set the layout resource
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
