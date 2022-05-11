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

public class SpinnerAdapter extends ArrayAdapter<String> {

    private static final String TAG = "SpinnerAdapter";
    
    private Context context;
    private List<String> list;
    
    private String className;
    
    private TextView spnItemName;
    private ImageView spnItemDel;
    
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        init(context, objects);
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
    
    private void init(@NonNull Context context, @NonNull List<String> objects) {
        this.context = context;
        this.list = objects;
    }

    public View getCustomDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_spinner, parent, false);

        String text = list.get(position);

        spnItemName = rowView.findViewById(R.id.spnItemName);
        spnItemDel = rowView.findViewById(R.id.spnItemDel);
        
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