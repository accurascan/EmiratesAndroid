package com.inet.facelock.callback;

import android.graphics.Point;
import android.graphics.Rect;

public class FaceDetectionResult {
    Rect   faceRect;
    float  confidence;
    int		newWidth;
    int		newHeight;
    byte[]	newImg;
    float[] feature;

    public int getNewWidth() {
        return newWidth;
    }

    public void setNewWidth(int newWidth) {
        this.newWidth = newWidth;
    }

    public int getNewHeight() {
        return newHeight;
    }

    public void setNewHeight(int newHeight) {
        this.newHeight = newHeight;
    }

    public byte[] getNewImg() {
        return newImg;
    }

    public void setNewImg(byte[] newImg) {
        this.newImg = newImg;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public Rect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(int left, int top, int right, int bottom) {
        this.faceRect = new Rect(left, top,right,bottom);
    }

    public float[] getFeature() {
        return feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }

}
