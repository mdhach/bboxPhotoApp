package com.example.bboxphotoapp;

public final class ImageMetaData {
    public String displayName;
    public long size;
    public String mimeType;
    public String path;

    public String toString() {
        return "name: " + displayName
                + " ; size: " + size
                + " ; path: " + path
                + " ; mime: " + mimeType;
    }
}
