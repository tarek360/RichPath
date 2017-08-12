package com.richpath;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import com.richpath.listener.OnRichPathUpdatedListener;
import com.richpath.model.Group;
import com.richpath.pathparser.PathDataNode;
import com.richpath.pathparser.PathParser;
import com.richpath.pathparser.PathParserCompat;
import com.richpath.util.PathUtils;
import com.richpath.util.XmlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tarek on 6/29/17.
 */

public class RichPath extends Path {

    public final static String TAG_NAME = "path";

    private int fillColor = Color.TRANSPARENT;
    private int strokeColor = Color.TRANSPARENT;
    private float fillAlpha = 1.0f;
    private float strokeAlpha = 1.0f;
    private float strokeWidth = 0;
    private float trimPathStart = 0;
    private float trimPathEnd = 1;
    private float trimPathOffset = 0;

    private Paint.Cap strokeLineCap = Paint.Cap.BUTT;
    private Paint.Join strokeLineJoin = Paint.Join.MITER;

    private float strokeMiterLimit = 4;

    private String name;
    private Paint paint;
    private float rotation;
    private float scaleX = 1;
    private float scaleY = 1;
    private float translationX;
    private float translationY;
    private float originalWidth;
    private float originalHeight;

    private float pivotX = 0;
    private float pivotY = 0;
    private boolean pivotToCenter = false;

    private OnRichPathUpdatedListener onRichPathUpdatedListener;

    private PathMeasure pathMeasure;

    private Path originalPath;

    private PathDataNode[] pathDataNodes;
    private List<Matrix> matrices;

    private OnPathClickListener onPathClickListener;

    public RichPath(String pathData) {
        this(PathParser.createPathFromPathData(pathData));
    }

