package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Custom ArrayAdapter for the spinner object in the main activity.
 * 
 * Allows the user to select, create, or delete classification IDs.
 * 
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    private static final String TAG = "SpinnerAdapter";
    
    private List<String> list;
    
    private String className;
    
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        init(objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomDropDownView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        className = list.get(position);
        return super.getView(position, convertView, parent);
    }
    
    private void init(@NonNull List<String> objects) {
        this.list = objects;
    }

    public View getCustomDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_spinner, parent, false);

        String text = list.get(position);

        TextView spnItemName = rowView.findViewById(R.id.spnItemName);
        ImageView spnItemDel = rowView.findViewById(R.id.spnItemDel);
        
        spnItemName.setText(text);
        
        spnItemDel.setOnClickListener(view -> {
            if(!list.isEmpty()) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
    
    public String getClassName() {
        return this.className;
    }
}