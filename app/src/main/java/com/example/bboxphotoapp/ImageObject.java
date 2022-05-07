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
 *          int[];
 *          represents 2 points as cartesian coordinates to draw a bounding box;
 *          upper-left and bottom-right
 *
 *          Ex.: [15, 12, 144, 90]
 */
public class ImageObject {

    private static final String TAG = "ImageObject";
    private final int ROWS = 2; // the total # of rows within the bounding box
    private final int COLS = 2; // the total # of columns within the bounding box

    public String imageName; // display name of image
    public String imageUri; // uri of image
    public JSONObject imageJSON; // image object as a JSONObject

    // used for classification; defined by user
    public String imageClass; // classification name; default null
    public int[] imageBbox; // 2 points that draw a box; length 4; default null

    /**
     * Creates an image object.
     *
     * @param name the display name of a given image
     * @param uri the uri of a given image
     * @param className classification name of a given image
     * @param bbox bounding box cartesian coordinates of a given image (dim: int[4])
     */
    public ImageObject(String name, String uri, String className, int[] bbox) {
        this.imageName = name;
        this.imageUri = uri;
        this.imageClass = className;
        this.imageJSON = new JSONObject();

        // check bounding box dimension requirements; sets to null otherwise
        this.imageBbox = (bbox != null && bbox.length == 4) ? bbox : null;
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
     * @param bbox coordinates (must be 2 by 2)
     */
    public void setImageBbox(int[] bbox) {
        this.imageBbox = (bbox != null && bbox.length == 4) ? bbox : null;
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
    public int[] getImageBbox() { return this.imageBbox; }

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
        String str = "Name: " + this.imageName + "| Uri: " + this.imageUri;

        if(this.imageClass != null && this.imageBbox != null) {
            return str + "| Class Name: " + this.imageClass
                    + "| Bounding Box: " + Arrays.toString(this.imageBbox);
        }

        return str;
    }
}