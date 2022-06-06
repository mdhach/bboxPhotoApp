package com.example.bboxphotoapp;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageRecViewAdapter extends RecyclerView.Adapter<ImageRecViewAdapter.ViewHolder> {

    private static final String TAG = "ImageRecViewAdapter";
    
    private ArrayList<ImageObject> imageObjects;

    /**
     * Constructor. Allows user to initialize the ImageObject ArrayList
     * via argument.
     *
     */
    public ImageRecViewAdapter(ArrayList<ImageObject> imageObjects) {
        this.imageObjects = imageObjects;
    }

    /**
     * This inner ViewHolder class is responsible for creating a view for each image item.
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private LinearLayout parent;
        private TextView name;
        private TextView className;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageListItem);
            parent = itemView.findViewById(R.id.imageListParent);
            name = itemView.findViewById(R.id.imageListName);
            className = itemView.findViewById(R.id.imageListClass);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();
        View view = inflater.inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // current image object
        ImageObject imageObject = imageObjects.get(position);
        
        // retrieve image metadata
        String name = "File Name: " + imageObject.getImageName();
        String className = "Class Name: " + imageObject.getImageClass();
        Uri uri = Uri.parse(imageObject.getImageUri());
        
        // set image metadata in recycler adapter
        holder.image.setImageURI(uri);
        holder.name.setText(name);
        holder.className.setText(className);
    }

    @Override
    public int getItemCount() {
        return this.imageObjects.size();
    }
    
    public void setImageObjectArrayList(ArrayList<ImageObject> imageObjects) {
        this.imageObjects = imageObjects;
        notifyDataSetChanged();
    }
}