    public RichPath(Path src) {
        super(src);
        originalPath = src;
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        matrices = new ArrayList<>();
        updateOriginalDimens();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWidth(float width) {
        PathUtils.setPathWidth(this, width);
        PathUtils.setPathWidth(originalPath, width);
        onPathUpdated();
    }

    public float getWidth() {
        return PathUtils.getPathWidth(this);
    }

    public void setHeight(float height) {
        PathUtils.setPathHeight(this, height);
        PathUtils.setPathHeight(originalPath, height);
        onPathUpdated();
    }

    public float getHeight() {
        return PathUtils.getPathHeight(this);
    }

    void setOnRichPathUpdatedListener(OnRichPathUpdatedListener onRichPathUpdatedListener) {
        this.onRichPathUpdatedListener = onRichPathUpdatedListener;
    }

    public OnRichPathUpdatedListener getOnRichPathUpdatedListener() {
        return onRichPathUpdatedListener;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {

        float deltaValue = rotation - this.rotation;
        if (pivotToCenter) {
            PathUtils.setPathRotation(this, deltaValue);
            PathUtils.setPathRotation(originalPath, deltaValue);
        } else {
            PathUtils.setPathRotation(this, deltaValue, pivotX, pivotY);
            PathUtils.setPathRotation(originalPath, deltaValue, pivotX, pivotY);
        }
        this.rotation = rotation;
        onPathUpdated();
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {

        if (pivotToCenter) {
            //reset scaling
            PathUtils.setPathScaleX(this, 1.0f / this.scaleX);
            PathUtils.setPathScaleX(originalPath, 1.0f / this.scaleX);
            //new scaling
            PathUtils.setPathScaleX(this, scaleX);
            PathUtils.setPathScaleX(originalPath, scaleX);
        } else {
            //reset scaling
            PathUtils.setPathScaleX(this, 1.0f / this.scaleX, pivotX, pivotY);
            PathUtils.setPathScaleX(originalPath, 1.0f / this.scaleX, pivotX, pivotY);
            //new scaling
            PathUtils.setPathScaleX(this, scaleX, pivotX, pivotY);
            PathUtils.setPathScaleX(originalPath, scaleX, pivotX, pivotY);
        }
        this.scaleX = scaleX;
        onPathUpdated();
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {

        if (pivotToCenter) {
            //reset scaling
            PathUtils.setPathScaleY(this, 1.0f / this.scaleY);
            PathUtils.setPathScaleY(originalPath, 1.0f / this.scaleY);
            //new scaling
            PathUtils.setPathScaleY(this, scaleY);
            PathUtils.setPathScaleY(originalPath, scaleY);
        } else {
            //reset scaling
            PathUtils.setPathScaleY(this, 1.0f / this.scaleY, pivotX, pivotY);
            PathUtils.setPathScaleY(originalPath, 1.0f / this.scaleY, pivotX, pivotY);
            //new scaling
            PathUtils.setPathScaleY(this, scaleY, pivotX, pivotY);
            PathUtils.setPathScaleY(originalPath, scaleY, pivotX, pivotY);
        }
        this.scaleY = scaleY;
        onPathUpdated();
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        PathUtils.setPathTranslationX(this, translationX - this.translationX);
        PathUtils.setPathTranslationX(originalPath, translationX - this.translationX);
        this.translationX = translationX;
        onPathUpdated();
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        PathUtils.setPathTranslationY(this, translationY - this.translationY);
        PathUtils.setPathTranslationY(originalPath, translationY - this.translationY);
        this.translationY = translationY;
        onPathUpdated();
    }

    public float getOriginalWidth() {
        return originalWidth;
    }

    public float getOriginalHeight() {
        return originalHeight;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        onPathUpdated();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        onPathUpdated();
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        onPathUpdated();
    }

    public float getStrokeAlpha() {
        return strokeAlpha;
    }

    public void setStrokeAlpha(float strokeAlpha) {
        this.strokeAlpha = strokeAlpha;
        onPathUpdated();
    }

    public float getFillAlpha() {
        return fillAlpha;
    }

    public void setFillAlpha(float fillAlpha) {
        this.fillAlpha = fillAlpha;
        onPathUpdated();
    }

    public float getTrimPathStart() {
        return trimPathStart;
    }

    public void setTrimPathStart(float trimPathStart) {
        this.trimPathStart = trimPathStart;
        trim();
        onPathUpdated();
    }

    public float getTrimPathEnd() {
        return trimPathEnd;
    }

    public void setTrimPathEnd(float trimPathEnd) {
        this.trimPathEnd = trimPathEnd;
        trim();
        onPathUpdated();
    }

    public float getTrimPathOffset() {
        return trimPathOffset;
    }

    public void setTrimPathOffset(float trimPathOffset) {
        this.trimPathOffset = trimPathOffset;
        trim();
        onPathUpdated();
    }

    public Paint.Cap getStrokeLineCap() {
        return strokeLineCap;
    }

    public void setStrokeLineCap(Paint.Cap strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
        onPathUpdated();
    }

    public Paint.Join getStrokeLineJoin() {
        return strokeLineJoin;
    }

    public void setStrokeLineJoin(Paint.Join strokeLineJoin) {
        this.strokeLineJoin = strokeLineJoin;
        onPathUpdated();
    }

    public float getStrokeMiterLimit() {
        return strokeMiterLimit;
    }

    public void setStrokeMiterLimit(float strokeMiterLimit) {
        this.strokeMiterLimit = strokeMiterLimit;
        onPathUpdated();
    }

    public float getPivotX() {
        return pivotX;
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
    }

    public boolean isPivotToCenter() {
        return pivotToCenter;
    }

    public void setPivotToCenter(boolean pivotToCenter) {
        this.pivotToCenter = pivotToCenter;
    }

    void draw(Canvas canvas) {

        paint.setColor(applyAlpha(fillColor, fillAlpha));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(this, paint);

        paint.setColor(applyAlpha(strokeColor, strokeAlpha));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(this, paint);

    }

    public void applyGroup(Group group) {
        mapToMatrix(group.matrix());
        pivotX = group.getPivotX();
        pivotY = group.getPivotY();
    }

    void mapToMatrix(Matrix matrix) {
        matrices.add(matrix);
        transform(matrix);
        originalPath.transform(matrix);
        mapPoints(matrix);
        updateOriginalDimens();
    }

    private void mapPoints(Matrix matrix) {
        float[] src = {pivotX, pivotY};
        matrix.mapPoints(src);
        pivotX = src[0];
        pivotY = src[1];
    }

    void scaleStrokeWidth(float scale) {
        paint.setStrokeWidth(strokeWidth * scale);
    }

    public void setPathData(String pathData) {
        setPathDataNodes(PathParserCompat.createNodesFromPathData(pathData));
    }

    public PathDataNode[] getPathDataNodes() {
        return pathDataNodes;
    }

    public void setPathDataNodes(PathDataNode[] pathDataNodes) {
        PathUtils.setPathDataNodes(this, pathDataNodes);
        this.pathDataNodes = pathDataNodes;

        for (Matrix matrix : matrices) {
            transform(matrix);
        }

        onPathUpdated();
    }

    public void inflate(Context context, XmlResourceParser xpp) {

        String pathData = XmlParser.getAttributeString(context, xpp, "pathData", name);

        pathDataNodes = PathParserCompat.createNodesFromPathData(pathData);

        name = XmlParser.getAttributeString(context, xpp, "name", name);

        fillAlpha = XmlParser.getAttributeFloat(xpp, "fillAlpha", fillAlpha);

        fillColor = XmlParser.getAttributeColor(context, xpp, "fillColor", fillColor);

        strokeAlpha = XmlParser.getAttributeFloat(xpp, "strokeAlpha", strokeAlpha);

        strokeColor = XmlParser.getAttributeColor(context, xpp, "strokeColor", strokeColor);

        strokeLineCap = XmlParser.getAttributeStrokeLineCap(xpp, "strokeLineCap", strokeLineCap);

        strokeLineJoin = XmlParser.getAttributeStrokeLineJoin(xpp, "strokeLineJoin", strokeLineJoin);

        strokeMiterLimit = XmlParser.getAttributeFloat(xpp, "strokeMiterLimit", strokeMiterLimit);

        strokeWidth = XmlParser.getAttributeFloat(xpp, "strokeWidth", strokeWidth);

        trimPathStart = XmlParser.getAttributeFloat(xpp, "trimPathStart", trimPathStart);

        trimPathEnd = XmlParser.getAttributeFloat(xpp, "trimPathEnd", trimPathEnd);

        trimPathOffset = XmlParser.getAttributeFloat(xpp, "trimPathOffset", trimPathOffset);

        setFillType(XmlParser.getAttributePathFillType(xpp, "fillType", getFillType()));

        updatePaint();

        trim();
    }

    private void updateOriginalDimens() {
        originalWidth = PathUtils.getPathWidth(this);
        originalHeight = PathUtils.getPathHeight(this);
    }

    private void trim() {

        if (trimPathStart != 0.0f || trimPathEnd != 1.0f) {
            float start = (trimPathStart + trimPathOffset) % 1.0f;
            float end = (trimPathEnd + trimPathOffset) % 1.0f;

            if (pathMeasure == null) {
                pathMeasure = new PathMeasure();
            }
            pathMeasure.setPath(originalPath, false);

            float len = pathMeasure.getLength();
            start = start * len;
            end = end * len;
            reset();
            if (start > end) {
                pathMeasure.getSegment(start, len, this, true);
                pathMeasure.getSegment(0f, end, this, true);
            } else {
                pathMeasure.getSegment(start, end, this, true);
            }
            rLineTo(0, 0); // fix bug in measure
        }
    }

    private void updatePaint() {

        paint.setStrokeCap(strokeLineCap);

        paint.setStrokeJoin(strokeLineJoin);

        paint.setStrokeMiter(strokeMiterLimit);

        paint.setStrokeWidth(strokeWidth);

        //todo fillType

    }

    private void onPathUpdated() {
        if (onRichPathUpdatedListener != null) {
            onRichPathUpdatedListener.onPathUpdated();
        }
    }

    private int applyAlpha(int color, float alpha) {
        int alphaBytes = Color.alpha(color);
        color &= 0x00FFFFFF;
        color |= ((int) (alphaBytes * alpha)) << 24;
        return color;
    }

    public void setOnPathClickListener(OnPathClickListener onPathClickListener) {
        this.onPathClickListener = onPathClickListener;
    }

    public OnPathClickListener getOnPathClickListener() {
        return onPathClickListener;
    }

    public interface OnPathClickListener {
        void onClick();
    }
}
