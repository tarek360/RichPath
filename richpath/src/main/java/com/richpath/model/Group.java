package com.richpath.model;

import android.content.res.XmlResourceParser;
import android.graphics.Matrix;

import com.richpath.util.XmlParser;


/**
 * Created by tarek on 6/30/17.
 */

public class Group {

    public final static String TAG_NAME = "group";


    private float rotation = 0;
    private float pivotX = 0;
    private float pivotY = 0;
    private float scaleX = 1;
    private float scaleY = 1;
    private float translateX = 0;
    private float translateY = 0;
    private String name;
    private Matrix matrix;

    public Group(XmlResourceParser xpp) {
        inflate(xpp);
    }

    public float getRotation() {
        return rotation;
    }

    public float getPivotX() {
        return pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getTranslateX() {
        return translateX;
    }

    public float getTranslateY() {
        return translateY;
    }

    public String getName() {
        return name;
    }

    private void inflate(XmlResourceParser xpp) {

        name = XmlParser.getAttributeString(xpp, "name", name);

        rotation = XmlParser.getAttributeFloat(xpp, "rotation", rotation);

        scaleX = XmlParser.getAttributeFloat(xpp, "scaleX", scaleX);

        scaleY = XmlParser.getAttributeFloat(xpp, "scaleY", scaleY);

        translateX = XmlParser.getAttributeFloat(xpp, "translateX", translateX);

        translateY = XmlParser.getAttributeFloat(xpp, "translateY", translateY);

        pivotX = XmlParser.getAttributeFloat(xpp, "pivotX", pivotX) + translateX;

        pivotY = XmlParser.getAttributeFloat(xpp, "pivotY", pivotY) + translateY;

        matrix();
    }

    public Matrix matrix() {
        if (matrix == null) {
            matrix = new Matrix();
            matrix.postScale(scaleX, scaleY, pivotX, pivotY);
            matrix.postRotate(rotation, pivotX, pivotY);
            matrix.postTranslate(translateX, translateY);
        }
        return matrix;
    }

    public void scale(Matrix matrix) {
        matrix().postConcat(matrix);
    }
}
