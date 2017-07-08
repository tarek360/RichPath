package com.richpath;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.richpath.listener.OnRichPathUpdatedListener;
import com.richpath.model.Vector;
import com.richpath.pathparser.PathParser;
import com.richpath.util.Utils;
import com.richpath.util.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by tarek on 6/29/17.
 */

public class RichPathView extends View {

    private Vector vector;
    private int width;
    private int height;

    public RichPathView(Context context) {
        this(context, null);
    }

    public RichPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setupAttributes(attrs);
    }

    private void init() {
        //Disable hardware
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void setupAttributes(AttributeSet attrs) {

        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RichPathView, 0, 0);
        // Extract custom attributes into member variables
        int resID = -1;
        try {
            resID = a.getResourceId(R.styleable.RichPathView_vector, -1);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }

        if (resID != -1) {
            setVectorDrawable(resID);
        }

    }

    /**
     * Set a VectorDrawable resource ID.
     *
     * @param resId the resource ID for VectorDrawableCompat object.
     */
    public void setVectorDrawable(@DrawableRes int resId) {

        @SuppressWarnings("ResourceType")
        XmlResourceParser xpp = getContext().getResources().getXml(resId);
        vector = new Vector();
        try {
            XmlParser.parseVector(vector, xpp, getContext());
            listenToUpdatingPath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (vector == null) return;

        setMeasuredDimension((int) Utils.dpToPixel(getContext(), vector.getWidth()),
                (int) Utils.dpToPixel(getContext(), vector.getHeight()));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            width = w;
            height = h;
            mapPaths();
        }
    }

    void mapPaths() {
        if (vector == null) return;

        float centerX = width / 2;
        float centerY = height / 2;

        Matrix matrix = new Matrix();

        matrix.postTranslate(centerX - vector.getViewportWidth() / 2,
                centerY - vector.getViewportHeight() / 2);

        float widthRatio = width / vector.getViewportWidth();
        float heightRatio = height / vector.getViewportHeight();

        float ratio = Math.min(widthRatio, heightRatio);

        matrix.postScale(ratio, ratio, centerX, centerY);

        for (RichPath path : vector.paths) {
            path.mapToMatrix(matrix);
            path.scaleStrokeWidth(ratio);
        }

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

    public void listenToUpdatingPath() {
        if (vector == null) return;

        for (RichPath path : vector.paths) {
            path.setOnRichPathUpdatedListener(new OnRichPathUpdatedListener() {
                @Override
                public void onPathUpdated() {
                    invalidate();
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
                invalidate();
            }
        });
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (vector == null || vector.paths.size() < 0) return;

        for (RichPath path : vector.paths) {
            path.draw(canvas);
        }
    }

}
