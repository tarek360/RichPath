package com.richpath.util;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by tarek on 6/29/17.
 */

public class PathUtils {


    public static void resizePath(Path path, float width, float height) {
        RectF bounds = new RectF(0, 0, width, height);
        RectF src = new RectF();
        path.computeBounds(src, true);
        Matrix resizeMatrix = new Matrix();
        resizeMatrix.setRectToRect(src, bounds, Matrix.ScaleToFit.FILL);
        path.transform(resizeMatrix);
    }

    public static void setPathWidth(Path path, float width) {
        RectF src = new RectF();
        path.computeBounds(src, true);
        Matrix resizeMatrix = new Matrix();
        RectF bounds = new RectF(src.left, src.top, src.left + width, src.bottom);
        resizeMatrix.setRectToRect(src, bounds, Matrix.ScaleToFit.FILL);
        path.transform(resizeMatrix);
    }


    public static void setPathHeight(Path path, float height) {
        RectF src = new RectF();
        path.computeBounds(src, true);
        Matrix resizeMatrix = new Matrix();
        RectF bounds = new RectF(src.left, src.top, src.right, src.top + height);
        resizeMatrix.setRectToRect(src, bounds, Matrix.ScaleToFit.FILL);
        path.transform(resizeMatrix);
    }


    public static float getPathWidth(Path path) {
        RectF rect = new RectF();
        path.computeBounds(rect, true);
        return rect.width();
    }

    public static float getPathHeight(Path path) {
        RectF rect = new RectF();
        path.computeBounds(rect, true);
        return rect.height();
    }

    public static void setPathRotation(Path path, float rotation) {
        RectF rect = new RectF();
        path.computeBounds(rect, true);
        setPathRotation(path, rotation, rect.centerX(), rect.centerY());
    }

    public static void setPathRotation(Path path, float rotation, float px, float py) {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation, px, py);
        path.transform(matrix);
    }

    public static void setPathTranslationX(Path path, float translationX) {
        Matrix matrix = new Matrix();
        matrix.postTranslate(translationX, 0);
        path.transform(matrix);
    }

    public static void setPathTranslationY(Path path, float translationY) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(0, translationY);
        path.transform(matrix);
    }

    public static void setPathScaleX(Path path, float scaleX) {
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, 1);
        path.transform(matrix);
    }

    public static void setPathScaleY(Path path, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.setScale(1, scaleY);
        path.transform(matrix);
    }
}
