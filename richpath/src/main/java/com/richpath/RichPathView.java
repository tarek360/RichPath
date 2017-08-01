package com.richpath;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.richpath.model.Vector;
import com.richpath.pathparser.PathParser;
import com.richpath.util.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by tarek on 6/29/17.
 */

public class RichPathView extends ImageView {

    private Vector vector;
    private RichPathDrawable richPathDrawable;

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

            richPathDrawable = new RichPathDrawable(vector);
            setImageDrawable(richPathDrawable);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (vector == null) return;

        int desiredWidth = (int) vector.getWidth();
        int desiredHeight = (int) vector.getHeight();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        //Measure Width
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }


    @Nullable
    public RichPath findRichPathByName(String name) {
        return richPathDrawable == null ? null : richPathDrawable.findRichPathByName(name);
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
        return richPathDrawable == null ? null : richPathDrawable.findFirstRichPath();
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
        return richPathDrawable == null ? null : richPathDrawable.findRichPathByIndex(index);
    }

    public void addPath(String path) {
        if (richPathDrawable != null) {
            richPathDrawable.addPath(PathParser.createPathFromPathData(path));
        }
    }

    public void addPath(Path path) {
        if (richPathDrawable != null) {
            richPathDrawable.addPath(path);
        }
    }

}
