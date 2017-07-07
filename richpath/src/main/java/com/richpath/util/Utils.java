package com.richpath.util;

/**
 * Created by tarek on 7/1/17.
 */

public class Utils {

    public static float getDimenFromString(String value) {
        int end = value.charAt(value.length() - 3) == 'd' ? 3 : 2;
        return Float.parseFloat(value.substring(0, value.length() - end));
    }
}
