package com.example.bboxphotoapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Image object.
 *
 * Stores information regarding an image with their corresponding:
 *
 *      uri: The uri path to the image
 *      class: The classification of the image
 *      bounding box: coordinates to the bounding box of the image
 *
 * Default image naming convention:
 *
 *          String;
 *          "yyyy.MM.dd HH:mm:ss"
 *
 *          Ex.: "2022.4.15 13:05:27"
 *
 * Bounding box standard dimensions:
 *
 *          int[][];
 *          represents 4 points as cartesian coordinates
 *
 *          Ex.: [[15,12],
 *                [15,90],
 *                [144, 12],
 *                [144, 90]]
 */
public class ImageObject {

    private static final String TAG = "ImageObject";

    public String imageName; // display name of image
    public String imageUri; // uri of image
    public JSONObject imageJSON; // image object as a JSONObject

    // used for classification; defined by user
    public String imageClass; // classification name; default null
    public int[][] imageBbox; // 4 points that draw a box; default null

    /**
     * Creates an image object.
     *
     * @param name the display name of a given image
     * @param uri the uri of a given image
     * @param className classification name of a given image
     * @param bbox bounding box cartesian coordinates of a given image (dim: (4, 2)))
     */
    public ImageObject(String name, String uri, String className, int[][] bbox) {
        this.imageName = name;
        this.imageUri = uri;
        this.imageClass = className;
        this.imageJSON = new JSONObject();

        // check bounding box dimension requirements; sets to null otherwise
        if(bbox != null) {
            int rows = bbox.length;
            int columns = bbox[0].length;
            this.imageBbox = (rows == 4 && columns == 2) ? bbox : null;
        }
    }

    /**
     * Sets the image name
     *
     * @param name the image name
     */
    public void setImageName(String name) { this.imageName = name; }

    /**
     * Sets the image uri
     *
     * @param uri the image uri
     */
    public void setImageUri(String uri) { this.imageUri = uri; }

    /**
     * Sets image classification name
     *
     * @param className the classification name
     */
    public void setImageClass(String className) { this.imageClass = className; }

    /**
     * Sets the bounding box coordinates of the image
     *
     * @param bbox coordinates (must be 4 by 2)
     */
    public void setImageBbox(int[][] bbox) {
        if(bbox != null) {
            int rows = bbox.length;
            int columns = bbox[0].length;
            this.imageBbox = (rows == 4 && columns == 2) ? bbox : null;
        }
    }

    /**
     * Sets the coordinate of an index in within the image bounding box.
     *
     * @param index the index of a bbox coordinate (0-3) to set
     * @param newCoordinate the new coordinates
     */
    public void setBboxCoordinate(int index, int[] newCoordinate) {
        this.imageBbox[index][0] = newCoordinate[0];
        this.imageBbox[index][1] = newCoordinate[1];
    }

    /**
     * Returns the image display name
     *
     * @return the image name
     */
    public String getImageName() { return this.imageName; }

    /**
     * Returns the image uri
     *
     * @return the image uri
     */
    public String getImageUri() { return this.imageUri; }

    /**
     * Returns image classification name
     *
     * @return the image class name
     */
    public String getImageClass() { return this.imageClass; }

    /**
     * Returns image bounding box coordinates as an integer array.
     *
     * @return the image bounding box coordinates
     */
    public int[][] getImageBbox() { return this.imageBbox; }

    /**
     * Appends field information to local JSONObject and returns it.
     *
     * @return the image JSON
     */
    public JSONObject getImageJSON() {
        try {
            JSONArray bbox = new JSONArray(this.imageBbox);
            this.imageJSON.put("name", this.imageName);
            this.imageJSON.put("uri", this.imageUri);
            this.imageJSON.put("class", this.imageClass);
            this.imageJSON.put("bbox", bbox);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return this.imageJSON;
    }

    @Override
    public String toString() {
        String toString = "Name: " + this.imageName + "| Uri: " + this.imageUri;

        if(this.imageClass != null && this.imageBbox != null) {
            return toString + "| Class Name: " + this.imageClass
                    + "| Bounding Box: " + Arrays.deepToString(this.imageBbox);
        }

        return toString;
    }
}