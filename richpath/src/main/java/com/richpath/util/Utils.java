package com.richpath.util;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by tarek360 on 7/1/17.
 */

class Utils {

  static float dpToPixel(Context context, float dp) {
    return dp * context.getResources().getDisplayMetrics().density;
  }

  static float getDimenFromString(String value) {
    int end = value.charAt(value.length() - 3) == 'd' ? 3 : 2;
    return Float.parseFloat(value.substring(0, value.length() - end));
  }

  static int getColorFromString(String value) {
    int color = Color.TRANSPARENT;
    if (value.length() == 7 || value.length() == 9) {
      color = Color.parseColor(value);
    } else if (value.length() == 4) {
      color = Color.parseColor("#"
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(2)
          + value.charAt(2)
          + value.charAt(3)
          + value.charAt(3));
    } else if (value.length() == 2) {
      color = Color.parseColor("#"
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(1)
          + value.charAt(1));
    }
    return color;
  }
}
