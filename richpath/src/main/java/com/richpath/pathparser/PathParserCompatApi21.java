package com.richpath.pathparser;

import android.graphics.Path;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tarek on 6/27/17.
 */

class PathParserCompatApi21 {

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    static Path createPathFromPathData(String pathData) {

        try {
            Method method = getCreatePathFromPathDataMethod();
            if (method != null) {
                Object object = method.invoke(null, pathData);
                return (Path) object;
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method getCreatePathFromPathDataMethod() {
        try {
            return Class.forName("android.util.PathParser")
                    .getDeclaredMethod("createPathFromPathData", String.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
