package com.richpath.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.richpath.RichPath;
import com.richpath.model.Group;
import com.richpath.model.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by tarek on 6/30/17.
 */

public class XmlParser {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    public static void parseVector(Vector vector, XmlResourceParser xpp, Context context)
            throws IOException, XmlPullParserException {

        Stack<Group> groupStack = new Stack<>();

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            String tagName = xpp.getName();

            if (eventType == XmlPullParser.START_TAG) {

                switch (tagName) {

                    case Vector.TAG_NAME:
                        parseVectorElement(vector, xpp, context);
                        break;

                    case Group.TAG_NAME:
                        Group group = parseGroupElement(context, xpp);
                        if (!groupStack.empty()) {
                            group.scale(groupStack.peek().matrix());
                        }
                        groupStack.push(group);
                        break;

                    case RichPath.TAG_NAME:
                        RichPath path = parsePathElement(context, xpp);
                        if (!groupStack.empty()) {
                            path.applyGroup(groupStack.peek());
                        }
                        vector.paths.add(path);
                        break;
                }

            } else if (eventType == XmlPullParser.END_TAG) {
                if (Group.TAG_NAME.equals(tagName)) {
                    if (!groupStack.empty()) {
                        groupStack.pop();
                    }
                }
            }

            eventType = xpp.next();
        }

        xpp.close();
    }

    private static void parseVectorElement(Vector vector, XmlResourceParser xpp, Context context) {
        vector.inflate(xpp, context);
    }

    private static Group parseGroupElement(Context context, XmlResourceParser xpp) {
        return new Group(context, xpp);
    }

    private static RichPath parsePathElement(Context context, XmlResourceParser xpp) {
        String pathData = getAttributeString(context, xpp, "pathData", null);
        RichPath path = new RichPath(pathData);
        path.inflate(context, xpp);
        return path;
    }

    public static String getAttributeString(Context context, XmlResourceParser xpp, String attributeName, String defValue) {

        int resourceId = getAttributeResourceValue(xpp, attributeName);
        String value;
        if (resourceId != -1) {
            value = context.getString(resourceId);
        } else {
            value = getAttributeValue(xpp, attributeName);
        }
        return value != null ? value : defValue;
    }

    public static float getAttributeFloat(XmlResourceParser xpp, String attributeName, float defValue) {
        String value = getAttributeValue(xpp, attributeName);
        return value != null ? Float.parseFloat(value) : defValue;
    }

    public static float getAttributeDimen(Context context, XmlResourceParser xpp, String attributeName, float defValue) {
        String value = getAttributeValue(xpp, attributeName);
        float dp = Utils.dpToPixel(context, Utils.getDimenFromString(value));
        return value != null ? dp : defValue;
    }


    public static boolean getAttributeBoolean(XmlResourceParser xpp, String attributeName, boolean defValue) {
        String value = getAttributeValue(xpp, attributeName);
        return value != null ? Boolean.parseBoolean(value) : defValue;
    }

    public static int getAttributeInt(XmlResourceParser xpp, String attributeName, int defValue) {
        String value = getAttributeValue(xpp, attributeName);
        return value != null ? Integer.parseInt(value) : defValue;
    }

    public static int getAttributeColor(Context context, XmlResourceParser xpp, String attributeName, int defValue) {
        int resourceId = getAttributeResourceValue(xpp, attributeName);
        if (resourceId != -1) {
            return ContextCompat.getColor(context, resourceId);
        }

        String value = getAttributeValue(xpp, attributeName);
        return value != null ? Utils.getColorFromString(value) : defValue;
    }


    public static Paint.Cap getAttributeStrokeLineCap(XmlResourceParser xpp, String attributeName, Paint.Cap defValue) {
        String value = getAttributeValue(xpp, attributeName);
        return value != null ? getStrokeLineCap(Integer.parseInt(value), defValue) : defValue;
    }

    public static Paint.Join getAttributeStrokeLineJoin(XmlResourceParser xpp, String attributeName, Paint.Join defValue) {
        String value = getAttributeValue(xpp, attributeName);
        return value != null ? getStrokeLineJoin(Integer.parseInt(value), defValue) : defValue;
    }

    public static Path.FillType getAttributePathFillType(XmlResourceParser xpp, String attributeName, Path.FillType defValue) {
        String value = getAttributeValue(xpp, attributeName);
        return value != null ? getPathFillType(Integer.parseInt(value), defValue) : defValue;
    }

    @Nullable
    private static String getAttributeValue(XmlResourceParser xpp, String attributeName) {
        return xpp.getAttributeValue(NAMESPACE, attributeName);
    }

    private static int getAttributeResourceValue(XmlResourceParser xpp, String attributeName) {
        return xpp.getAttributeResourceValue(NAMESPACE, attributeName, -1);
    }

    private static Paint.Cap getStrokeLineCap(int id, Paint.Cap defValue) {
        switch (id) {
            case 0:
                return Paint.Cap.BUTT;
            case 1:
                return Paint.Cap.ROUND;
            case 2:
                return Paint.Cap.SQUARE;
            default:
                return defValue;
        }
    }

    private static Paint.Join getStrokeLineJoin(int id, Paint.Join defValue) {
        switch (id) {
            case 0:
                return Paint.Join.MITER;
            case 1:
                return Paint.Join.ROUND;
            case 2:
                return Paint.Join.BEVEL;
            default:
                return defValue;
        }
    }

    private static Path.FillType getPathFillType(int id, Path.FillType defValue) {
        switch (id) {
            case 0:
                return Path.FillType.WINDING;
            case 1:
                return Path.FillType.EVEN_ODD;
            case 2:
                return Path.FillType.INVERSE_WINDING;
            case 3:
                return Path.FillType.INVERSE_EVEN_ODD;
            default:
                return defValue;
        }
    }

}
