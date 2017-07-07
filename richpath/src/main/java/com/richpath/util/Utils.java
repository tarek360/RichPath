package com.richpath.util;

import android.content.Context;

/**
 * Created by tarek on 7/1/17.
 */

public class Utils {

    public static float dpToPixel(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float getDimenFromString(String value) {
        int end = value.charAt(value.length() - 3) == 'd' ? 3 : 2;
        return Float.parseFloat(value.substring(0, value.length() - end));
    }
}
