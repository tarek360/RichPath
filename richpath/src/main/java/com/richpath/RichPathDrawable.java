package com.richpath;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.widget.ImageView.ScaleType;

import com.richpath.listener.OnRichPathUpdatedListener;
import com.richpath.model.Vector;
import com.richpath.pathparser.PathParser;
import com.richpath.util.PathUtils;

/**
 * Created by tarek on 6/29/17.
 */

class RichPathDrawable extends Drawable {

    private Vector vector;
    private int width;
    private int height;
    private ScaleType scaleType;

    public RichPathDrawable(Vector vector, ScaleType scaleType) {
        this.vector = vector;
        this.scaleType = scaleType;
        listenToPathsUpdates();

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds.width() > 0 && bounds.height() > 0) {
            width = bounds.width();
            height = bounds.height();
            mapPaths();

        }
    }

    void mapPaths() {
        if (vector == null) return;

        float centerX = width / 2;
        float centerY = height / 2;

        Matrix matrix = new Matrix();

        matrix.postTranslate(centerX - vector.getCurrentWidth() / 2,
                centerY - vector.getCurrentHeight() / 2);

        float widthRatio = width / vector.getCurrentWidth();
        float heightRatio = height / vector.getCurrentHeight();


        if (scaleType == ScaleType.FIT_XY) {
            matrix.postScale(widthRatio, heightRatio, centerX, centerY);
        } else {
            float ratio;
            if (width < height) {
                ratio = widthRatio;
            } else {
                ratio = heightRatio;
            }
            matrix.postScale(ratio, ratio, centerX, centerY);
        }

        float absWidthRatio = width / vector.getViewportWidth();
        float absHeightRatio = height / vector.getViewportHeight();
        float absRatio = Math.min(absWidthRatio, absHeightRatio);

        for (RichPath path : vector.paths) {
            path.mapToMatrix(matrix);
            path.scaleStrokeWidth(absRatio);
        }

        vector.setCurrentWidth(width);
        vector.setCurrentHeight(height);

    }

    @Nullable
    public RichPath findRichPathByName(String name) {
        if (vector == null) return null;

        for (RichPath path : vector.paths) {
            if (name.equals(path.getName())) {
                return path;
            }
        }
        return null;
    }

    /**
     * find the first {@link RichPath} or null if not found
     * <p>
     * This can be in handy if the vector consists of 1 path only
     *
     * @return the {@link RichPath} object found or null
     */
    @Nullable
    public RichPath findFirstRichPath() {
        return findRichPathByIndex(0);
    }

    /**
     * find {@link RichPath} by its index or null if not found
     * <p>
     * Note that the provided index must be the flattened index of the path
     * <p>
     * example:
     * <pre>
     * {@code <vector>
     *     <path> // index = 0
     *     <path> // index = 1
     *     <group>
     *          <path> // index = 2
     *          <group>
     *              <path> // index = 3
     *          </group>
     *      </group>
     *      <path> // index = 4
     *   </vector>}
     * </pre>
     *
     * @param index the flattened index of the path
     * @return the {@link RichPath} object found or null
     */
    @Nullable
    public RichPath findRichPathByIndex(@IntRange(from = 0) int index) {
        if (vector == null || index < 0 || index >= vector.paths.size()) return null;
        return vector.paths.get(index);
    }

    public void listenToPathsUpdates() {

        if (vector == null) return;

        for (RichPath path : vector.paths) {

            path.setOnRichPathUpdatedListener(new OnRichPathUpdatedListener() {
                @Override
                public void onPathUpdated() {
                    invalidateSelf();
                }
            });
        }

    }

    public void addPath(String path) {
        addPath(PathParser.createPathFromPathData(path));
    }

    public void addPath(Path path) {
        if (path instanceof RichPath) {
            addPath((RichPath) path);
        } else {
            addPath(new RichPath(path));
        }
    }

    private void addPath(RichPath path) {

        if (vector == null) return;

        vector.paths.add(path);
        path.setOnRichPathUpdatedListener(new OnRichPathUpdatedListener() {
            @Override
            public void onPathUpdated() {
                invalidateSelf();
            }
        });
        invalidateSelf();
    }

    boolean onTouchEvent(MotionEvent event) {

        if (vector == null) return false;

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_UP:

                for (int i = vector.paths.size() - 1; i >= 0; i--) {
                    RichPath richPath = vector.paths.get(i);
                    RichPath.OnPathClickListener onPathClickListener = richPath
                            .getOnPathClickListener();
                    if (onPathClickListener != null) {
                        if (PathUtils.isTouched(richPath, event.getX(), event.getY())) {
                            onPathClickListener.onClick();
                            return true;
                        }
                    }
                }
                break;
        }

        return true;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (vector == null || vector.paths.size() < 0) return;

        for (RichPath path : vector.paths) {
            path.draw(canvas);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
